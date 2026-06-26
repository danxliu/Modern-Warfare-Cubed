package com.paneedah.weaponlib.tile;

import static com.paneedah.mwc.proxies.ClientProxy.MC;

import com.paneedah.mwc.renderer.ModelSource;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.vecmath.Matrix4f;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class CustomTileEntityItemRenderer extends ModelSource {

    private final ModelBase model;
    private final ResourceLocation textureResource;
    private final Consumer<TileEntity> positioning;
    private final BiConsumer<
        ItemStack,
        ItemCameraTransforms.TransformType
    > itemPositioning;
    private final float displayScale;
    private final float offsetX, offsetY, offsetZ;

    private ItemStack itemStack;
    private ItemCameraTransforms.TransformType transformType;
    private EntityLivingBase owner;

    private class CustomItemOverrideList extends ItemOverrideList {

        public CustomItemOverrideList(List<ItemOverride> overridesIn) {
            super(overridesIn);
        }

        @Override
        public IBakedModel handleItemState(
            IBakedModel originalModel,
            ItemStack stack,
            World world,
            EntityLivingBase entity
        ) {
            CustomTileEntityItemRenderer.this.itemStack = stack;
            CustomTileEntityItemRenderer.this.owner = entity;
            return super.handleItemState(originalModel, stack, world, entity);
        }
    }

    public CustomTileEntityItemRenderer(
        ModelBase model,
        ResourceLocation textureResource,
        Consumer<TileEntity> positioning,
        BiConsumer<
            ItemStack,
            ItemCameraTransforms.TransformType
        > itemPositioning,
        float displayScale,
        float offsetX,
        float offsetY,
        float offsetZ
    ) {
        this.model = model;
        this.textureResource = textureResource;
        this.positioning = positioning;
        this.itemPositioning = itemPositioning;
        this.displayScale = displayScale;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
    }

    @Override
    public List<BakedQuad> getQuads(
        IBlockState state,
        EnumFacing side,
        long rand
    ) {
        if (net.minecraftforge.common.ForgeModContainer.allowEmissiveItems) {
            return Collections.emptyList();
        }

        if (itemStack == null) {
            return Collections.emptyList();
        }

        if (
            transformType == null ||
            transformType == ItemCameraTransforms.TransformType.GROUND ||
            transformType == ItemCameraTransforms.TransformType.GUI ||
            transformType ==
                ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND ||
            transformType ==
                ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND
        ) {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder worldrenderer = tessellator.getBuffer();
            tessellator.draw();
            GlStateManager.pushMatrix();

            int currentTextureId = GlStateManager.glGetInteger(
                GL11.GL_TEXTURE_BINDING_2D
            );

            renderItem();

            if (currentTextureId != 0) {
                GlStateManager.bindTexture(currentTextureId);
            }

            GlStateManager.popMatrix();
            worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
        }

        this.owner = null;
        this.itemStack = null;
        this.transformType = null;

        return Collections.emptyList();
    }

    private void renderItem() {
        GlStateManager.pushMatrix();

        if (transformType == ItemCameraTransforms.TransformType.GUI) {
            GlStateManager.scale(0.45F, 0.45F, 0.45F);
            GlStateManager.translate(0.3F, 1.0F, 0.0F);
            GlStateManager.rotate(30F, 1, 0, 0);
            GlStateManager.rotate(45F, 0, 1, 0);
        } else if (transformType == ItemCameraTransforms.TransformType.GROUND) {
            GlStateManager.scale(0.25F, 0.25F, 0.25F);
            GlStateManager.translate(0.0F, 0.5F, 0.0F);
        } else if (
            transformType ==
                ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND ||
            transformType ==
                ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND
        ) {
            GlStateManager.scale(0.375F, 0.375F, 0.375F);
            GlStateManager.translate(0.0F, 0.5F, 0.0F);
            GlStateManager.rotate(75F, 1, 0, 0);
            GlStateManager.rotate(45F, 0, 1, 0);
        } else if (
            transformType ==
                ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND ||
            transformType ==
                ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND
        ) {
            GlStateManager.scale(0.4F, 0.4F, 0.4F);
            GlStateManager.translate(0.0F, 0.5F, 0.0F);
            GlStateManager.rotate(0F, 1, 0, 0);
            GlStateManager.rotate(45F, 0, 1, 0);
        }

        GlStateManager.scale(displayScale, displayScale, displayScale);
        GlStateManager.translate(offsetX, offsetY, offsetZ);

        MC.getTextureManager().bindTexture(textureResource);

        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();

        GlStateManager.scale(1.0F, -1.0F, -1.0F);
        GlStateManager.translate(0.5F, -0.5F, -0.5F);

        if (itemPositioning != null) {
            itemPositioning.accept(itemStack, transformType);
        }

        if (positioning != null) {
            positioning.accept(null);
        }

        model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);

        GlStateManager.popAttrib();
        GlStateManager.popMatrix();

        GlStateManager.popMatrix();
    }

    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return MC.getTextureMapBlocks().getMissingSprite();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return new CustomItemOverrideList(Collections.emptyList());
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(
        ItemCameraTransforms.TransformType cameraTransformType
    ) {
        this.transformType = cameraTransformType;
        return Pair.of(this, null);
    }
}
