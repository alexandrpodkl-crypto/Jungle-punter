package dev.ironjungle.client;

import dev.ironjungle.IronJungle;
import dev.ironjungle.entity.PantherEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class PantherRenderer extends MobEntityRenderer<PantherEntity, PantherModel> {
	private static final Identifier TEXTURE = IronJungle.id("textures/entity/panther/panther.png");

	public PantherRenderer(EntityRendererFactory.Context context) {
		super(context, new PantherModel(context.getPart(PantherModel.LAYER)), 0.6f);
	}

	@Override
	public Identifier getTexture(PantherEntity entity) {
		return TEXTURE;
	}

	@Override
	protected void scale(PantherEntity entity, MatrixStack matrices, float amount) {
		// Взрослая пантера чуть крупнее модели, котёнок в два раза меньше
		float s = entity.isBaby() ? 0.55f : 1.1f;
		matrices.scale(s, s, s);
	}
}
