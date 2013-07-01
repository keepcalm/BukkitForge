package org.bukkit.craftbukkit.v1_5_R3.block;

import net.minecraft.tileentity.TileEntityChest;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_5_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_5_R3.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_5_R3.inventory.CraftInventoryDoubleChest;
import org.bukkit.inventory.Inventory;
//import org.bukkit.craftbukkit.v1_5_R3.CraftWorld;
//import org.bukkit.craftbukkit.v1_5_R3.inventory.CraftInventory;
//import org.bukkit.craftbukkit.v1_5_R3.inventory.CraftInventoryDoubleChest;

public class CraftChest extends CraftBlockState implements Chest {
    private final CraftWorld world;
    private final TileEntityChest chest;

    public CraftChest(final Block block) {
        super(block);

        world = (CraftWorld) block.getWorld();
        chest = (TileEntityChest) world.getTileEntityAt(getX(), getY(), getZ());
    }

    public Inventory getBlockInventory() {
        return new CraftInventory(chest);
    }

    public Inventory getInventory() {
        int x = getX();
        int y = getY();
        int z = getZ();
        // The logic here is basically identical to the logic in BlockChest.interact
        CraftInventory inventory = new CraftInventory(chest);
        if (world.getBlockTypeIdAt(x - 1, y, z) == Material.CHEST.getId()) {
            CraftInventory left = new CraftInventory((TileEntityChest)world.getHandle().getBlockTileEntity(x - 1, y, z));
            inventory = new CraftInventoryDoubleChest(left, inventory);
        }
        if (world.getBlockTypeIdAt(x + 1, y, z) == Material.CHEST.getId()) {
            CraftInventory right = new CraftInventory((TileEntityChest) world.getHandle().getBlockTileEntity(x + 1, y, z));
            inventory = new CraftInventoryDoubleChest(inventory, right);
        }
        if (world.getBlockTypeIdAt(x, y, z - 1) == Material.CHEST.getId()) {
            CraftInventory left = new CraftInventory((TileEntityChest) world.getHandle().getBlockTileEntity(x, y, z - 1));
            inventory = new CraftInventoryDoubleChest(left, inventory);
        }
        if (world.getBlockTypeIdAt(x, y, z + 1) == Material.CHEST.getId()) {
            CraftInventory right = new CraftInventory((TileEntityChest) world.getHandle().getBlockTileEntity(x, y, z + 1));
            inventory = new CraftInventoryDoubleChest(inventory, right);
        }
        return inventory;
    }

    @Override
    public boolean update(boolean force) {
        boolean result = super.update(force);

        if (result) {
            chest.updateEntity();
        }

        return result;
    }
}
