package com.paneedah.weaponlib.jei;

import com.paneedah.mwc.ProjectConstants;
import com.paneedah.weaponlib.crafting.CraftingGroup;
import com.paneedah.weaponlib.crafting.CraftingRegistry;
import com.paneedah.weaponlib.crafting.ICraftingRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

@JEIPlugin
public class MWCJEIPlugin implements IModPlugin {

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(
                new WorkbenchRecipeCategory(registry.getJeiHelpers().getGuiHelper()),
                new AmmoPressRecipeCategory(registry.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void register(IModRegistry registry) {
        // Register Workbench recipes
        List<StationRecipeWrapper> workbenchRecipes = new ArrayList<>();
        addRecipesForGroup(workbenchRecipes, CraftingGroup.GUN);
        addRecipesForGroup(workbenchRecipes, CraftingGroup.ATTACHMENT_NORMAL);
        addRecipesForGroup(workbenchRecipes, CraftingGroup.ATTACHMENT_MODIFICATION);
        addRecipesForGroup(workbenchRecipes, CraftingGroup.GEAR);
        registry.addRecipes(workbenchRecipes, WorkbenchRecipeCategory.UID);

        // Register Ammo Press recipes
        List<StationRecipeWrapper> ammoPressRecipes = new ArrayList<>();
        addRecipesForGroup(ammoPressRecipes, CraftingGroup.BULLET);
        addRecipesForGroup(ammoPressRecipes, CraftingGroup.MAGAZINE);
        addRecipesForGroup(ammoPressRecipes, CraftingGroup.GRENADE);
        registry.addRecipes(ammoPressRecipes, AmmoPressRecipeCategory.UID);

        // Register Catalysts
        Item workbench = Item.getByNameOrId(ProjectConstants.ID + ":weapon_workbench");
        if (workbench != null) {
            registry.addRecipeCatalyst(new ItemStack(workbench), WorkbenchRecipeCategory.UID);
        }

        Item ammoPress = Item.getByNameOrId(ProjectConstants.ID + ":ammo_press");
        if (ammoPress != null) {
            registry.addRecipeCatalyst(new ItemStack(ammoPress), AmmoPressRecipeCategory.UID);
        }
    }

    private void addRecipesForGroup(List<StationRecipeWrapper> wrapperList, CraftingGroup group) {
        ArrayList<ICraftingRecipe> recipes = CraftingRegistry.getCraftingListForGroup(group);
        if (recipes == null) return;
        for (ICraftingRecipe recipe : recipes) {
            if (recipe.getOutput() != null && recipe.getCraftingRecipe() != null && recipe.getCraftingRecipe().length > 0) {
                wrapperList.add(new StationRecipeWrapper(recipe));
            }
        }
    }
}
