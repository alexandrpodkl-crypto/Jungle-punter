package dev.ironjungle.item;

import dev.ironjungle.IronJungle;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

/**
 * Броня для пантеры. Надевается в слот тела (как броня волка).
 * Даёт защиту, прочность брони и бонус к урону — пантера становится боевой.
 */
public class PantherArmorItem extends Item {
	private final Identifier entityTexture;

	public PantherArmorItem(String tier, double armor, double toughness, double attackBonus,
							double knockbackResistance, Item.Settings settings) {
		super(settings.maxCount(1).attributeModifiers(
				modifiers(armor, toughness, attackBonus, knockbackResistance)));
		this.entityTexture = IronJungle.id("textures/entity/panther/armor/" + tier + ".png");
	}

	/** Текстура брони на самой пантере. */
	public Identifier getEntityTexture() {
		return this.entityTexture;
	}

	private static AttributeModifiersComponent modifiers(double armor, double toughness,
														 double attackBonus, double knockbackResistance) {
		Identifier id = IronJungle.id("panther_armor");
		AttributeModifierSlot slot = AttributeModifierSlot.BODY;
		AttributeModifiersComponent.Builder builder = AttributeModifiersComponent.builder()
				.add(EntityAttributes.GENERIC_ARMOR,
						new EntityAttributeModifier(id, armor, EntityAttributeModifier.Operation.ADD_VALUE), slot)
				.add(EntityAttributes.GENERIC_ATTACK_DAMAGE,
						new EntityAttributeModifier(id, attackBonus, EntityAttributeModifier.Operation.ADD_VALUE), slot);
		if (toughness > 0) {
			builder.add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS,
					new EntityAttributeModifier(id, toughness, EntityAttributeModifier.Operation.ADD_VALUE), slot);
		}
		if (knockbackResistance > 0) {
			builder.add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,
					new EntityAttributeModifier(id, knockbackResistance, EntityAttributeModifier.Operation.ADD_VALUE), slot);
		}
		return builder.build();
	}
}
