package com.teamwizardry.wizardry.common.core;

import com.teamwizardry.librarianlib.common.network.PacketHandler;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Config;
import com.teamwizardry.wizardry.api.module.ModuleRegistry;
import com.teamwizardry.wizardry.api.spell.SpellHandler;
import com.teamwizardry.wizardry.api.trackerobject.SpellTracker;
import com.teamwizardry.wizardry.client.gui.GuiHandler;
import com.teamwizardry.wizardry.common.achievement.AchievementEvents;
import com.teamwizardry.wizardry.common.achievement.Achievements;
import com.teamwizardry.wizardry.common.fluid.Fluids;
import com.teamwizardry.wizardry.common.network.PacketCapeOwnerTransfer;
import com.teamwizardry.wizardry.common.network.PacketCapeTick;
import com.teamwizardry.wizardry.common.network.WizardryPacketHandler;
import com.teamwizardry.wizardry.common.world.GenHandler;
import com.teamwizardry.wizardry.common.world.WorldProviderUnderWorld;
import com.teamwizardry.wizardry.init.*;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {
		WizardryPacketHandler.registerMessages();

		Config.initConfig(event.getSuggestedConfigurationFile());

		ModSounds.init();
		ModItems.init();
		ModBlocks.init();
		Achievements.init();
		ModRecipes.initCrafting();
		ModEntities.init();

		ModCapabilities.preInit();
		Fluids.preInit();

		WizardryPacketHandler.registerMessages();
		NetworkRegistry.INSTANCE.registerGuiHandler(Wizardry.instance, new GuiHandler());

		Wizardry.underWorld = DimensionType.register("underworld", "_dim", Config.underworld_id, WorldProviderUnderWorld.class, false);
		DimensionManager.registerDimension(Config.underworld_id, Wizardry.underWorld);

		MinecraftForge.EVENT_BUS.register(new EventHandler());
		MinecraftForge.EVENT_BUS.register(new AchievementEvents());
		MinecraftForge.EVENT_BUS.register(new ModCapabilities());

		PacketHandler.register(PacketCapeTick.class, Side.SERVER);
		PacketHandler.register(PacketCapeOwnerTransfer.class, Side.SERVER);
	}

	public void init(FMLInitializationEvent event) {
		GameRegistry.registerWorldGenerator(new GenHandler(), 0);

		ModuleRegistry.getInstance();
		SpellHandler.INSTANCE.getClass();
		ModModules.init();
	}

	public void postInit(FMLPostInitializationEvent event) {
		SpellTracker.init();
	}

	public boolean isClient() {
		return false;
	}

	public void openGUI(Object gui) {

	}
}