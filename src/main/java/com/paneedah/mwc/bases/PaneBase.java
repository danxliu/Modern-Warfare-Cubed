package com.paneedah.mwc.bases;

import com.paneedah.mwc.MWC;
import net.minecraft.block.BlockPane;
import net.minecraft.block.material.Material;

import static com.paneedah.mwc.ProjectConstants.ID;

public class PaneBase extends BlockPane {
    public PaneBase(String registryName, Material materialIn, boolean canDrop) {
        super(materialIn, canDrop);
        setRegistryName(ID, registryName);
        setTranslationKey(registryName);
        setCreativeTab(MWC.BLOCKS_AND_INGOTS_TAB);
    }
}
