package com.paneedah.mwc.blocks;

import static com.paneedah.mwc.ProjectConstants.ID;

import com.paneedah.mwc.bases.IItemBlockProvider;
import net.minecraft.item.Item;

public class SteelBricksDoubleSlab extends SteelBricksSlab implements IItemBlockProvider {
    public SteelBricksDoubleSlab() {
        super();
        this.setRegistryName(ID, "steel_bricks_double_slab");
        this.setTranslationKey("steel_bricks_double_slab");
    }

    @Override
    public boolean isDouble() {
        return true;
    }

    @Override
    public Item getItemBlock() {
        return null;
    }
}
