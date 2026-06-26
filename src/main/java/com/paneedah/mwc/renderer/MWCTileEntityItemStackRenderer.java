package com.paneedah.mwc.renderer;

import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MWCTileEntityItemStackRenderer extends TileEntityItemStackRenderer {

    private final ModelSource modelSource;

    public MWCTileEntityItemStackRenderer(ModelSource modelSource) {
        this.modelSource = modelSource;
    }

    @Override
    public void renderByItem(ItemStack itemStackIn) {
        if (this.modelSource != null) {
            this.modelSource.renderItem();
        }
    }
}
