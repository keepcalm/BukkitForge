package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IInventory
{
    /**
     * Returns the number of slots in the inventory.
     */
    int getSizeInventory();

    /**
     * Returns the stack in slot i
     */
    ItemStack getStackInSlot(int i);

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     */
    ItemStack decrStackSize(int i, int j);

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     */
    ItemStack getStackInSlotOnClosing(int i);

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    void setInventorySlotContents(int i, ItemStack itemstack);

    /**
     * Returns the name of the inventory.
     */
    String getInvName();

    /**
     * If this returns false, the inventory name will be used as an unlocalized name, and translated into the player's
     * language. Otherwise it will be used directly.
     */
    boolean isInvNameLocalized();

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
     * this more of a set than a get?*
     */
    int getInventoryStackLimit();

    /**
     * Called when an the contents of an Inventory change, usually
     */
    void onInventoryChanged();

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    boolean isUseableByPlayer(EntityPlayer entityplayer);

    void openChest();

    void closeChest();

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
    boolean isStackValidForSlot(int i, ItemStack itemstack);
    
    // CraftBukkit start
    ItemStack[] getContents();

    void setMaxStackSize(int size);

    int MAX_STACK = 64;
    // CraftBukkit end
}
