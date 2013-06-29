package net.minecraft.entity.player;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ReportedException;

public class InventoryPlayer implements IInventory
{
    /**
     * An array of 36 item stacks indicating the main player inventory (including the visible bar).
     */
    public ItemStack[] mainInventory = new ItemStack[36];

    /** An array of 4 item stacks containing the currently worn armor pieces. */
    public ItemStack[] armorInventory = new ItemStack[4];

    /** The index of the currently held item (0-8). */
    public int currentItem = 0;
    @SideOnly(Side.CLIENT)

    /** The current ItemStack. */
    private ItemStack currentItemStack;

    /** The player whose inventory this is. */
    public EntityPlayer player;
    private ItemStack itemStack;

    /**
     * Set true whenever the inventory changes. Nothing sets it false so you will have to write your own code to check
     * it and reset the value.
     */
    public boolean inventoryChanged = false;

    public InventoryPlayer(EntityPlayer par1EntityPlayer)
    {
        this.player = par1EntityPlayer;
    }
    
   // CraftBukkit start
    private int maxStack = MAX_STACK;

    public ItemStack[] getContents()
    {
        return this.mainInventory;
    }

    public ItemStack[] getArmorContents()
    {
        return this.armorInventory;
    }

    public void setMaxStackSize(int size)
    {
        maxStack = size;
    }
    // CraftBukkit end

    /**
     * Returns the item stack currently held by the player.
     */
    public ItemStack getCurrentItem()
    {
        return this.currentItem < 9 && this.currentItem >= 0 ? this.mainInventory[this.currentItem] : null;
    }

    /**
     * Get the size of the player hotbar inventory
     */
    public static int getHotbarSize()
    {
        return 9;
    }

    /**
     * Returns a slot index in main inventory containing a specific itemID
     */
    private int getInventorySlotContainItem(int par1)
    {
        for (int j = 0; j < this.mainInventory.length; ++j)
        {
            if (this.mainInventory[j] != null && this.mainInventory[j].itemID == par1)
            {
                return j;
            }
        }

        return -1;
    }

    @SideOnly(Side.CLIENT)
    private int getInventorySlotContainItemAndDamage(int par1, int par2)
    {
        for (int k = 0; k < this.mainInventory.length; ++k)
        {
            if (this.mainInventory[k] != null && this.mainInventory[k].itemID == par1 && this.mainInventory[k].getItemDamage() == par2)
            {
                return k;
            }
        }

        return -1;
    }

    /**
     * stores an itemstack in the users inventory
     */
    private int storeItemStack(ItemStack par1ItemStack)
    {
        for (int i = 0; i < this.mainInventory.length; ++i)
        {
            if (this.mainInventory[i] != null && this.mainInventory[i].itemID == par1ItemStack.itemID && this.mainInventory[i].isStackable() && this.mainInventory[i].stackSize < this.mainInventory[i].getMaxStackSize() && this.mainInventory[i].stackSize < this.getInventoryStackLimit() && (!this.mainInventory[i].getHasSubtypes() || this.mainInventory[i].getItemDamage() == par1ItemStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(this.mainInventory[i], par1ItemStack))
            {
                return i;
            }
        }

        return -1;
    }

    /**
     * Returns the first item stack that is empty.
     */
    public int getFirstEmptyStack()
    {
        for (int i = 0; i < this.mainInventory.length; ++i)
        {
            if (this.mainInventory[i] == null)
            {
                return i;
            }
        }

        return -1;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Sets a specific itemID as the current item being held (only if it exists on the hotbar)
     */
    public void setCurrentItem(int par1, int par2, boolean par3, boolean par4)
    {
        boolean flag2 = true;
        this.currentItemStack = this.getCurrentItem();
        int k;

        if (par3)
        {
            k = this.getInventorySlotContainItemAndDamage(par1, par2);
        }
        else
        {
            k = this.getInventorySlotContainItem(par1);
        }

        if (k >= 0 && k < 9)
        {
            this.currentItem = k;
        }
        else
        {
            if (par4 && par1 > 0)
            {
                int l = this.getFirstEmptyStack();

                if (l >= 0 && l < 9)
                {
                    this.currentItem = l;
                }

                this.func_70439_a(Item.itemsList[par1], par2);
            }
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * Switch the current item to the next one or the previous one
     */
    public void changeCurrentItem(int par1)
    {
        if (par1 > 0)
        {
            par1 = 1;
        }

        if (par1 < 0)
        {
            par1 = -1;
        }

        for (this.currentItem -= par1; this.currentItem < 0; this.currentItem += 9)
        {
            ;
        }

        while (this.currentItem >= 9)
        {
            this.currentItem -= 9;
        }
    }

    /**
     * Clear this player's inventory, using the specified ID and metadata as filters or -1 for no filter.
     */
    public int clearInventory(int par1, int par2)
    {
        int k = 0;
        int l;
        ItemStack itemstack;

        for (l = 0; l < this.mainInventory.length; ++l)
        {
            itemstack = this.mainInventory[l];

            if (itemstack != null && (par1 <= -1 || itemstack.itemID == par1) && (par2 <= -1 || itemstack.getItemDamage() == par2))
            {
                k += itemstack.stackSize;
                this.mainInventory[l] = null;
            }
        }

        for (l = 0; l < this.armorInventory.length; ++l)
        {
            itemstack = this.armorInventory[l];

            if (itemstack != null && (par1 <= -1 || itemstack.itemID == par1) && (par2 <= -1 || itemstack.getItemDamage() == par2))
            {
                k += itemstack.stackSize;
                this.armorInventory[l] = null;
            }
        }

        return k;
    }

    @SideOnly(Side.CLIENT)
    public void func_70439_a(Item par1Item, int par2)
    {
        if (par1Item != null)
        {
            int j = this.getInventorySlotContainItemAndDamage(par1Item.itemID, par2);

            if (j >= 0)
            {
                this.mainInventory[j] = this.mainInventory[this.currentItem];
            }

            if (this.currentItemStack != null && this.currentItemStack.isItemEnchantable() && this.getInventorySlotContainItemAndDamage(this.currentItemStack.itemID, this.currentItemStack.getItemDamageForDisplay()) == this.currentItem)
            {
                return;
            }

            this.mainInventory[this.currentItem] = new ItemStack(Item.itemsList[par1Item.itemID], 1, par2);
        }
    }

    /**
     * This function stores as many items of an ItemStack as possible in a matching slot and returns the quantity of
     * left over items.
     */
    private int storePartialItemStack(ItemStack par1ItemStack)
    {
        int i = par1ItemStack.itemID;
        int j = par1ItemStack.stackSize;
        int k;

        if (par1ItemStack.getMaxStackSize() == 1)
        {
            k = this.getFirstEmptyStack();

            if (k < 0)
            {
                return j;
            }
            else
            {
                if (this.mainInventory[k] == null)
                {
                    this.mainInventory[k] = ItemStack.copyItemStack(par1ItemStack);
                }

                return 0;
            }
        }
        else
        {
            k = this.storeItemStack(par1ItemStack);

            if (k < 0)
            {
                k = this.getFirstEmptyStack();
            }

            if (k < 0)
            {
                return j;
            }
            else
            {
                if (this.mainInventory[k] == null)
                {
                    this.mainInventory[k] = new ItemStack(i, 0, par1ItemStack.getItemDamage());

                    if (par1ItemStack.hasTagCompound())
                    {
                        this.mainInventory[k].setTagCompound((NBTTagCompound)par1ItemStack.getTagCompound().copy());
                    }
                }

                int l = j;

                if (j > this.mainInventory[k].getMaxStackSize() - this.mainInventory[k].stackSize)
                {
                    l = this.mainInventory[k].getMaxStackSize() - this.mainInventory[k].stackSize;
                }

                if (l > this.getInventoryStackLimit() - this.mainInventory[k].stackSize)
                {
                    l = this.getInventoryStackLimit() - this.mainInventory[k].stackSize;
                }

                if (l == 0)
                {
                    return j;
                }
                else
                {
                    j -= l;
                    this.mainInventory[k].stackSize += l;
                    this.mainInventory[k].animationsToGo = 5;
                    return j;
                }
            }
        }
    }

    /**
     * Decrement the number of animations remaining. Only called on client side. This is used to handle the animation of
     * receiving a block.
     */
    public void decrementAnimations()
    {
        for (int i = 0; i < this.mainInventory.length; ++i)
        {
            if (this.mainInventory[i] != null)
            {
                this.mainInventory[i].updateAnimation(this.player.worldObj, this.player, i, this.currentItem == i);
            }
        }

        for (int i = 0; i < this.armorInventory.length; i++)
        {
            if (this.armorInventory[i] != null)
            {
                this.armorInventory[i].getItem().onArmorTickUpdate(this.player.worldObj, this.player, this.armorInventory[i]);
            }
        }
    }

    /**
     * removed one item of specified itemID from inventory (if it is in a stack, the stack size will reduce with 1)
     */
    public boolean consumeInventoryItem(int par1)
    {
        int j = this.getInventorySlotContainItem(par1);

        if (j < 0)
        {
            return false;
        }
        else
        {
            if (--this.mainInventory[j].stackSize <= 0)
            {
                this.mainInventory[j] = null;
            }

            return true;
        }
    }

    /**
     * Get if a specifiied item id is inside the inventory.
     */
    public boolean hasItem(int par1)
    {
        int j = this.getInventorySlotContainItem(par1);
        return j >= 0;
    }

    /**
     * Adds the item stack to the inventory, returns false if it is impossible.
     */
    public boolean addItemStackToInventory(ItemStack par1ItemStack)
    {
        if (par1ItemStack == null)
        {
            return false;
        }
        else
        {
            try
            {
                int i;

                if (par1ItemStack.isItemDamaged())
                {
                    i = this.getFirstEmptyStack();

                    if (i >= 0)
                    {
                        this.mainInventory[i] = ItemStack.copyItemStack(par1ItemStack);
                        this.mainInventory[i].animationsToGo = 5;
                        par1ItemStack.stackSize = 0;
                        return true;
                    }
                    else if (this.player.capabilities.isCreativeMode)
                    {
                        par1ItemStack.stackSize = 0;
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
                else
                {
                    do
                    {
                        i = par1ItemStack.stackSize;
                        par1ItemStack.stackSize = this.storePartialItemStack(par1ItemStack);
                    }
                    while (par1ItemStack.stackSize > 0 && par1ItemStack.stackSize < i);

                    if (par1ItemStack.stackSize == i && this.player.capabilities.isCreativeMode)
                    {
                        par1ItemStack.stackSize = 0;
                        return true;
                    }
                    else
                    {
                        return par1ItemStack.stackSize < i;
                    }
                }
            }
            catch (Throwable throwable)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Adding item to inventory");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Item being added");
                crashreportcategory.addCrashSection("Item ID", Integer.valueOf(par1ItemStack.itemID));
                crashreportcategory.addCrashSection("Item data", Integer.valueOf(par1ItemStack.getItemDamage()));
                crashreportcategory.addCrashSectionCallable("Item name", new CallableItemName(this, par1ItemStack));
                throw new ReportedException(crashreport);
            }
        }
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     */
    public ItemStack decrStackSize(int par1, int par2)
    {
        ItemStack[] aitemstack = this.mainInventory;

        if (par1 >= this.mainInventory.length)
        {
            aitemstack = this.armorInventory;
            par1 -= this.mainInventory.length;
        }

        if (aitemstack[par1] != null)
        {
            ItemStack itemstack;

            if (aitemstack[par1].stackSize <= par2)
            {
                itemstack = aitemstack[par1];
                aitemstack[par1] = null;
                return itemstack;
            }
            else
            {
                itemstack = aitemstack[par1].splitStack(par2);

                if (aitemstack[par1].stackSize == 0)
                {
                    aitemstack[par1] = null;
                }

                return itemstack;
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     */
    public ItemStack getStackInSlotOnClosing(int par1)
    {
        ItemStack[] aitemstack = this.mainInventory;

        if (par1 >= this.mainInventory.length)
        {
            aitemstack = this.armorInventory;
            par1 -= this.mainInventory.length;
        }

        if (aitemstack[par1] != null)
        {
            ItemStack itemstack = aitemstack[par1];
            aitemstack[par1] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
    {
        ItemStack[] aitemstack = this.mainInventory;

        if (par1 >= aitemstack.length)
        {
            par1 -= aitemstack.length;
            aitemstack = this.armorInventory;
        }

        aitemstack[par1] = par2ItemStack;
    }

    /**
     * Gets the strength of the current item (tool) against the specified block, 1.0f if not holding anything.
     */
    public float getStrVsBlock(Block par1Block)
    {
        float f = 1.0F;

        if (this.mainInventory[this.currentItem] != null)
        {
            f *= this.mainInventory[this.currentItem].getStrVsBlock(par1Block);
        }

        return f;
    }

    /**
     * Writes the inventory out as a list of compound tags. This is where the slot indices are used (+100 for armor, +80
     * for crafting).
     */
    public NBTTagList writeToNBT(NBTTagList par1NBTTagList)
    {
        int i;
        NBTTagCompound nbttagcompound;

        for (i = 0; i < this.mainInventory.length; ++i)
        {
            if (this.mainInventory[i] != null)
            {
                nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte)i);
                this.mainInventory[i].writeToNBT(nbttagcompound);
                par1NBTTagList.appendTag(nbttagcompound);
            }
        }

        for (i = 0; i < this.armorInventory.length; ++i)
        {
            if (this.armorInventory[i] != null)
            {
                nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte)(i + 100));
                this.armorInventory[i].writeToNBT(nbttagcompound);
                par1NBTTagList.appendTag(nbttagcompound);
            }
        }

        return par1NBTTagList;
    }

    /**
     * Reads from the given tag list and fills the slots in the inventory with the correct items.
     */
    public void readFromNBT(NBTTagList par1NBTTagList)
    {
        this.mainInventory = new ItemStack[36];
        this.armorInventory = new ItemStack[4];

        for (int i = 0; i < par1NBTTagList.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound = (NBTTagCompound)par1NBTTagList.tagAt(i);
            int j = nbttagcompound.getByte("Slot") & 255;
            ItemStack itemstack = ItemStack.loadItemStackFromNBT(nbttagcompound);

            if (itemstack != null)
            {
                if (j >= 0 && j < this.mainInventory.length)
                {
                    this.mainInventory[j] = itemstack;
                }

                if (j >= 100 && j < this.armorInventory.length + 100)
                {
                    this.armorInventory[j - 100] = itemstack;
                }
            }
        }
    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory()
    {
        return this.mainInventory.length + 4;
    }

    /**
     * Returns the stack in slot i
     */
    public ItemStack getStackInSlot(int par1)
    {
        ItemStack[] aitemstack = this.mainInventory;

        if (par1 >= aitemstack.length)
        {
            par1 -= aitemstack.length;
            aitemstack = this.armorInventory;
        }

        return aitemstack[par1];
    }

    /**
     * Returns the name of the inventory.
     */
    public String getInvName()
    {
        return "container.inventory";
    }

    /**
     * If this returns false, the inventory name will be used as an unlocalized name, and translated into the player's
     * language. Otherwise it will be used directly.
     */
    public boolean isInvNameLocalized()
    {
        return false;
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
     * this more of a set than a get?*
     */
    public int getInventoryStackLimit()
    {
        return 64;
    }

    /**
     * Return damage vs an entity done by the current held weapon, or 1 if nothing is held
     */
    public int getDamageVsEntity(Entity par1Entity)
    {
        ItemStack itemstack = this.getStackInSlot(this.currentItem);
        return itemstack != null ? itemstack.getDamageVsEntity(par1Entity) : 1;
    }

    /**
     * Returns whether the current item (tool) can harvest from the specified block (actually get a result).
     */
    public boolean canHarvestBlock(Block par1Block)
    {
        if (par1Block.blockMaterial.isToolNotRequired())
        {
            return true;
        }
        else
        {
            ItemStack itemstack = this.getStackInSlot(this.currentItem);
            return itemstack != null ? itemstack.canHarvestBlock(par1Block) : false;
        }
    }

    /**
     * returns a player armor item (as itemstack) contained in specified armor slot.
     */
    public ItemStack armorItemInSlot(int par1)
    {
        return this.armorInventory[par1];
    }

    /**
     * Based on the damage values and maximum damage values of each armor item, returns the current armor value.
     */
    public int getTotalArmorValue()
    {
        int i = 0;

        for (int j = 0; j < this.armorInventory.length; ++j)
        {
            if (this.armorInventory[j] != null && this.armorInventory[j].getItem() instanceof ItemArmor)
            {
                int k = ((ItemArmor)this.armorInventory[j].getItem()).damageReduceAmount;
                i += k;
            }
        }

        return i;
    }

    /**
     * Damages armor in each slot by the specified amount.
     */
    public void damageArmor(int par1)
    {
        par1 /= 4;

        if (par1 < 1)
        {
            par1 = 1;
        }

        for (int j = 0; j < this.armorInventory.length; ++j)
        {
            if (this.armorInventory[j] != null && this.armorInventory[j].getItem() instanceof ItemArmor)
            {
                this.armorInventory[j].damageItem(par1, this.player);

                if (this.armorInventory[j].stackSize == 0)
                {
                    this.armorInventory[j] = null;
                }
            }
        }
    }

    /**
     * Drop all armor and main inventory items.
     */
    public void dropAllItems()
    {
        int i;

        for (i = 0; i < this.mainInventory.length; ++i)
        {
            if (this.mainInventory[i] != null)
            {
                this.player.dropPlayerItemWithRandomChoice(this.mainInventory[i], true);
                this.mainInventory[i] = null;
            }
        }

        for (i = 0; i < this.armorInventory.length; ++i)
        {
            if (this.armorInventory[i] != null)
            {
                this.player.dropPlayerItemWithRandomChoice(this.armorInventory[i], true);
                this.armorInventory[i] = null;
            }
        }
    }

    /**
     * Called when an the contents of an Inventory change, usually
     */
    public void onInventoryChanged()
    {
        this.inventoryChanged = true;
    }

    public void setItemStack(ItemStack par1ItemStack)
    {
        this.itemStack = par1ItemStack;
    }

    public ItemStack getItemStack()
    {
        return this.itemStack;
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
    {
        return this.player.isDead ? false : par1EntityPlayer.getDistanceSqToEntity(this.player) <= 64.0D;
    }

    /**
     * Returns true if the specified ItemStack exists in the inventory.
     */
    public boolean hasItemStack(ItemStack par1ItemStack)
    {
        int i;

        for (i = 0; i < this.armorInventory.length; ++i)
        {
            if (this.armorInventory[i] != null && this.armorInventory[i].isItemEqual(par1ItemStack))
            {
                return true;
            }
        }

        for (i = 0; i < this.mainInventory.length; ++i)
        {
            if (this.mainInventory[i] != null && this.mainInventory[i].isItemEqual(par1ItemStack))
            {
                return true;
            }
        }

        return false;
    }

    public void openChest() {}

    public void closeChest() {}

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
    public boolean isStackValidForSlot(int par1, ItemStack par2ItemStack)
    {
        return true;
    }

    /**
     * Copy the ItemStack contents from another InventoryPlayer instance
     */
    public void copyInventory(InventoryPlayer par1InventoryPlayer)
    {
        int i;

        for (i = 0; i < this.mainInventory.length; ++i)
        {
            this.mainInventory[i] = ItemStack.copyItemStack(par1InventoryPlayer.mainInventory[i]);
        }

        for (i = 0; i < this.armorInventory.length; ++i)
        {
            this.armorInventory[i] = ItemStack.copyItemStack(par1InventoryPlayer.armorInventory[i]);
        }

        this.currentItem = par1InventoryPlayer.currentItem;
    }
}
