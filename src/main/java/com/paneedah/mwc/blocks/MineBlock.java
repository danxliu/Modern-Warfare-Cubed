package com.paneedah.mwc.blocks;

import static com.paneedah.mwc.ProjectConstants.ID;

import com.paneedah.mwc.MWC;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class MineBlock extends Block {

    private static final float EXPLOSION_POWER = 6.0F;
    private static final int HARVEST_LEVEL = 0;
    private static final AxisAlignedBB MINE_AABB = new AxisAlignedBB(
        0.1875,
        0.0,
        0.1875,
        0.8125,
        0.15625,
        0.8125
    );

    public MineBlock() {
        super(Material.IRON);
        setRegistryName(ID, "mine");
        setTranslationKey("mine");
        setHardness(0.5F);
        setResistance(0.5F);
        setHarvestLevel("pickaxe", HARVEST_LEVEL);
        setCreativeTab(MWC.EQUIPMENT_TAB);
        setLightOpacity(0);
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(
        IBlockState state,
        IBlockAccess source,
        BlockPos pos
    ) {
        return MINE_AABB;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(
        IBlockState state,
        IBlockAccess worldIn,
        BlockPos pos
    ) {
        return NULL_AABB;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(this);
    }

    @Override
    public void onEntityCollision(
        World worldIn,
        BlockPos pos,
        IBlockState state,
        Entity entityIn
    ) {
        if (worldIn.isRemote) return;
        if (!(entityIn instanceof EntityLivingBase)) return;
        if (
            entityIn instanceof EntityPlayer &&
            ((EntityPlayer) entityIn).isSpectator()
        ) return;

        worldIn.setBlockToAir(pos);
        entityIn.setFire(5);
        worldIn.newExplosion(
            null,
            pos.getX() + 0.5,
            entityIn.posY,
            pos.getZ() + 0.5,
            EXPLOSION_POWER,
            true,
            true
        );
    }
}
