package com.paneedah.mwc.blocks;

import com.paneedah.mwc.MWC;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

import static com.paneedah.mwc.ProjectConstants.ID;

public class BarbedWireBlock extends Block {

    public static final DamageSource BARBED_WIRE_DAMAGE = new DamageSource("barbedWire");

    private static final float DAMAGE_PER_HIT = 2.0F;
    private static final int HARVEST_LEVEL = 0;

    public BarbedWireBlock() {
        super(Material.WEB);
        setRegistryName(ID, "barbed_wire");
        setTranslationKey("barbed_wire");
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
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(this);
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        entityIn.setInWeb();

        if (worldIn.isRemote) return;
        if (!(entityIn instanceof EntityLivingBase)) return;
        if (entityIn instanceof EntityPlayer && ((EntityPlayer) entityIn).isSpectator()) return;

        entityIn.attackEntityFrom(BARBED_WIRE_DAMAGE, DAMAGE_PER_HIT);
    }
}
