package dev.ironjungle.item;

import dev.ironjungle.IronJungle;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class ModItems {
	//                                         тир          защита прочность урон  отбрасывание
	public static final PantherArmorItem IRON_PANTHER_ARMOR = register("iron_panther_armor",
			new PantherArmorItem("iron", 6, 0, 2, 0, new Item.Settings()));

	public static final PantherArmorItem DIAMOND_PANTHER_ARMOR = register("diamond_panther_armor",
			new PantherArmorItem("diamond", 11, 2, 4, 0, new Item.Settings()));

	public static final PantherArmorItem NETHERITE_PANTHER_ARMOR = register("netherite_panther_armor",
			new PantherArmorItem("netherite", 15, 3, 6, 0.3, new Item.Settings().fireproof()));

	private ModItems() {}

	private static <T extends Item> T register(String name, T item) {
		return Registry.register(Registries.ITEM, IronJungle.id(name), item);
	}

	public static void register() {
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
			entries.add(IRON_PANTHER_ARMOR);
			entries.add(DIAMOND_PANTHER_ARMOR);
			entries.add(NETHERITE_PANTHER_ARMOR);
		});
	}
}
