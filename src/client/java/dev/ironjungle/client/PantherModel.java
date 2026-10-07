package dev.ironjungle.client;

import dev.ironjungle.IronJungle;
import dev.ironjungle.entity.PantherEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.util.math.MathHelper;

/**
 * Модель пантеры (координаты в пикселях, 16 = 1 блок, ось Y смотрит вниз, земля на Y = 24).
 * Анимации: шаг лап, хвост, поворот головы, поза "лежит" когда сидит.
 */
public class PantherModel extends SinglePartEntityModel<PantherEntity> {
	public static final EntityModelLayer LAYER = new EntityModelLayer(IronJungle.id("panther"), "main");
	public static final EntityModelLayer ARMOR_LAYER = new EntityModelLayer(IronJungle.id("panther"), "armor");

	private static final float PI = (float) Math.PI;

	private final ModelPart root;
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart tail;
	private final ModelPart tailTip;
	private final ModelPart frontLeftLeg;
	private final ModelPart frontRightLeg;
	private final ModelPart hindLeftLeg;
	private final ModelPart hindRightLeg;

	public PantherModel(ModelPart root) {
		this.root = root;
		this.body = root.getChild("body");
		this.head = root.getChild("head");
		this.tail = root.getChild("tail");
		this.tailTip = this.tail.getChild("tail_tip");
		this.frontLeftLeg = root.getChild("front_left_leg");
		this.frontRightLeg = root.getChild("front_right_leg");
		this.hindLeftLeg = root.getChild("hind_left_leg");
		this.hindRightLeg = root.getChild("hind_right_leg");
	}

	public static TexturedModelData getTexturedModelData() {
		return create(Dilation.NONE);
	}

	/** Та же модель, но чуть толще: на неё натягивается броня. */
	public static TexturedModelData getArmorModelData() {
		return create(new Dilation(0.4f));
	}

	private static TexturedModelData create(Dilation d) {
		ModelData data = new ModelData();
		ModelPartData r = data.getRoot();

		// Туловище 6x6x16
		r.addChild("body", ModelPartBuilder.create().uv(0, 0)
				.cuboid(-3f, -3f, -8f, 6f, 6f, 16f, d), ModelTransform.pivot(0f, 14f, 0f));

		// Голова 6x6x5 + морда + уши
		r.addChild("head", ModelPartBuilder.create()
				.uv(0, 22).cuboid(-3f, -3f, -5f, 6f, 6f, 5f, d)
				.uv(22, 22).cuboid(-2f, 0f, -7f, 4f, 3f, 2f, d)
				.uv(34, 22).cuboid(-3f, -4f, -2f, 1f, 1f, 1f, d)
				.uv(38, 22).cuboid(2f, -4f, -2f, 1f, 1f, 1f, d),
				ModelTransform.pivot(0f, 12f, -8f));

		// Хвост из двух частей, второй загибается вверх
		ModelPartData tail = r.addChild("tail", ModelPartBuilder.create().uv(44, 0)
				.cuboid(-1f, 0f, 0f, 2f, 8f, 2f, d), ModelTransform.of(0f, 12f, 7f, 0.9f, 0f, 0f));
		tail.addChild("tail_tip", ModelPartBuilder.create().uv(52, 0)
				.cuboid(-1f, 0f, 0f, 2f, 7f, 2f, d), ModelTransform.of(0f, 7.5f, 0f, -0.5f, 0f, 0f));

		// Лапы 3x7x3
		r.addChild("front_left_leg", ModelPartBuilder.create().uv(0, 34)
				.cuboid(-1.5f, 0f, -1.5f, 3f, 7f, 3f, d), ModelTransform.pivot(1.9f, 17f, -6f));
		r.addChild("front_right_leg", ModelPartBuilder.create().uv(12, 34)
				.cuboid(-1.5f, 0f, -1.5f, 3f, 7f, 3f, d), ModelTransform.pivot(-1.9f, 17f, -6f));
		r.addChild("hind_left_leg", ModelPartBuilder.create().uv(24, 34)
				.cuboid(-1.5f, 0f, -1.5f, 3f, 7f, 3f, d), ModelTransform.pivot(1.9f, 17f, 6f));
		r.addChild("hind_right_leg", ModelPartBuilder.create().uv(36, 34)
				.cuboid(-1.5f, 0f, -1.5f, 3f, 7f, 3f, d), ModelTransform.pivot(-1.9f, 17f, 6f));

		return TexturedModelData.of(data, 64, 64);
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}

	@Override
	public void setAngles(PantherEntity entity, float limbAngle, float limbDistance,
						  float animationProgress, float headYaw, float headPitch) {
		// Каждый кадр начинаем с исходной позы
		this.root.traverse().forEach(ModelPart::resetTransform);

		this.head.yaw = headYaw * (PI / 180f);
		this.head.pitch = headPitch * (PI / 180f);

		if (entity.isInSittingPose()) {
			// Лежит: тело на земле, передние лапы вытянуты вперёд
			this.body.pivotY = 19f;
			this.head.pivotY = 17f;
			this.tail.pivotY = 17f;
			this.tail.pitch = 1.45f;
			this.tailTip.pitch = 0.1f;

			this.frontLeftLeg.pivotY = 21.5f;
			this.frontRightLeg.pivotY = 21.5f;
			this.frontLeftLeg.pitch = -PI / 2f;
			this.frontRightLeg.pitch = -PI / 2f;

			this.hindLeftLeg.pivotY = 21.5f;
			this.hindRightLeg.pivotY = 21.5f;
			this.hindLeftLeg.pitch = -PI / 2f;
			this.hindRightLeg.pitch = -PI / 2f;

			// Хвост лениво покачивается
			this.tail.yaw = MathHelper.sin(animationProgress * 0.05f) * 0.2f;
			return;
		}

		// Шаг: диагональные лапы двигаются вместе
		float swing = limbAngle * 0.6662f;
		float amount = 1.2f * limbDistance;
		this.frontLeftLeg.pitch = MathHelper.cos(swing) * amount;
		this.hindRightLeg.pitch = MathHelper.cos(swing) * amount;
		this.frontRightLeg.pitch = MathHelper.cos(swing + PI) * amount;
		this.hindLeftLeg.pitch = MathHelper.cos(swing + PI) * amount;

		// Хвост: на бегу вытягивается назад, в покое покачивается
		this.tail.pitch = 0.9f + limbDistance * 0.5f;
		this.tail.yaw = MathHelper.sin(animationProgress * 0.08f) * 0.15f
				+ MathHelper.cos(swing) * 0.2f * limbDistance;
		this.tailTip.pitch = -0.5f + limbDistance * 0.4f;
	}
}
