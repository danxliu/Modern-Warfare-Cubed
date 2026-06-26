package com.paneedah.mwc.init;

import com.paneedah.mwc.bases.OreBase;
import com.paneedah.mwc.blocks.BarbedWireBlock;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

import static com.paneedah.mwc.ProjectConstants.ID;

@Mod.EventBusSubscriber(modid = ID)
public class MWCBlocks {

    public static OreBase copperOre;
    public static OreBase tinOre;
    public static OreBase sulfurOre;
    public static OreBase leadOre;
    public static OreBase graphiteOre;

    public static BarbedWireBlock barbedWire;

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

        ALL_BLOCKS.add(copperOre);
        ALL_BLOCKS.add(tinOre);
        ALL_BLOCKS.add(leadOre);
        ALL_BLOCKS.add(sulfurOre);
        ALL_BLOCKS.add(graphiteOre);
        ALL_BLOCKS.add(barbedWire);
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> blockRegistryEvent) {
        blockRegistryEvent.getRegistry().registerAll(ALL_BLOCKS.toArray(new Block[0]));
    }

    @SubscribeEvent
    public static void registerItemBlock(RegistryEvent.Register<Item> itemRegistryEvent) {
        Item[] items = new Item[ALL_BLOCKS.size()];

        for (int i = 0; i < ALL_BLOCKS.size(); i++) {
            items[i] = new ItemBlock(ALL_BLOCKS.get(i));
            items[i].setRegistryName(ALL_BLOCKS.get(i).getRegistryName());
        }

        itemRegistryEvent.getRegistry().registerAll(items);
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
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName().toString(), "inventory"));
        }
    }
}
