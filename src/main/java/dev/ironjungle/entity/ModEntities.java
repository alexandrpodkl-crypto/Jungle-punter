package dev.ironjungle.entity;

import dev.ironjungle.IronJungle;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnLocationTypes;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.BiomeKeys;

public final class ModEntities {
	public static final EntityType<PantherEntity> PANTHER = Registry.register(
			Registries.ENTITY_TYPE,
			IronJungle.id("panther"),
			EntityType.Builder.create(PantherEntity::new, SpawnGroup.CREATURE)
					.dimensions(0.8f, 1.0f)
					.maxTrackingRange(10)
					.build("panther"));

	// Яйцо призыва: чёрное с серыми пятнами
	public static final Item PANTHER_SPAWN_EGG = Registry.register(
			Registries.ITEM,
			IronJungle.id("panther_spawn_egg"),
			new SpawnEggItem(PANTHER, 0x15151A, 0x3C3C46, new Item.Settings()));

	private ModEntities() {}

	public static void register() {
		FabricDefaultAttributeRegistry.register(PANTHER, PantherEntity.createPantherAttributes());

		SpawnRestriction.register(PANTHER, SpawnLocationTypes.ON_GROUND,
				Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);

		// Пантеры встречаются в джунглях: вес 6, группы по 1-2
		BiomeModifications.addSpawn(
				BiomeSelectors.includeByKey(BiomeKeys.JUNGLE, BiomeKeys.SPARSE_JUNGLE, BiomeKeys.BAMBOO_JUNGLE),
				SpawnGroup.CREATURE, PANTHER, 6, 1, 2);

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(entries -> entries.add(PANTHER_SPAWN_EGG));
	}
}
