package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.wizardry.api.capability.mana.CapManager;
import com.teamwizardry.wizardry.api.capability.mana.CustomWizardryCapability;
import com.teamwizardry.wizardry.api.capability.mana.WizardryCapabilityProvider;
import com.teamwizardry.wizardry.api.item.IManaCell;
import com.teamwizardry.wizardry.common.block.fluid.ModFluids;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Demoniaque on 6/21/2016.
 */
public class ItemOrb extends ItemMod implements IManaCell {

	public ItemOrb() {
		super("orb", "glass_orb", "mana_orb");

		this.addPropertyOverride(new ResourceLocation("fill"), new IItemPropertyGetter() {
			@SideOnly(Side.CLIENT)
			public float apply(@Nonnull ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				double mana = CapManager.getMana(stack);
				double maxMana = CapManager.getMaxMana(stack);

				return (int) (10 * mana / maxMana) / 10f;
			}
		});
	}

	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
		return new WizardryCapabilityProvider(new CustomWizardryCapability(100, 100, stack.getItemDamage() * 100, 0));
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		if (entityItem.getItem().getItemDamage() == 0) {
			if (!CapManager.isManaEmpty(entityItem.getItem())) {
				entityItem.getItem().setItemDamage(1);
			} else {
				IBlockState state = entityItem.world.getBlockState(entityItem.getPosition());
				if (state.getBlock() == ModFluids.NACRE.getActualBlock()) {
					ItemStack newStack = new ItemStack(ModItems.PEARL_NACRE, entityItem.getItem().getCount());
					entityItem.setItem(newStack);
					newStack.getItem().onEntityItemUpdate(entityItem);
				}
			}
		} else if (entityItem.getItem().getItemDamage() == 1) {
			if (CapManager.isManaEmpty(entityItem.getItem())) {
				entityItem.getItem().setItemDamage(0);
			} else {
				IBlockState state = entityItem.world.getBlockState(entityItem.getPosition());
				if (state.getBlock() == ModFluids.MANA.getActualBlock()) {
					CapManager.forObject(entityItem.getItem())
							.setMana(CapManager.getMaxMana(entityItem.getItem()))
							.close();
				}
			}
		}
		return super.onEntityItemUpdate(entityItem);
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {
		if (CapManager.isManaEmpty(stack) && stack.getItemDamage() == 1)
			stack.setItemDamage(0);
		else if (!CapManager.isManaEmpty(stack) && stack.getItemDamage() == 0)
			stack.setItemDamage(1);
	}

	@NotNull
	@Override
	public String getTranslationKey(@NotNull ItemStack stack) {
		float percentage = (int) (10 * CapManager.getMana(stack) / CapManager.getMaxMana(stack)) / 10f;
		return super.getTranslationKey(stack) + ".fill." + ((int) (percentage * 100));
	}

	@Override
	public void getSubItems(@Nullable CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems) {
		if (isInCreativeTab(tab)) {

			subItems.add(new ItemStack(this));

			for (int i = 1; i < 10; i++) {
				ItemStack stack = new ItemStack(this, 1, 1);
				CapManager.forObject(stack)
						.setMana(CapManager.getMaxMana(stack) * i / 10.0)
						.close();
				subItems.add(stack);
			}

			subItems.add(new ItemStack(this, 1, 1));
		}
	}
}
