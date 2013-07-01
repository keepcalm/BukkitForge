package org.bukkit.craftbukkit.v1_5_R3.block;

import net.minecraft.block.BlockDispenser;
import net.minecraft.tileentity.TileEntityDispenser;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.craftbukkit.v1_5_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_5_R3.inventory.CraftInventory;
import org.bukkit.inventory.Inventory;
//import org.bukkit.craftbukkit.v1_5_R3.CraftWorld;
//import org.bukkit.craftbukkit.v1_5_R3.inventory.CraftInventory;

public class CraftDispenser extends CraftBlockState implements Dispenser {
    private final CraftWorld world;
    private final TileEntityDispenser dispenser;

    public CraftDispenser(final Block block) {
        super(block);

        world = (CraftWorld) block.getWorld();
        dispenser = (TileEntityDispenser) world.getTileEntityAt(getX(), getY(), getZ());
    }

    public Inventory getInventory() {
        return new CraftInventory(dispenser);
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
