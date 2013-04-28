package org.bukkit.craftbukkit.v1_5_R2.block;

import net.minecraft.command.WrongUsageException;
import net.minecraft.tileentity.TileEntityBrewingStand;

import org.bukkit.block.Block;
import org.bukkit.block.BrewingStand;
import org.bukkit.craftbukkit.v1_5_R2.CraftChunk;
import org.bukkit.craftbukkit.v1_5_R2.CraftServer;
import org.bukkit.craftbukkit.v1_5_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_5_R2.inventory.CraftInventoryBrewer;
import org.bukkit.inventory.BrewerInventory;
//import org.bukkit.craftbukkit.CraftWorld;
//import org.bukkit.craftbukkit.inventory.CraftInventoryBrewer;

public class CraftBrewingStand extends CraftBlockState implements BrewingStand {
    private final CraftWorld world;
    private final TileEntityBrewingStand brewingStand;

    public CraftBrewingStand(Block block) {
        super(block);

        world = (CraftWorld) block.getWorld();
        brewingStand = (TileEntityBrewingStand) world.getTileEntityAt(getX(), getY(), getZ());
    }

    public CraftBrewingStand(int xCoord, int yCoord, int zCoord, net.minecraft.world.World world) {
    	
    	super(new CraftBlock(new CraftChunk(world.getChunkFromBlockCoords(xCoord, zCoord)), xCoord, yCoord, zCoord));
    	
        if (world.getBlockId(xCoord, yCoord, zCoord) != net.minecraft.block.Block.brewingStand.blockID) {
        	throw new WrongUsageException(String.format("No brewing stand at %s,%s,%s!", new Object[] {xCoord, yCoord, zCoord})) ;
        }
        
        this.brewingStand = (TileEntityBrewingStand) world.getBlockTileEntity(xCoord, yCoord, zCoord);
        this.world = (CraftWorld) CraftServer.instance().getWorld(world.provider.dimensionId);
	}

	public BrewerInventory getInventory() {
        return new CraftInventoryBrewer(brewingStand);
    }

    @Override
    public boolean update(boolean force) {
        boolean result = super.update(force);

        if (result) {
            brewingStand.updateEntity();
        }

        return result;
    }

    public int getBrewingTime() {
        return brewingStand.getBrewTime();
    }

    public void setBrewingTime(int brewTime) {
        brewingStand.setBrewTime(brewTime);
    }
}
