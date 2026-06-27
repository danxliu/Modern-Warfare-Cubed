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

public class BarbedWireFlamingBlock extends Block {

    private static final float DAMAGE_PER_HIT = 4.0F;
    private static final int HARVEST_LEVEL = 0;

    public BarbedWireFlamingBlock() {
        super(Material.WEB);
        setRegistryName(ID, "barbed_wire_flaming");
        setTranslationKey("barbed_wire_flaming");
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

        entityIn.attackEntityFrom(DamageSource.IN_FIRE, DAMAGE_PER_HIT);
        entityIn.setFire(5);
    }
}
