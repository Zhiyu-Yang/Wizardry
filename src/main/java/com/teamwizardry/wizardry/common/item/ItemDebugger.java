package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.item.GlowingOverlayHelper;
import com.teamwizardry.wizardry.api.item.IGlowOverlayable;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import com.teamwizardry.wizardry.common.tile.TileManaBattery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDebugger extends Item implements IGlowOverlayable {

	public ItemDebugger() {
		setRegistryName("debugger");
		setUnlocalizedName("debugger");
		GameRegistry.register(this);
		setMaxStackSize(1);
		setCreativeTab(Wizardry.tab);
		addPropertyOverride(new ResourceLocation(Wizardry.MODID, "overlay"), GlowingOverlayHelper.OVERLAY_OVERRIDE);
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (worldIn.getTileEntity(pos) instanceof TileManaBattery) {
			TileManaBattery tmb = (TileManaBattery) worldIn.getTileEntity(pos);
			if (!worldIn.isRemote) {
				playerIn.addChatMessage(new TextComponentString("Mana: " + tmb.current_mana + "/" + tmb.MAX_MANA));
			}
		}
		return EnumActionResult.PASS;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
		if (worldIn.isRemote) return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
		/*IWizardryCapability data = WizardryCapabilityProvider.get(playerIn);
		if (!worldIn.isRemote) {
			if (playerIn.isSneaking())
				data.setBloodType(BloodRegistry.HUMANBLOOD, playerIn);
			else data.setBloodType(BloodRegistry.PYROBLOOD, playerIn);
			return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
		}
		if (playerIn.isSneaking())
			if (GuiScreen.isCtrlKeyDown()) data.setBurnout(50, playerIn);
			else data.setBurnout(0, playerIn);

		else if (GuiScreen.isCtrlKeyDown()) data.setMana(50, playerIn);
		else data.setMana(0, playerIn);

		playerIn.addChatComponentMessage(new TextComponentString(data.getMana() + "/" + data.getMaxMana()));*/

		//TeleportUtil.teleportToDimension(playerIn, 100, 0, 100, 0);
		//EntityHallowedSpirit entity = new EntityHallowedSpirit(worldIn);
		EntityFairy entity = new EntityFairy(worldIn);
		entity.setPosition(playerIn.posX, playerIn.posY + 1, playerIn.posZ);
		worldIn.spawnEntityInWorld(entity);

		return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}
}
