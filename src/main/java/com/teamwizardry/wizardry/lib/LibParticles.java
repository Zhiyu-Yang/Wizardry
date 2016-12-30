package com.teamwizardry.wizardry.lib;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpColorFade;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpColorHSV;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.common.util.math.interpolate.InterpFunction;
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.common.util.math.interpolate.position.InterpHelix;
import com.teamwizardry.librarianlib.common.util.math.interpolate.position.InterpLine;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants.MISC;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Saad on 8/29/2016.
 */
public class LibParticles {

	public static void FIZZING_AMBIENT(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(30);
		glitter.setScale(0.3f);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(new Vec3d(pos.xCoord + ThreadLocalRandom.current().nextDouble(-0.5, 0.5), pos.yCoord + ThreadLocalRandom.current().nextDouble(-0.5, 0.5), pos.zCoord + ThreadLocalRandom.current().nextDouble(-0.5, 0.5))), 1, 0, (aFloat, particleBuilder) -> {
			glitter.setColor(new Color(255, 255, 255, ThreadLocalRandom.current().nextInt(50, 150)));
			glitter.setLifetime(ThreadLocalRandom.current().nextInt(20, 30));
			glitter.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.05, 0.05), ThreadLocalRandom.current().nextDouble(0.05, 0.1), ThreadLocalRandom.current().nextDouble(-0.05, 0.05)));
		});
	}

	public static void FIZZING_ITEM(World world, Vec3d pos) {
		ParticleBuilder fizz = new ParticleBuilder(10);
		fizz.setScale(0.3f);
		fizz.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		fizz.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));

		ParticleSpawner.spawn(fizz, world, new StaticInterp<>(pos.addVector(0, 0.5, 0)), 10, 0, (aFloat, particleBuilder) -> {
			fizz.setColor(new Color(255, 255, 255, ThreadLocalRandom.current().nextInt(50, 150)));
			fizz.setLifetime(ThreadLocalRandom.current().nextInt(20, 30));
			fizz.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.005, 0.005), ThreadLocalRandom.current().nextDouble(0.04, 0.08), ThreadLocalRandom.current().nextDouble(-0.005, 0.005)));
		});
	}

	public static void FIZZING_EXPLOSION(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(50);
		glitter.setScale(0.3f);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.0f, 0.3f));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 300, 0, (aFloat, particleBuilder) -> {
			glitter.setLifetime(ThreadLocalRandom.current().nextInt(20, 30));
			glitter.setColor(new Color(255, 255, 255, ThreadLocalRandom.current().nextInt(100, 150)));
			glitter.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.5, 0.5), ThreadLocalRandom.current().nextDouble(-0.5, 0.5), ThreadLocalRandom.current().nextDouble(-0.5, 0.5)));
		});
	}

	public static void DEVIL_DUST_BIG_CRACKLES(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(30);
		glitter.setScale(ThreadLocalRandom.current().nextFloat());
		glitter.setColor(new Color(ThreadLocalRandom.current().nextFloat(), 0, 0));
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE));
		glitter.setAlphaFunction(new InterpFadeInOut(0.0f, 0.3f));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 1, 0, (i, builder) -> {
			Vec3d offset = new Vec3d(ThreadLocalRandom.current().nextDouble(-0.5, 0.5), ThreadLocalRandom.current().nextDouble(-0.5, 0.5), ThreadLocalRandom.current().nextDouble(-0.5, 0.5));
			glitter.setPositionOffset(offset);
			glitter.setLifetime(ThreadLocalRandom.current().nextInt(30, 50));
			glitter.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.01, 0.01), ThreadLocalRandom.current().nextDouble(0.04, 0.06), ThreadLocalRandom.current().nextDouble(-0.01, 0.01)));
		});
	}

	public static void DEVIL_DUST_SMALL_CRACKLES(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(10);
		glitter.setColor(new Color(ThreadLocalRandom.current().nextFloat(), 0, 0).darker());
		glitter.setScale((float) ThreadLocalRandom.current().nextDouble(0, 0.5));
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE));
		glitter.setAlphaFunction(new InterpFadeInOut(0.0f, 0.3f));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 5, 0, (i, builder) -> {
			glitter.setLifetime(ThreadLocalRandom.current().nextInt(10, 30));
			glitter.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.03, 0.03), ThreadLocalRandom.current().nextDouble(0.07, 0.2), ThreadLocalRandom.current().nextDouble(-0.03, 0.03)));
		});
	}

	public static void BOOK_BEAM_NORMAL(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(50);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.0f, 0.3f));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 10, 0, (aFloat, particleBuilder) -> {
			glitter.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.02, 0.02), ThreadLocalRandom.current().nextDouble(0, 1.0), ThreadLocalRandom.current().nextDouble(-0.02, 0.02)));
			glitter.setColor(new Color(255, 255, 255, ThreadLocalRandom.current().nextInt(0, 255)));
			glitter.setScale(ThreadLocalRandom.current().nextFloat());
			glitter.setLifetime(ThreadLocalRandom.current().nextInt(0, 50));
		});
	}

	public static void BOOK_BEAM_HELIX(World world, Vec3d pos) {
		ParticleBuilder helix = new ParticleBuilder(200);
		helix.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		helix.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));

		ParticleSpawner.spawn(helix, world, new StaticInterp<>(pos), 30, 0, (aFloat, particleBuilder) -> {
			helix.setColor(new Color(255, 255, 255, ThreadLocalRandom.current().nextInt(0, 255)));
			helix.setScale(ThreadLocalRandom.current().nextFloat());
			helix.setPositionFunction(new InterpHelix(Vec3d.ZERO, new Vec3d(0, ThreadLocalRandom.current().nextDouble(1.0, 255.0), 0), 0, ThreadLocalRandom.current().nextInt(1, 5), ThreadLocalRandom.current().nextInt(1, 5), 0));
			helix.setLifetime(ThreadLocalRandom.current().nextInt(0, 200));
		});
	}

	public static void BOOK_LARGE_EXPLOSION(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(1000);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.0f, 0.3f));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 1000, 0, (i, build) -> {

			double radius = 1.0;
			double theta = 2.0f * (float) Math.PI * ThreadLocalRandom.current().nextFloat();
			double r = radius * ThreadLocalRandom.current().nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);

			glitter.setPositionOffset(new Vec3d(0, ThreadLocalRandom.current().nextDouble(0, 255.0), 0));
			glitter.setMotion(new Vec3d(x, 0, z));
			glitter.setJitter(10, new Vec3d(ThreadLocalRandom.current().nextDouble(-0.05, 0.05), ThreadLocalRandom.current().nextDouble(-0.05, -0.01), ThreadLocalRandom.current().nextDouble(-0.05, 0.05)));
			glitter.enableMotionCalculation();
			glitter.setColor(new Color(255, 255, 255, ThreadLocalRandom.current().nextInt(70, 170)));
			glitter.setScale((float) ThreadLocalRandom.current().nextDouble(0.3, 0.5));
		});
	}

	public static void AIR_THROTTLE(World world, Vec3d pos, Entity collided, Color color1, Color color2) {
		ParticleBuilder glitter = new ParticleBuilder(ThreadLocalRandom.current().nextInt(30, 50));
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.0f, 0.3f));
		glitter.enableMotionCalculation();

		ParticleSpawner.spawn(glitter, world, new InterpLine(pos, pos.addVector(collided.posX - collided.prevPosX, collided.posY - collided.prevPosY, collided.posZ - collided.prevPosZ)), ThreadLocalRandom.current().nextInt(30, 50), 1, (i, build) -> {
			glitter.setMotion(new Vec3d(collided.motionX + ThreadLocalRandom.current().nextDouble(-0.01, 0.01), (collided.motionY / 2.0) + ThreadLocalRandom.current().nextDouble(-0.01, 0.01), collided.motionZ + ThreadLocalRandom.current().nextDouble(-0.01, 0.01)));
			if (ThreadLocalRandom.current().nextBoolean()) glitter.setColor(color1);
			else glitter.setColor(color2);
		});
	}

	public static void HALLOWED_SPIRIT_FLAME_FAR(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(30);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 5, 0, (i, build) -> {
			double radius = 0.15;
			double theta = 2.0f * (float) Math.PI * ThreadLocalRandom.current().nextFloat();
			double r = radius * ThreadLocalRandom.current().nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);

			glitter.setColorFunction(new InterpColorHSV(Color.RED, 50, 20.0F));
			glitter.setPositionOffset(new Vec3d(x, ThreadLocalRandom.current().nextDouble(0, 0.5), z));
			glitter.addMotion(new Vec3d(0, ThreadLocalRandom.current().nextDouble(0, 0.02), 0));
		});
	}

	public static void HALLOWED_SPIRIT_FLAME_CLOSE(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(30);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 20, 0, (i, build) -> {
			double radius = 0.2;
			double theta = 2.0f * (float) Math.PI * ThreadLocalRandom.current().nextFloat();
			double r = radius * ThreadLocalRandom.current().nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);

			glitter.setColor(Color.RED);
			glitter.setPositionOffset(new Vec3d(x, ThreadLocalRandom.current().nextDouble(0, 0.5), z));
			glitter.addMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.02, 0.02), ThreadLocalRandom.current().nextDouble(0, 0.03), ThreadLocalRandom.current().nextDouble(-0.02, 0.02)));
		});
	}

	public static void HALLOWED_SPIRIT_FLAME_NORMAL(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(30);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));
		glitter.disableRandom();

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 10, 0, (i, build) -> {
			double radius = 0.15;
			double theta = 2.0f * (float) Math.PI * ThreadLocalRandom.current().nextFloat();
			double r = radius * ThreadLocalRandom.current().nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);

			glitter.setColor(new Color(0x4DFFFFFF, true));
			glitter.setPositionOffset(new Vec3d(x, ThreadLocalRandom.current().nextDouble(0, 0.4), z));
		});
	}

	public static void HALLOWED_SPIRIT_HURT(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(ThreadLocalRandom.current().nextInt(100, 150));
		glitter.setColorFunction(new InterpColorHSV(Color.BLUE, 50, 20.0F));
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.1f, 0.1f));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), ThreadLocalRandom.current().nextInt(40, 100), 0, (i, build) -> {
			double radius = 0.2;
			double theta = 2.0f * (float) Math.PI * ThreadLocalRandom.current().nextFloat();
			double r = radius * ThreadLocalRandom.current().nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);

			glitter.setPositionOffset(new Vec3d(x, ThreadLocalRandom.current().nextDouble(0, 0.4), z));
			glitter.setMotion(new Vec3d(0, ThreadLocalRandom.current().nextDouble(0, 0.02), 0));
		});
	}

	public static void FAIRY_TRAIL(World world, Vec3d pos, Color color, boolean sad, int age) {
		if (((age / 4) >= (age / 2)) || (age == 0)) return;
		ParticleBuilder glitter = new ParticleBuilder(ThreadLocalRandom.current().nextInt(age / 4, age / 2));
		glitter.setColor(color);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.2f, 0.4f));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), ThreadLocalRandom.current().nextInt(5, 10), 0, (i, build) -> {
			glitter.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.02, 0.02), ThreadLocalRandom.current().nextDouble(-0.02, 0.02), ThreadLocalRandom.current().nextDouble(-0.02, 0.02)));
			if (sad) glitter.enableMotionCalculation();
		});
	}

	public static void FAIRY_EXPLODE(World world, Vec3d pos, Color color) {
		ParticleBuilder glitter = new ParticleBuilder(ThreadLocalRandom.current().nextInt(50, 80));
		glitter.setColor(color.darker());
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.0f, 0.3f));
		glitter.enableMotionCalculation();

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), ThreadLocalRandom.current().nextInt(50, 100), 0, (i, build) -> {
			double radius = 0.5;
			double theta = 2.0f * (float) Math.PI * ThreadLocalRandom.current().nextFloat();
			double r = radius * ThreadLocalRandom.current().nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);
			glitter.setMotion(new Vec3d(x, ThreadLocalRandom.current().nextDouble(-0.3, 0.5), z));
		});
	}

	public static void CRAFTING_ALTAR_CLUSTER_EXPLODE(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(ThreadLocalRandom.current().nextInt(30, 80));
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.0f, 1.0f));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), ThreadLocalRandom.current().nextInt(20, 30), 0, (i, build) -> {
			double radius = 0.3;
			double theta = 2.0f * (float) Math.PI * ThreadLocalRandom.current().nextFloat();
			double r = radius * ThreadLocalRandom.current().nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);
			Random random = new Random();
			glitter.setScale(random.nextFloat());
			glitter.setColor(new Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), random.nextFloat()).brighter());
			glitter.setPositionOffset(new Vec3d(x, ThreadLocalRandom.current().nextDouble(-0.3, 0.3), z));
			glitter.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.1, 0.1),
					ThreadLocalRandom.current().nextDouble(-0.1, 0.1),
					ThreadLocalRandom.current().nextDouble(-0.1, 0.1)));
		});
	}

	public static void CRAFTING_ALTAR_HELIX(World world, Vec3d pos) {
		ParticleBuilder beam = new ParticleBuilder(200);
		beam.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		beam.setAlphaFunction(new InterpFadeInOut(0.1f, 0.3f));

		ParticleSpawner.spawn(beam, world, new StaticInterp<>(pos), 10, 0, (aFloat, particleBuilder) -> {
			beam.setScale(ThreadLocalRandom.current().nextFloat());
			beam.setColor(new Color(ThreadLocalRandom.current().nextInt(50, 255), ThreadLocalRandom.current().nextInt(50, 255), ThreadLocalRandom.current().nextInt(50, 255), ThreadLocalRandom.current().nextInt(150, 255)).brighter().brighter());
			beam.setMotion(new Vec3d(0, ThreadLocalRandom.current().nextDouble(0.1, 0.8), 0));
			beam.setLifetime(ThreadLocalRandom.current().nextInt(0, 200));
		});

		ParticleBuilder helix = new ParticleBuilder(200);
		helix.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		helix.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));

		ParticleSpawner.spawn(helix, world, new StaticInterp<>(pos), 10, 0, (aFloat, particleBuilder) -> {
			helix.setScale(ThreadLocalRandom.current().nextFloat());
			helix.setColor(new Color(255, 255, 255, ThreadLocalRandom.current().nextInt(50, 170)));
			helix.setPositionFunction(new InterpHelix(Vec3d.ZERO, new Vec3d(0, ThreadLocalRandom.current().nextDouble(1.0, 255.0), 0), 0, ThreadLocalRandom.current().nextInt(1, 5), ThreadLocalRandom.current().nextInt(1, 5), 0));
			helix.setLifetime(ThreadLocalRandom.current().nextInt(0, 200));
		});
	}

	public static void CRAFTING_ALTAR_CLUSTER_DRAPE(World world, Vec3d pos) {
        ParticleBuilder glitter = new ParticleBuilder(200);
        glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
        glitter.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));
        glitter.enableMotionCalculation();

        ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), ThreadLocalRandom.current().nextInt(5, 20), 0, (aFloat, particleBuilder) -> {
            glitter.setColor(new Color(ThreadLocalRandom.current().nextInt(0, 100), ThreadLocalRandom.current().nextInt(0, 100), ThreadLocalRandom.current().nextInt(50, 255)));
            glitter.setScale(ThreadLocalRandom.current().nextFloat());
            glitter.addMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.01, 0.01),
                    ThreadLocalRandom.current().nextDouble(-0.01, 0.01),
					ThreadLocalRandom.current().nextDouble(-0.01, 0.01)));
            glitter.setLifetime(ThreadLocalRandom.current().nextInt(20, 50));
        });
	}

	public static void CRAFTING_ALTAR_CLUSTER_SUCTION(World world, Vec3d pos, InterpFunction<Vec3d> bezier3D) {
		ParticleBuilder helix = new ParticleBuilder(200);
		helix.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		helix.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));

		ParticleSpawner.spawn(helix, world, new StaticInterp<>(pos), ThreadLocalRandom.current().nextInt(1, 3), 0, (aFloat, particleBuilder) -> {
			helix.setColor(new Color(ThreadLocalRandom.current().nextInt(50, 255), ThreadLocalRandom.current().nextInt(50, 255), ThreadLocalRandom.current().nextInt(50, 255), ThreadLocalRandom.current().nextInt(10, 255)));
			helix.setScale(ThreadLocalRandom.current().nextFloat());
			helix.setPositionFunction(bezier3D);
			helix.setLifetime(ThreadLocalRandom.current().nextInt(0, 200));
		});
	}

	public static void CRAFTING_ALTAR_PEARL_EXPLODE(World world, Vec3d pos) {
		ParticleBuilder builder = new ParticleBuilder(1);
		builder.setAlphaFunction(new InterpFadeInOut(0.0f, 1.0f));
		builder.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		ParticleSpawner.spawn(builder, world, new StaticInterp<>(pos), ThreadLocalRandom.current().nextInt(200, 300), 0, (aFloat, particleBuilder) -> {
			builder.setColorFunction(new InterpColorFade(new Color(ThreadLocalRandom.current().nextInt(0, 20), ThreadLocalRandom.current().nextInt(100, 255), ThreadLocalRandom.current().nextInt(0, 20)), 1, 255, 1));
			double radius = 0.1;
			double theta = 2.0f * (float) Math.PI * ThreadLocalRandom.current().nextFloat();
			double r = radius * ThreadLocalRandom.current().nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);
			builder.setPositionOffset(new Vec3d(x, ThreadLocalRandom.current().nextDouble(-0.1, 0.1), z));
			builder.setScale(ThreadLocalRandom.current().nextFloat());
			builder.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.03, 0.03),
					ThreadLocalRandom.current().nextDouble(-0.03, 0.03),
					ThreadLocalRandom.current().nextDouble(-0.03, 0.03)));
			builder.setLifetime(ThreadLocalRandom.current().nextInt(20, 80));
		});
	}

	public static void SHAPE_BEAM(World world, Vec3d target, Vec3d origin, Vec3d reverseNormal, int distance, Color color) {
		ParticleBuilder glitter = new ParticleBuilder(10);
		glitter.setColor(new Color(1.0f, 1.0f, 1.0f, 0.1f));
		glitter.setPositionFunction(new InterpHelix(Vec3d.ZERO, reverseNormal, 0.0f, 0.15f, 1.0F, 0));
		glitter.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));

		float[] hsbVals = new float[3];
		Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsbVals);
		color = new Color(Color.HSBtoRGB((float) (1.0 / ThreadLocalRandom.current().nextInt(100, 255)), hsbVals[1], hsbVals[2]));
		glitter.setColor(color);

		ParticleSpawner.spawn(glitter, world, new InterpLine(target, origin), distance, 0, (aFloat, particleBuilder) -> {
			glitter.setScale((float) ThreadLocalRandom.current().nextDouble(0.3, 0.8));
			glitter.setLifetime(ThreadLocalRandom.current().nextInt(10, 20));
			if (ThreadLocalRandom.current().nextBoolean())
				glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE));
			else glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		});
	}

	public static void EFFECT_FIRE(World world, Vec3d pos, Vec3d normal, float power) {
		ParticleBuilder core = new ParticleBuilder((int) (20.0F * power));
		core.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		ParticleSpawner.spawn(core, world, new StaticInterp<>(pos), (int) (40.0F * power), 0, (aFloat, particleBuilder) -> {
			double radius = (3.0 * power);
			double theta = 2.0f * (float) Math.PI * ThreadLocalRandom.current().nextFloat();
			double r = radius * ThreadLocalRandom.current().nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);

			core.setMotion(new Vec3d((x / 10.0) * power, ThreadLocalRandom.current().nextDouble(0.2 * power, 0.3 * power), z / 10.0 * power));

			Color color = Color.RED;
			float[] hsbVals = new float[3];
			Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsbVals);
			color = new Color(Color.HSBtoRGB((float) (1.0 / ThreadLocalRandom.current().nextInt(50, 255)), hsbVals[1], hsbVals[2]));
			core.setColor(color);
			core.setAlphaFunction(new InterpFadeInOut(0.1f, (float) ThreadLocalRandom.current().nextDouble(0.3, 0.6)));
		});
	}

	public static void SHAPE_CONE(World world, Vec3d pos, Vec3d normal) {
		ParticleBuilder core = new ParticleBuilder(20);
		core.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		ParticleSpawner.spawn(core, world, new StaticInterp<>(pos), 40, 0, (aFloat, particleBuilder) -> {
			double radius = 3.0;
			double theta = 2.0f * (float) Math.PI * ThreadLocalRandom.current().nextFloat();
			double r = radius * ThreadLocalRandom.current().nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);
			core.setMotion(new Vec3d(x + normal.xCoord, z / 10.0, z + normal.yCoord).normalize());

			Color color = Color.RED;
			float[] hsbVals = new float[3];
			Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsbVals);
			color = new Color(Color.HSBtoRGB((float) (1.0 / ThreadLocalRandom.current().nextInt(50, 255)), hsbVals[1], hsbVals[2]));
			core.setColor(color);
			core.setAlphaFunction(new InterpFadeInOut(0.1f, (float) ThreadLocalRandom.current().nextDouble(0.3, 0.6)));
		});
	}

	public static void TEMPLATE_BLOCK_ERROR(World world, Vec3d pos) {
		ParticleBuilder glitter = new ParticleBuilder(50);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
		glitter.setColor(Color.RED);
		glitter.setAlphaFunction(new InterpFadeInOut(0.3f, 0.5f));
		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 50, 0, (aFloat, particleBuilder) -> {
			glitter.setMotion(new Vec3d(0, ThreadLocalRandom.current().nextDouble(0, 0.1), 0));
			glitter.setPositionOffset(new Vec3d(0, ThreadLocalRandom.current().nextDouble(0, 1), 0));
		});
	}
}