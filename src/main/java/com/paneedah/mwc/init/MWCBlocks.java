package com.paneedah.mwc.init;

import static com.paneedah.mwc.ProjectConstants.ID;

import com.paneedah.mwc.bases.BlockBase;
import com.paneedah.mwc.bases.IItemBlockProvider;
import com.paneedah.mwc.bases.OreBase;
import com.paneedah.mwc.bases.PaneBase;
import com.paneedah.mwc.blocks.BarbedWireBlock;
import com.paneedah.mwc.blocks.BarbedWireFlamingBlock;
import com.paneedah.mwc.blocks.SteelBricksDoubleSlab;
import com.paneedah.mwc.blocks.SteelBricksHalfSlab;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSlab;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = ID)
public class MWCBlocks {

    public static OreBase copperOre;
    public static OreBase tinOre;
    public static OreBase sulfurOre;
    public static OreBase leadOre;
    public static OreBase graphiteOre;

    public static BarbedWireBlock barbedWire;
    public static BarbedWireFlamingBlock barbedWireFlaming;

    public static Block steelBricks;
    public static SteelBricksHalfSlab steelBricksHalfSlab;
    public static SteelBricksDoubleSlab steelBricksDoubleSlab;
    public static Block steelBars;

    public static final List<Block> ALL_BLOCKS = new ArrayList<>();

    public static void init() {
        copperOre = new OreBase("copper_ore");
        copperOre.setOreDict("oreCopper");

        tinOre = new OreBase("tin_ore");
        tinOre.setOreDict("oreTin");

        leadOre = new OreBase("lead_ore");
        leadOre.setOreDict("oreLead");

        sulfurOre = new OreBase("sulfur_ore");
        sulfurOre.setOreDict("oreSulfur");
        sulfurOre.isSmeltable(false);
        sulfurOre.setItemDropped(MWCItems.sulfurDust);
        sulfurOre.setDropAmount(2, 5);

        graphiteOre = new OreBase("graphite_ore");
        graphiteOre.setOreDict("oreGraphite");
        graphiteOre.isSmeltable(false);
        graphiteOre.setItemDropped(MWCItems.graphiteChunk);
        graphiteOre.setDropAmount(1, 3);

        barbedWire = new BarbedWireBlock();
        barbedWireFlaming = new BarbedWireFlamingBlock();

        steelBricks = new BlockBase("steel_bricks").setHardness(50.0F).setResistance(2000.0F);
        steelBricksHalfSlab = new SteelBricksHalfSlab();
        steelBricksDoubleSlab = new SteelBricksDoubleSlab();
        steelBars = new PaneBase("steel_bars", Material.IRON, true).setHardness(50.0F).setResistance(2000.0F);

        ALL_BLOCKS.add(copperOre);
        ALL_BLOCKS.add(tinOre);
        ALL_BLOCKS.add(leadOre);
        ALL_BLOCKS.add(sulfurOre);
        ALL_BLOCKS.add(graphiteOre);
        ALL_BLOCKS.add(barbedWire);
        ALL_BLOCKS.add(barbedWireFlaming);
        ALL_BLOCKS.add(steelBricks);
        ALL_BLOCKS.add(steelBricksHalfSlab);
        ALL_BLOCKS.add(steelBricksDoubleSlab);
        ALL_BLOCKS.add(steelBars);
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> blockRegistryEvent) {
        blockRegistryEvent.getRegistry().registerAll(ALL_BLOCKS.toArray(new Block[0]));
    }

    @SubscribeEvent
    public static void registerItemBlock(RegistryEvent.Register<Item> itemRegistryEvent) {
        List<Item> items = new ArrayList<>();

        for (Block block : ALL_BLOCKS) {
            Item item;
            if (block instanceof IItemBlockProvider) {
                item = ((IItemBlockProvider) block).getItemBlock();
            } else {
                item = new ItemBlock(block);
            }
            if (item != null) {
                item.setRegistryName(block.getRegistryName());
                items.add(item);
            }
        }

        itemRegistryEvent.getRegistry().registerAll(items.toArray(new Item[0]));
        registerOreDictionaryKeys(ALL_BLOCKS);
    }

    static void registerOreDictionaryKeys(List<Block> blocks) {
        for (Block block : blocks) {
            if (!(block instanceof OreBase)) continue;

            String[] oreDictKeys = ((OreBase) block).getOreDictKeys();
            if (oreDictKeys == null) continue;

            for (String oreDictKey : oreDictKeys) {
                OreDictionary.registerOre(oreDictKey, block);
            }
        }
    }

    @SubscribeEvent
    public static void registerRenders(ModelRegistryEvent modelRegistryEvent) {
        for (Block block : ALL_BLOCKS) {
            Item item = Item.getItemFromBlock(block);
            if (item != null && item != Items.AIR) {
                ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(block.getRegistryName().toString(), "inventory"));
            }
        }
    }
}
