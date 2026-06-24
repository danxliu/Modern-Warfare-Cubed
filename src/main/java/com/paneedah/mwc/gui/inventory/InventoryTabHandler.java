package com.paneedah.mwc.gui.inventory;

import static com.paneedah.mwc.proxies.ClientProxy.MC;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class InventoryTabHandler {

    private final ArrayList<InventoryTab> tabList = new ArrayList<InventoryTab>();

    private static final InventoryTabHandler instance = new InventoryTabHandler();

    private InventoryTabHandler() {}

    public static InventoryTabHandler getInstance() {
        return instance;
    }

    public void registerTab(InventoryTab tab) {
        tabList.add(tab);
    }

    public ArrayList<InventoryTab> getTabList() {
        return tabList;
    }

    @SubscribeEvent(priority = net.minecraftforge.fml.common.eventhandler.EventPriority.LOWEST)
    @SideOnly(Side.CLIENT)
    public void guiPostInit(GuiScreenEvent.InitGuiEvent.Post event) {
        final GuiScreen gui = event.getGui();

        if (gui instanceof GuiInventory) {
            int xSize = 176;
            int ySize = 166;
            int guiLeft = (gui.width - xSize) / 2;
            int guiTop = (gui.height - ySize) / 2;

            updateTabValues(guiLeft, guiTop, StandardPlayerInventoryTab.class, event.getButtonList());
            addTabsToList(event.getButtonList());
        }
    }

    public void openInventoryGui() {
        GuiInventory inventory = new GuiInventory(MC.player);
        MC.displayGuiScreen(inventory);
    }

    public void updateTabValues(int cornerX, int cornerY, Class<?> selectedButton, List<?> existingButtons) {
        int startX = cornerX;
        Set<Integer> usedIds = new HashSet<>(); // Tab ids occupied by other mods

        if (existingButtons == null) return;
        for (Object obj : existingButtons) {
            if (obj instanceof GuiButton) {
                GuiButton btn = (GuiButton) obj;
                usedIds.add(btn.id);
                if (
                    btn.y >= cornerY - 32 &&
                    btn.y < cornerY &&
                    btn.x >= cornerX &&
                    btn.x < cornerX + 176
                ) {
                    startX = Math.max(startX, btn.x + btn.width);
                }
            }
        }

        int count = 2;
        for (int i = 0; i < tabList.size(); i++) {
            InventoryTab t = tabList.get(i);
            if (t.shouldAddToList()) {
                while (usedIds.contains(count)) {
                    count++;
                }
                t.id = count;
                t.x = startX;
                t.y = cornerY - 28;
                t.enabled = !t.getClass().equals(selectedButton);
                startX += 28; // Advance startX by the width of the tab
                count++;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void addTabsToList(List<?> buttonList) {
        for (InventoryTab tab : tabList) {
            if (tab.shouldAddToList()) {
                ((List<Object>) buttonList).add(tab);
            }
        }
    }
}
