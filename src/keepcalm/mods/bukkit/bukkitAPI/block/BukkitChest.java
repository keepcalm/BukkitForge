package keepcalm.mods.bukkit.bukkitAPI.block;

import keepcalm.mods.bukkit.bukkitAPI.BukkitWorld;
import keepcalm.mods.bukkit.bukkitAPI.inventory.BukkitInventory;
import keepcalm.mods.bukkit.bukkitAPI.inventory.BukkitInventoryDoubleChest;
import net.minecraft.src.TileEntityChest;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
//import org.bukkit.craftbukkit.BukkitWorld;
//import org.bukkit.craftbukkit.inventory.BukkitInventory;
//import org.bukkit.craftbukkit.inventory.BukkitInventoryDoubleChest;

public class BukkitChest extends BukkitBlockState implements Chest {
    private final BukkitWorld world;
    private final TileEntityChest chest;

    public BukkitChest(final Block block) {
        super(block);

        world = (BukkitWorld) block.getWorld();
        chest = (TileEntityChest) world.getTileEntityAt(getX(), getY(), getZ());
    }

    public Inventory getBlockInventory() {
        return new BukkitInventory(chest);
    }

    public Inventory getInventory() {
        int x = getX();
        int y = getY();
        int z = getZ();
        // The logic here is basically identical to the logic in BlockChest.interact
        BukkitInventory inventory = new BukkitInventory(chest);
        if (world.getBlockTypeIdAt(x - 1, y, z) == Material.CHEST.getId()) {
            BukkitInventory left = new BukkitInventory((TileEntityChest)world.getHandle().getBlockTileEntity(x - 1, y, z));
            inventory = new BukkitInventoryDoubleChest(left, inventory);
        }
        if (world.getBlockTypeIdAt(x + 1, y, z) == Material.CHEST.getId()) {
            BukkitInventory right = new BukkitInventory((TileEntityChest) world.getHandle().getBlockTileEntity(x + 1, y, z));
            inventory = new BukkitInventoryDoubleChest(inventory, right);
        }
        if (world.getBlockTypeIdAt(x, y, z - 1) == Material.CHEST.getId()) {
            BukkitInventory left = new BukkitInventory((TileEntityChest) world.getHandle().getBlockTileEntity(x, y, z - 1));
            inventory = new BukkitInventoryDoubleChest(left, inventory);
        }
        if (world.getBlockTypeIdAt(x, y, z + 1) == Material.CHEST.getId()) {
            BukkitInventory right = new BukkitInventory((TileEntityChest) world.getHandle().getBlockTileEntity(x, y, z + 1));
            inventory = new BukkitInventoryDoubleChest(inventory, right);
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
