package dev.ironjungle;

import dev.ironjungle.entity.ModEntities;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IronJungle implements ModInitializer {
	public static final String MOD_ID = "ironjungle";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		ModEntities.register();
		LOGGER.info("Iron Jungle загружен");
	}
}
