package com.paneedah.weaponlib.tile;

import static com.paneedah.mwc.proxies.ClientProxy.MC;

import java.util.function.Consumer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CustomTileEntityItemRenderer extends TileEntityItemStackRenderer {

    private final ModelBase model;
    private final ResourceLocation textureResource;
    private final Consumer<TileEntity> positioning;
    private final float displayScale;
    private final float offsetX, offsetY, offsetZ;

    public CustomTileEntityItemRenderer(
        ModelBase model,
        ResourceLocation textureResource,
        Consumer<TileEntity> positioning,
        float displayScale,
        float offsetX,
        float offsetY,
        float offsetZ
    ) {
        this.model = model;
        this.textureResource = textureResource;
        this.positioning = positioning;
        this.displayScale = displayScale;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
    }

    @Override
    public void renderByItem(ItemStack itemStackIn, float partialTicks) {
        GlStateManager.pushMatrix();

        GlStateManager.scale(displayScale, displayScale, displayScale);
        GlStateManager.translate(offsetX, offsetY, offsetZ);

        MC.getTextureManager().bindTexture(textureResource);

        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();

        GlStateManager.scale(1.0F, -1.0F, -1.0F);
        GlStateManager.translate(0.5F, -0.5F, -0.5F);

        if (positioning != null) {
            positioning.accept(null);
        }

        model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);

        GlStateManager.popAttrib();
        GlStateManager.popMatrix();

        GlStateManager.popMatrix();
    }
}
