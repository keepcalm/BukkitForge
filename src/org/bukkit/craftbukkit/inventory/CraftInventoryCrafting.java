package org.bukkit.craftbukkit.inventory;

import java.util.Arrays;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.ShapedRecipes;

import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class CraftInventoryCrafting extends CraftInventory implements CraftingInventory {
    private final IInventory resultInventory;

    public CraftInventoryCrafting(InventoryCrafting inventory, IInventory resultInventory) {
        super(inventory);
        this.resultInventory = resultInventory;
    }

	public IInventory getResultInventory() {
        return resultInventory;
    }

    public IInventory getMatrixInventory() {
        return getInventory();
    }

    @Override
    public int getSize() {
        return getResultInventory().getSizeInventory() + getMatrixInventory().getSizeInventory();
    }

    @Override
    public void setContents(ItemStack[] items) {
        int resultLen = getMatrixInventory().getSizeInventory();
        int len = getMatrixContents().length + resultLen; //TODO
        if (len > items.length) {
            throw new IllegalArgumentException("Invalid inventory size; expected " + len + " or less");
        }
        setContents(items[0], Arrays.copyOfRange(items, 1, items.length));
    }

    @Override
    public ItemStack[] getContents() {
        ItemStack[] items = new ItemStack[getSize()];
        net.minecraft.item.ItemStack[] mcResultItems = new net.minecraft.item.ItemStack[getSize()];

        int i = 0;
        for (i = 0; i < mcResultItems.length; i++ ) {
            items[i] = new CraftItemStack(mcResultItems[i]);
        }

        net.minecraft.item.ItemStack[] mcItems = getMatrixContents();

        for (int j = 0; j < mcItems.length; j++) {
            items[i + j] = new CraftItemStack(mcItems[j]);
        }

        return items;
    }

    public void setContents(ItemStack result, ItemStack[] contents) {
        setResult(result);
        setMatrix(contents);
    }

    @Override
    public CraftItemStack getItem(int index) {
        if(index < 0 ) {
            return null;
        } else if (index < getResultInventory().getSizeInventory()) {
            net.minecraft.item.ItemStack item = getResultInventory().getStackInSlot(index);
            return item == null ? null : new CraftItemStack(item);
        } else {
            net.minecraft.item.ItemStack item = getMatrixInventory().getStackInSlot(index - getResultInventory().getSizeInventory());
            return item == null ? null : new CraftItemStack(item);
        }
    }

    @Override
    public void setItem(int index, ItemStack item) {
        if (index < getResultInventory().getSizeInventory()) {
            getResultInventory().setInventorySlotContents(index, (item == null ? null : CraftItemStack.createNMSItemStack(item)));
        } else {
            getMatrixInventory().setInventorySlotContents((index - getResultInventory().getSizeInventory()), (item == null ? null : CraftItemStack.createNMSItemStack(item)));
        }
    }

    public ItemStack[] getMatrix() {
        ItemStack[] items = new ItemStack[getSize()];
        net.minecraft.item.ItemStack[] matrix = getMatrixContents();

        for (int i = 0; i < matrix.length; i++ ) {
            items[i] = new CraftItemStack(matrix[i]);
        }

        return items;
    }

    public ItemStack getResult() {
        net.minecraft.item.ItemStack item = getResultInventory().getStackInSlot(0);
        if(item != null) return new CraftItemStack(item);
        return null;
    }

    public void setMatrix(ItemStack[] contents) {
        if (getMatrixContents().length > contents.length) {
            throw new IllegalArgumentException("Invalid inventory size; expected " + getMatrixContents().length + " or less");
        }

        net.minecraft.item.ItemStack[] mcItems = getMatrixContents();

        for (int i = 0; i < mcItems.length; i++ ) {
            if (i < contents.length) {
                ItemStack item = contents[i];
                if (item == null || item.getTypeId() <= 0) {
                    mcItems[i] = null;
                } else {
                    mcItems[i] = CraftItemStack.createNMSItemStack(item);
                }
            } else {
                mcItems[i] = null;
            }
        }
    }
    protected net.minecraft.item.ItemStack[] getResultInventoryContents() {
    	IInventory inv = getResultInventory();
    	net.minecraft.item.ItemStack[] items = new net.minecraft.item.ItemStack[inv.getSizeInventory()];
    	for (int i = 0; i < inv.getSizeInventory(); i++) {
    		items[i] = inv.getStackInSlot(i);
    	}
    	return items;
    }
    protected net.minecraft.item.ItemStack[] getMatrixContents() {
    	IInventory inv = getMatrixInventory();
    	net.minecraft.item.ItemStack[] items = new net.minecraft.item.ItemStack[inv.getSizeInventory()];
    	for (int i = 0; i < inv.getSizeInventory(); i++) {
    		items[i] = inv.getStackInSlot(0);
    	}
    	return items;
    }
    public void setResult(ItemStack item) {
        net.minecraft.item.ItemStack[] contents = getResultInventoryContents();
        if (item == null || item.getTypeId() <= 0) {
            contents[0] = null;
        } else {
            contents[0] = CraftItemStack.createNMSItemStack(item);
        }
    }

    public Recipe getRecipe() {
    	net.minecraft.item.ItemStack[] stacks = new net.minecraft.item.ItemStack[9];
    	int height = 0;
    	int width = 0;
    	for (int i = 0; i < 9; i++ ) {
    		stacks[i] = ((InventoryCrafting) getInventory()).getStackInSlot(i);
    		switch(i) {
    		case 0:
    		case 3:
    		case 6:
    			height++;
    		case 2:
    		case 5:
    		case 8:
    			width++;
    		}
    	}
    	if (getViewers().isEmpty()) {
    		return null;
    	}
    	net.minecraft.item.ItemStack recipe = CraftingManager.getInstance().findMatchingRecipe((InventoryCrafting) getInventory(), ((CraftHumanEntity) this.getViewers().get(0)).getHandle().worldObj);
    	if (recipe == null) {
    		return null;
    	}
    	ShapedRecipes j = new ShapedRecipes(width, height, stacks, recipe);
    	return new CraftShapedRecipe(new CraftItemStack(j.getRecipeOutput()), j);
        
        //return recipe == null ? null : new CraftRecipe();
    }
}
