package keepcalm.mods.bukkit.bukkitAPI.inventory;

import java.util.ArrayList;
import java.util.List;

import keepcalm.mods.bukkit.bukkitAPI.entity.BukkitEntityHuman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;
//import org.bukkit.craftbukkit.entity.CraftHumanEntity;

public class BukkitInventoryCustom extends BukkitInventory {
    public BukkitInventoryCustom(InventoryHolder owner, InventoryType type) {
        super(new MinecraftInventory(owner, type));
    }

    public BukkitInventoryCustom(InventoryHolder owner, int size) {
        super(new MinecraftInventory(owner, size));
    }

    public BukkitInventoryCustom(InventoryHolder owner, int size, String title) {
        super(new MinecraftInventory(owner, size, title));
    }

    static class MinecraftInventory implements IInventory {
        private final ItemStack[] items;
        private int maxStack = 64;
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
            this.items = new ItemStack[size];
            this.title = title;
            this.viewers = new ArrayList<HumanEntity>();
            this.owner = owner;
            this.type = InventoryType.CHEST;
        }

        public int getSize() {
            return items.length;
        }

        public ItemStack getItem(int i) {
            return items[i];
        }

        public ItemStack splitStack(int i, int j) {
            ItemStack stack = this.getItem(i);
            ItemStack result;
            if (stack == null) return null;
            if (stack.stackSize <= j) {
                this.setItem(i, null);
                result = stack;
            } else {
                result = new ItemStack(stack.itemID, j, stack.getItemDamage());
                stack.stackSize -= j;
            }
            this.update();
            return result;
        }

        public ItemStack splitWithoutUpdate(int i) {
            ItemStack stack = this.getItem(i);
            ItemStack result;
            if (stack == null) return null;
            if (stack.stackSize <= 1) {
                this.setItem(i, null);
                result = stack;
            } else {
                result = new ItemStack(stack.itemID, 1, stack.getItemDamage());
                stack.stackSize -= 1;
            }
            return result;
        }

        public void setItem(int i, ItemStack itemstack) {
            items[i] = itemstack;
            if (itemstack != null && this.getMaxStackSize() > 0 && itemstack.stackSize > this.getMaxStackSize()) {
                itemstack.stackSize = this.getMaxStackSize();
            }
        }

        public String getName() {
            return title;
        }

        public int getMaxStackSize() {
            return maxStack;
        }

        public void setMaxStackSize(int size) {
            maxStack = size;
        }

        public void update() {}

        public boolean a(EntityPlayer entityhuman) {
            return true;
        }

        public ItemStack[] getContents() {
            return items;
        }

        public void onOpen(BukkitEntityHuman who) {
            viewers.add(who);
        }

        public void onClose(BukkitEntityHuman who) {
            viewers.remove(who);
        }

        public List<HumanEntity> getViewers() {
            return viewers;
        }

        public InventoryType getType() {
            return type;
        }

        public void f() {}

        public void g() {}

        public InventoryHolder getOwner() {
            return owner;
        }

        public void startOpen() {}

		@Override
		public void closeChest() {}

		@Override
		public ItemStack decrStackSize(int var1, int var2) {
			int newStackSize = items[var1].stackSize - var2;
			items[var1].stackSize = var2;
			return new ItemStack(items[var1].itemID, newStackSize, items[var1].getItemDamage());
		}

		@Override
		public String getInvName() {
			return getName();
		}

		@Override
		public int getInventoryStackLimit() {
			return this.maxStack;
		}

		@Override
		public int getSizeInventory() {
			
			return this.getSize();
		}

		@Override
		public ItemStack getStackInSlot(int var1) {
			return items[var1];
		}

		@Override
		public ItemStack getStackInSlotOnClosing(int var1) {
			return null;
		}

		@Override
		public boolean isUseableByPlayer(EntityPlayer var1) {
			return true;
		}

		@Override
		public void onInventoryChanged() {}

		@Override
		public void openChest() {
		}

		@Override
		public void setInventorySlotContents(int var1, ItemStack var2) {
			items[var1] = var2;
			
		}
    }
}
