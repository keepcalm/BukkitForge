package keepcalm.mods.bukkit.bukkitAPI.block;

import keepcalm.mods.bukkit.bukkitAPI.BukkitWorld;
import keepcalm.mods.bukkit.bukkitAPI.inventory.BukkitInventory;
import net.minecraft.block.BlockDispenser;
import net.minecraft.tileentity.TileEntityDispenser;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.inventory.Inventory;
//import org.bukkit.craftbukkit.BukkitWorld;
//import org.bukkit.craftbukkit.inventory.BukkitInventory;

public class BukkitDispenser extends BukkitBlockState implements Dispenser {
    private final BukkitWorld world;
    private final TileEntityDispenser dispenser;

    public BukkitDispenser(final Block block) {
        super(block);

        world = (BukkitWorld) block.getWorld();
        dispenser = (TileEntityDispenser) world.getTileEntityAt(getX(), getY(), getZ());
    }

    public Inventory getInventory() {
        return new BukkitInventory(dispenser);
    }

    public boolean dispense() {
        Block block = getBlock();

        synchronized (block) {
            if (block.getType() == Material.DISPENSER) {
                BlockDispenser dispense = (BlockDispenser) net.minecraft.block.Block.dispenser;

                dispense.dispense(world.getHandle(), getX(), getY(), getZ());
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean update(boolean force) {
        boolean result = super.update(force);

        if (result) {
            dispenser.updateEntity();
        }

        return result;
    }
}
