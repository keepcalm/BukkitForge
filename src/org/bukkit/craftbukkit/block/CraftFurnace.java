package org.bukkit.craftbukkit.block;

import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.world.World;

import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.craftbukkit.CraftChunk;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.inventory.CraftInventoryFurnace;
import org.bukkit.inventory.FurnaceInventory;
//import org.bukkit.craftbukkit.CraftWorld;
//import org.bukkit.craftbukkit.inventory.CraftInventoryFurnace;

public class CraftFurnace extends CraftBlockState implements Furnace {
    private final CraftWorld world;
    private final TileEntityFurnace furnace;

    public CraftFurnace(final Block block) {
        super(block);

        world = (CraftWorld) block.getWorld();
        furnace = (TileEntityFurnace) world.getTileEntityAt(getX(), getY(), getZ());
    }

    public CraftFurnace(int xCoord, int yCoord, int zCoord, World worldObj) {
		super( new CraftBlock(new CraftChunk(worldObj.getChunkFromBlockCoords(yCoord, zCoord)), xCoord, yCoord, zCoord));
		world = (CraftWorld) CraftServer.instance().getWorld(worldObj.provider.dimensionId);
		furnace = (TileEntityFurnace) world.getTileEntityAt(xCoord, yCoord, zCoord);
	}

	public FurnaceInventory getInventory() {
        return new CraftInventoryFurnace(furnace, this);
    }

    @Override
    public boolean update(boolean force) {
        boolean result = super.update(force);

        if (result) {
            furnace.updateEntity();
        }

        return result;
    }

    public short getBurnTime() {
        return (short) furnace.furnaceBurnTime;
    }

    public void setBurnTime(short burnTime) {
        furnace.furnaceBurnTime = burnTime;
    }

    public short getCookTime() {
        return (short) furnace.furnaceCookTime;
    }

    public void setCookTime(short cookTime) {
        furnace.furnaceCookTime = cookTime;
    }
}
