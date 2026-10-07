package dev.ironjungle.entity;

import dev.ironjungle.item.PantherArmorItem;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Ручная боевая пантера.
 * - Приручается сырой рыбой (шанс 1/4).
 * - ПКМ хозяина пустой рукой (или не едой): сидеть / за мной.
 * - Еда лечит; при полном здоровье — размножение.
 * - ПКМ бронёй для пантеры: надеть. ПКМ ножницами: снять.
 */
public class PantherEntity extends TameableEntity {
	private static final double WILD_HEALTH = 20.0;
	private static final double TAMED_HEALTH = 40.0;
	private static final double WILD_DAMAGE = 5.0;
	private static final double TAMED_DAMAGE = 7.0;
	private static final int TAME_CHANCE = 4; // 1 из 4

	public PantherEntity(EntityType<? extends TameableEntity> type, World world) {
		super(type, world);
		// Броня всегда выпадает целой, если пантера погибнет
		this.setEquipmentDropChance(EquipmentSlot.BODY, 2.0f);
	}

	public static DefaultAttributeContainer.Builder createPantherAttributes() {
		return MobEntity.createMobAttributes()
				.add(EntityAttributes.GENERIC_MAX_HEALTH, WILD_HEALTH)
				.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35)
				.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, WILD_DAMAGE)
				.add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0);
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(1, new SwimGoal(this));
		this.goalSelector.add(2, new SitGoal(this));
		this.goalSelector.add(3, new PounceAtTargetGoal(this, 0.4f));
		this.goalSelector.add(4, new MeleeAttackGoal(this, 1.3, true));
		this.goalSelector.add(5, new FollowOwnerGoal(this, 1.1, 10.0f, 2.0f));
		this.goalSelector.add(6, new AnimalMateGoal(this, 1.0));
		this.goalSelector.add(7, new TemptGoal(this, 0.8, PantherEntity::isTamingItem, true));
		this.goalSelector.add(8, new WanderAroundFarGoal(this, 1.0));
		this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
		this.goalSelector.add(10, new LookAroundGoal(this));

		this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
		this.targetSelector.add(2, new AttackWithOwnerGoal(this));
		this.targetSelector.add(3, new RevengeGoal(this).setGroupRevenge());
		this.targetSelector.add(4, new UntamedActiveTargetGoal<>(this, ChickenEntity.class, false, null));
		this.targetSelector.add(4, new UntamedActiveTargetGoal<>(this, RabbitEntity.class, false, null));
	}

	/** Чем приручать: сырая рыба. */
	public static boolean isTamingItem(ItemStack stack) {
		return stack.isOf(Items.COD) || stack.isOf(Items.SALMON);
	}

	/** Чем кормить: рыба и мясо, сырые и жареные. */
	public static boolean isFood(ItemStack stack) {
		return isTamingItem(stack)
				|| stack.isOf(Items.COOKED_COD) || stack.isOf(Items.COOKED_SALMON)
				|| stack.isOf(Items.BEEF) || stack.isOf(Items.COOKED_BEEF)
				|| stack.isOf(Items.PORKCHOP) || stack.isOf(Items.COOKED_PORKCHOP)
				|| stack.isOf(Items.MUTTON) || stack.isOf(Items.COOKED_MUTTON)
				|| stack.isOf(Items.CHICKEN) || stack.isOf(Items.COOKED_CHICKEN);
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return isFood(stack);
	}

	public ItemStack getArmor() {
		return this.getEquippedStack(EquipmentSlot.BODY);
	}

	public boolean hasArmor() {
		return !this.getArmor().isEmpty();
	}

	/** Сообщение хозяину над хотбаром (только на сервере, чтобы не дублировалось). */
	private void tell(PlayerEntity player, String key) {
		if (!this.getWorld().isClient) {
			player.sendMessage(Text.translatable(key), true);
		}
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack stack = player.getStackInHand(hand);
		World world = this.getWorld();

		if (this.isTamed()) {
			if (!this.isOwner(player)) {
				return super.interactMob(player, hand);
			}

			// Надеть броню
			if (stack.getItem() instanceof PantherArmorItem) {
				if (this.hasArmor()) {
					tell(player, "message.ironjungle.panther.armor_already");
					return ActionResult.success(world.isClient);
				}
				if (!world.isClient) {
					this.equipStack(EquipmentSlot.BODY, stack.copyWithCount(1));
					stack.decrementUnlessCreative(1, player);
					this.playSound(SoundEvents.BLOCK_CHAIN_PLACE, 1.0f, 0.8f);
				}
				tell(player, "message.ironjungle.panther.armor_on");
				return ActionResult.success(world.isClient);
			}

			// Снять броню ножницами
			if (stack.isOf(Items.SHEARS) && this.hasArmor()) {
				if (!world.isClient) {
					this.dropStack(this.getArmor().copy());
					this.equipStack(EquipmentSlot.BODY, ItemStack.EMPTY);
					this.playSound(SoundEvents.ENTITY_SHEEP_SHEAR, 1.0f, 1.0f);
				}
				tell(player, "message.ironjungle.panther.armor_off");
				return ActionResult.success(world.isClient);
			}

			// Еда
			if (isFood(stack)) {
				if (this.getHealth() < this.getMaxHealth()) {
					stack.decrementUnlessCreative(1, player);
					this.heal(8.0f);
					return ActionResult.success(world.isClient);
				}
				// Здорова: размножение (обрабатывает AnimalEntity)
				return super.interactMob(player, hand);
			}

			// Всё остальное (пустая рука, меч и т.д.): сидеть / за мной
			this.setSitting(!this.isSitting());
			this.jumping = false;
			this.navigation.stop();
			this.setTarget(null);
			tell(player, this.isSitting()
					? "message.ironjungle.panther.sit"
					: "message.ironjungle.panther.follow");
			return ActionResult.success(world.isClient);
		}

		// Приручение дикой пантеры
		if (isTamingItem(stack)) {
			stack.decrementUnlessCreative(1, player);
			if (!world.isClient) {
				if (this.random.nextInt(TAME_CHANCE) == 0) {
					tameBy(player);
					world.sendEntityStatus(this, EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES);
					tell(player, "message.ironjungle.panther.tamed");
				} else {
					world.sendEntityStatus(this, EntityStatuses.ADD_NEGATIVE_PLAYER_REACTION_PARTICLES);
				}
			}
			return ActionResult.success(world.isClient);
		}

		return super.interactMob(player, hand);
	}

	private void tameBy(PlayerEntity player) {
		this.setOwner(player);
		this.navigation.stop();
		this.setTarget(null);
		this.setSitting(true);
		this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(TAMED_HEALTH);
		this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(TAMED_DAMAGE);
		this.setHealth((float) TAMED_HEALTH);
	}

	@Override
	public boolean canBreedWith(AnimalEntity other) {
		if (other == this || !this.isTamed() || !(other instanceof PantherEntity panther)) return false;
		if (!panther.isTamed() || panther.isInSittingPose() || this.isInSittingPose()) return false;
		return this.isInLove() && panther.isInLove();
	}

	@Nullable
	@Override
	public PassiveEntity createChild(ServerWorld world, PassiveEntity mate) {
		PantherEntity child = ModEntities.PANTHER.create(world);
		if (child != null && this.getOwner() instanceof PlayerEntity owner) {
			child.tameBy(owner);
			child.setSitting(false);
		}
		return child;
	}

	@Override
	public boolean canAttackWithOwner(LivingEntity target, LivingEntity owner) {
		if (target instanceof TameableEntity tameable && tameable.isTamed()) {
			return tameable.getOwner() != owner;
		}
		return !(target instanceof PlayerEntity && owner instanceof PlayerEntity
				&& !((PlayerEntity) owner).shouldDamagePlayer((PlayerEntity) target));
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return this.isTamed() ? SoundEvents.ENTITY_CAT_PURR : SoundEvents.ENTITY_OCELOT_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_CAT_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_CAT_DEATH;
	}

	@Override
	public float getSoundPitch() {
		return 0.6f + this.random.nextFloat() * 0.1f;
	}
}
