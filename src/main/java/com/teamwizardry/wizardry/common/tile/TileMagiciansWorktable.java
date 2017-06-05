package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.TileMod;
import com.teamwizardry.librarianlib.features.saving.Save;
import net.minecraft.util.math.BlockPos;

/**
 * Created by LordSaad.
 */
@TileRegister("magicians_worktable")
public class TileMagiciansWorktable extends TileMod {

	@Save
	public BlockPos linkedTable;
}
