package dev.ironjungle.client;

import dev.ironjungle.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class IronJungleClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EntityModelLayerRegistry.registerModelLayer(PantherModel.LAYER, PantherModel::getTexturedModelData);
		EntityRendererRegistry.register(ModEntities.PANTHER, PantherRenderer::new);
	}
}
