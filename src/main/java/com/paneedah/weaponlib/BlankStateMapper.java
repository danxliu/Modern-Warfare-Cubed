package com.paneedah.weaponlib;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;

import java.util.Collections;
import java.util.Map;

public class BlankStateMapper implements IStateMapper {

    public static final BlankStateMapper DEFAULT = new BlankStateMapper();


    @Override
    public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block blockIn) {
        ModelResourceLocation dummyLocation = new ModelResourceLocation(com.paneedah.mwc.ProjectConstants.ID + ":dummy", "normal");
        Map<IBlockState, ModelResourceLocation> map = new java.util.HashMap<>();
        for (IBlockState state : blockIn.getBlockState().getValidStates()) {
            map.put(state, dummyLocation);
        }
        return map;
    }

}
