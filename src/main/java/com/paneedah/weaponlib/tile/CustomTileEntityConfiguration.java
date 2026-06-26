package com.paneedah.weaponlib.tile;

import com.paneedah.weaponlib.ClientEventHandler;
import com.paneedah.weaponlib.ModContext;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.paneedah.mwc.ProjectConstants.ID;

public class CustomTileEntityConfiguration<T extends CustomTileEntityConfiguration<T>> {

    private Material material;
    private String name;
    private String textureName;
    private CreativeTabs creativeTab;
    private float hardness = 6f;
    private float resistance = 600000f;
    private String modelClassName;
    private final AtomicInteger counter = new AtomicInteger(10000);
    private final Supplier<Integer> entityIdSupplier = () -> counter.incrementAndGet();
    private Consumer<TileEntity> positioning = tileEntity -> {};
    private BiConsumer<ItemStack, net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType> itemPositioning = (itemStack, transformType) -> {};
    private Function<IBlockState, AxisAlignedBB> boundingBox;
    private float displayScale = 1.0F;
    private float displayOffsetX = 0F;
    private float displayOffsetY = 0F;
    private float displayOffsetZ = 0F;

    public T withDisplayScale(float scale) {
        this.displayScale = scale;
        return safeCast(this);
    }

    public T withDisplayOffset(float x, float y, float z) {
        this.displayOffsetX = x;
        this.displayOffsetY = y;
        this.displayOffsetZ = z;
        return safeCast(this);
    }

    private T safeCast(CustomTileEntityConfiguration<T> input) {
        return (T) input;
    }

    public T withMaterial(Material material) {
        this.material = material;
        return safeCast(this);
    }

    public T withName(String name) {
        this.name = name;
        return safeCast(this);
    }

    public T withTextureName(String textureName) {
        this.textureName = textureName;
        return safeCast(this);
    }

    public T withCreativeTab(CreativeTabs creativeTab) {
        this.creativeTab = creativeTab;
        return safeCast(this);
    }

    public T withHardness(float hardness) {
        this.hardness = hardness;
        return safeCast(this);
    }

    public T withResistance(float resistance) {
        this.resistance = resistance;
        return safeCast(this);
    }

    public T withModelClassName(String modelClassName) {
        this.modelClassName = modelClassName;
        return safeCast(this);
    }

    public T withPositioning(Consumer<TileEntity> positioning) {
        this.positioning = positioning;
        return safeCast(this);
    }

    public T withItemPositioning(BiConsumer<ItemStack, net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType> itemPositioning) {
        this.itemPositioning = itemPositioning;
        return safeCast(this);
    }

    public T withBoundingBox(Function<IBlockState, AxisAlignedBB> boundingBox) {
        this.boundingBox = boundingBox;
        return safeCast(this);
    }

    public T withBoundingBox(double x1, double y1, double z1, double x2, double y2, double z2) {
        AxisAlignedBB bb = new AxisAlignedBB(x1, y1, z1, x2, y2, z2);
        this.boundingBox = state -> bb;
        return safeCast(this);
    }

    protected Class<? extends TileEntity> getBaseClass() {
        return CustomTileEntity.class;
    }


    protected Class<CustomTileEntity<T>> createTileEntityClass() {
        int modEntityId = entityIdSupplier.get();
        return (Class<CustomTileEntity<T>>) CustomTileEntityClassFactory.getInstance().generateEntitySubclass(
                getBaseClass(), modEntityId, this);
    }

    public void build(ModContext modContext) {

        ModelBase model = null;
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            try {
                model = (ModelBase) Class.forName(modelClassName).newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (this.boundingBox == null && model != null) {
            AxisAlignedBB aabb = ModelAABB.compute(model);
            this.boundingBox = state -> aabb;
        }

        Class<? extends TileEntity> tileEntityClass = createTileEntityClass();

        CustomTileEntityBlock tileEntityBlock = new CustomTileEntityBlock(material, tileEntityClass);
        if (!FMLCommonHandler.instance().getSide().isServer()) {
            ClientEventHandler.BLANKMAPPED_LIST.add(tileEntityBlock);
        }
        tileEntityBlock.setTranslationKey(ID + "_" + name);
        tileEntityBlock.setHardness(hardness);
        tileEntityBlock.setResistance(resistance);
        tileEntityBlock.setCreativeTab(creativeTab);
        tileEntityBlock.setBoundingBox(boundingBox);
        ResourceLocation textureResource = new ResourceLocation(ID, textureName);
        GameRegistry.registerTileEntity(tileEntityClass, new ResourceLocation(ID, "tile" + name));

        //System.out.println("RUNNING!");

        if (tileEntityBlock.getRegistryName() == null) {
            if (tileEntityBlock.getTranslationKey().length() < ID.length() + 2 + 5) {
                throw new IllegalArgumentException("Unlocalize block name too short " + tileEntityBlock.getTranslationKey());
            }
            String unlocalizedName = tileEntityBlock.getTranslationKey().toLowerCase();
            String registryName = unlocalizedName.substring(5 + ID.length() + 1);
            tileEntityBlock.setRegistryName(ID, registryName);
        }

        ForgeRegistries.BLOCKS.register(tileEntityBlock);
        ItemBlock itemBlock = new ItemBlock(tileEntityBlock);
        // TODO: introduce registerItem()

        Object itemRenderer = null;
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            itemRenderer = RendererRegistration.createItemRenderer(model, textureResource, positioning, itemPositioning, displayScale, displayOffsetX, displayOffsetY, displayOffsetZ);
        }

        modContext.registerRenderableItem(tileEntityBlock.getRegistryName(), itemBlock, itemRenderer);

        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            RendererRegistration.registerRenderableEntity(modContext, name, tileEntityClass, model,
                    textureResource, positioning, tileEntityBlock, displayScale, displayOffsetX, displayOffsetY, displayOffsetZ);
        }
    }

    private static class RendererRegistration {
        /*
         * This method is wrapped into a static class to facilitate conditional client-side only loading
         */
        private static Object createItemRenderer(ModelBase model, ResourceLocation textureResource, Consumer<TileEntity> positioning, BiConsumer<ItemStack, net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType> itemPositioning, float displayScale, float displayOffsetX, float displayOffsetY, float displayOffsetZ) {
            return new CustomTileEntityItemRenderer(model, textureResource, positioning, itemPositioning, displayScale, displayOffsetX, displayOffsetY, displayOffsetZ);
        }

        private static <T extends CustomTileEntityConfiguration<T>> void registerRenderableEntity(
                ModContext context, String name, Class<? extends TileEntity> tileEntityClass, ModelBase model,
                ResourceLocation textureResource, Consumer<TileEntity> positioning, CustomTileEntityBlock tileEntityBlock, float displayScale, float displayOffsetX, float displayOffsetY, float displayOffsetZ) {
            
            ClientRegistry.bindTileEntitySpecialRenderer(tileEntityClass, (TileEntitySpecialRenderer) new CustomTileEntityRenderer(model, textureResource, positioning, displayScale, displayOffsetX, displayOffsetY, displayOffsetZ));
        }
    }
}
