package org.bukkit.craftbukkit.inventory;

import java.util.ArrayList;
import java.util.List;

import keepcalm.mods.bukkit.ToBukkit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;

import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;
//import org.bukkit.craftbukkit.entity.CraftHumanEntity;

public class CraftInventoryCustom extends CraftInventory {
    public CraftInventoryCustom(InventoryHolder owner, InventoryType type) {
        super(new MinecraftInventory(owner, type));
    }

    public CraftInventoryCustom(InventoryHolder owner, int size) {
        super(new MinecraftInventory(owner, size));
    }

    public CraftInventoryCustom(InventoryHolder owner, int size, String title) {
        super(new MinecraftInventory(owner, size, title));
    }

    static class MinecraftInventory implements net.minecraft.inventory.IInventory/*was:IInventory*/ {
        private final net.minecraft.item.ItemStack/*was:ItemStack*/[] items;
        private int maxStack = 54; // TODO: What should maxstack be
        private final List<HumanEntity> viewers;
        private final String title;
        private InventoryType type;
        private final InventoryHolder owner;

        public MinecraftInventory(InventoryHolder owner, InventoryType type) {
            this(owner, type.getDefaultSize(), type.getDefaultTitle());
            this.type = type;
        }

        public MinecraftInventory(InventoryHolder owner, int size) {
            this(owner, size, "Chest");
        }

        public MinecraftInventory(InventoryHolder owner, int size, String title) {
            this.items = new net.minecraft.item.ItemStack/*was:ItemStack*/[size];
            this.title = title;
            this.viewers = new ArrayList<HumanEntity>();
            this.owner = owner;
            this.type = InventoryType.CHEST;
        }

        public int getSizeInventory/*was:getSize*/() {
            return items.length;
        }

        public net.minecraft.item.ItemStack/*was:ItemStack*/ getStackInSlot/*was:getItem*/(int i) {
            return items[i];
        }

        public net.minecraft.item.ItemStack/*was:ItemStack*/ decrStackSize/*was:splitStack*/(int i, int j) {
            net.minecraft.item.ItemStack/*was:ItemStack*/ stack = this.getStackInSlot/*was:getItem*/(i);
            net.minecraft.item.ItemStack/*was:ItemStack*/ result;
            if (stack == null) return null;
            if (stack.stackSize/*was:count*/ <= j) {
                this.setInventorySlotContents/*was:setItem*/(i, null);
                result = stack;
            } else {
                result = CraftItemStack.createNMSItemStack(ToBukkit.itemStack(stack));  // TODO: This is convoluted
                stack.stackSize/*was:count*/ -= j;
            }
            this.onInventoryChanged/*was:update*/();
            return result;
        }

        public net.minecraft.item.ItemStack/*was:ItemStack*/ getStackInSlotOnClosing/*was:splitWithoutUpdate*/(int i) {
            net.minecraft.item.ItemStack/*was:ItemStack*/ stack = this.getStackInSlot/*was:getItem*/(i);
            net.minecraft.item.ItemStack/*was:ItemStack*/ result;
            if (stack == null) return null;
            if (stack.stackSize/*was:count*/ <= 1) {
                this.setInventorySlotContents/*was:setItem*/(i, null);
                result = stack;
            } else {
                result = CraftItemStack.createNMSItemStack(ToBukkit.itemStack(stack));
                stack.stackSize/*was:count*/ -= 1;
            }
            return result;
        }

        public void setInventorySlotContents/*was:setItem*/(int i, net.minecraft.item.ItemStack/*was:ItemStack*/ itemstack) {
            items[i] = itemstack;
            if (itemstack != null && this.getInventoryStackLimit/*was:getMaxStackSize*/() > 0 && itemstack.stackSize/*was:count*/ > this.getInventoryStackLimit/*was:getMaxStackSize*/()) {
                itemstack.stackSize/*was:count*/ = this.getInventoryStackLimit/*was:getMaxStackSize*/();
            }
        }

        public String getInvName/*was:getName*/() {
            return title;
        }

        public int getInventoryStackLimit/*was:getMaxStackSize*/() {
            return maxStack;
        }

        public void setMaxStackSize(int size) {
            maxStack = size;
        }

        public void onInventoryChanged/*was:update*/() {}

        public boolean isUseableByPlayer/*was:a_*/(net.minecraft.entity.player.EntityPlayer/*was:EntityHuman*/ entityhuman) {
            return true;
        }

        public net.minecraft.item.ItemStack/*was:ItemStack*/[] getContents() {
            return items;
        }

        public void onOpen(CraftHumanEntity who) {
            viewers.add(who);
        }

        public void onClose(CraftHumanEntity who) {
            viewers.remove(who);
        }

        public List<HumanEntity> getViewers() {
            return viewers;
        }

        public InventoryType getType() {
            return type;
        }

        public void closeChest/*was:f*/() {}

        public void g() {}

        public InventoryHolder getOwner() {
            return owner;
        }

        public void openChest/*was:startOpen*/() {}

		@Override
		public boolean func_94042_c() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean func_94041_b(int i, ItemStack itemstack) {
			// TODO Auto-generated method stub
			return false;
		}
    }
}