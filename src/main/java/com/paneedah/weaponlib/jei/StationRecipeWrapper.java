package com.paneedah.weaponlib.jei;

import com.paneedah.weaponlib.crafting.CraftingEntry;
import com.paneedah.weaponlib.crafting.ICraftingRecipe;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class StationRecipeWrapper implements IRecipeWrapper {

    private final ICraftingRecipe recipe;

    public StationRecipeWrapper(ICraftingRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        List<List<ItemStack>> inputs = new ArrayList<>();

        for (CraftingEntry entry : recipe.getCraftingRecipe()) {
            List<ItemStack> matchingStacks = new ArrayList<>();
            for (ItemStack stack : entry.getIngredient().getMatchingStacks()) {
                ItemStack copy = stack.copy();
                copy.setCount(entry.getCount());
                matchingStacks.add(copy);
            }
            inputs.add(matchingStacks);
        }

        ingredients.setInputLists(VanillaTypes.ITEM, inputs);
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getOutput());
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return new ArrayList<>();
    }

    @Override
    public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
        return false;
    }
}
