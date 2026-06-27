package com.paneedah.mwc.blocks;

import static com.paneedah.mwc.ProjectConstants.ID;

import com.paneedah.mwc.MWC;
import com.paneedah.mwc.bases.IItemBlockProvider;
import com.paneedah.mwc.init.MWCBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSlab;

public class SteelBricksHalfSlab extends SteelBricksSlab implements IItemBlockProvider {
    public SteelBricksHalfSlab() {
        super();
        this.setRegistryName(ID, "steel_bricks_slab");
        this.setTranslationKey("steel_bricks_slab");
        this.setCreativeTab(MWC.BLOCKS_AND_INGOTS_TAB);
    }

    @Override
    public boolean isDouble() {
        return false;
    }

    @Override
    public Item getItemBlock() {
        return new ItemSlab(this, this, MWCBlocks.steelBricksDoubleSlab);
    }
}
