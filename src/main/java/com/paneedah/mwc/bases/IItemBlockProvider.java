package com.paneedah.mwc.bases;

import net.minecraft.item.Item;

public interface IItemBlockProvider {
    /**
     * @return The item representing this block, or null if this block should not have an item.
     */
    Item getItemBlock();
}
