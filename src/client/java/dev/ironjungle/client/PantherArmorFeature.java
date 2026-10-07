package dev.ironjungle.client;

import dev.ironjungle.entity.PantherEntity;
import dev.ironjungle.item.PantherArmorItem;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

/** Рисует броню поверх пантеры, если она надета. */
public class PantherArmorFeature extends FeatureRenderer<PantherEntity, PantherModel> {
	private final PantherModel armorModel;

	public PantherArmorFeature(FeatureRendererContext<PantherEntity, PantherModel> context, ModelPart armorPart) {
		super(context);
		this.armorModel = new PantherModel(armorPart);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
					   PantherEntity entity, float limbAngle, float limbDistance, float tickDelta,
					   float animationProgress, float headYaw, float headPitch) {
		ItemStack armor = entity.getArmor();
		if (!(armor.getItem() instanceof PantherArmorItem armorItem)) {
			return;
		}

		this.getContextModel().copyStateTo(this.armorModel);
		this.armorModel.animateModel(entity, limbAngle, limbDistance, tickDelta);
		this.armorModel.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);

		VertexConsumer buffer = vertexConsumers.getBuffer(
				RenderLayer.getEntityCutoutNoCull(armorItem.getEntityTexture()));
		this.armorModel.render(matrices, buffer, light, OverlayTexture.DEFAULT_UV, 0xFFFFFFFF);
	}
}
