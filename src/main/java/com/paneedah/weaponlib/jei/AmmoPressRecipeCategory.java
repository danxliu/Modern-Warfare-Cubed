package com.paneedah.weaponlib.jei;

import com.paneedah.mwc.ProjectConstants;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class AmmoPressRecipeCategory implements IRecipeCategory<StationRecipeWrapper> {

    public static final String UID = ProjectConstants.ID + ".ammo_press";
    private final IDrawable background;
    private final IDrawable icon;
    private final String localizedName;

    public AmmoPressRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(160, 80);
        Item ammoPress = Item.getByNameOrId(ProjectConstants.ID + ":ammo_press");
        this.icon = guiHelper.createDrawableIngredient(ammoPress != null ? new ItemStack(ammoPress) : ItemStack.EMPTY);
        this.localizedName = I18n.format("tile.ammo_press.name");
    }

    @Override
    public String getUid() {
        return UID;
    }

    @Override
    public String getTitle() {
        return localizedName;
    }

    @Override
    public String getModName() {
        return ProjectConstants.NAME;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, StationRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();

        // Output slot
        itemStacks.init(0, false, 72, 60);

        // Input slots
        int inputCount = ingredients.getInputs(VanillaTypes.ITEM).size();
        for (int i = 0; i < inputCount; i++) {
            int x = (i % 8) * 18 + 8;
            int y = (i / 8) * 18 + 10;
            itemStacks.init(i + 1, true, x, y);
        }

        itemStacks.set(ingredients);
    }
}
