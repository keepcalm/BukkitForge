package keepcalm.mods.bukkit.bukkitAPI.block;

import keepcalm.mods.bukkit.bukkitAPI.BukkitChunk;
import keepcalm.mods.bukkit.bukkitAPI.BukkitWorld;
import keepcalm.mods.bukkit.bukkitAPI.inventory.BukkitInventoryFurnace;
import net.minecraft.tileentity.TileEntityFurnace;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.inventory.FurnaceInventory;
//import org.bukkit.craftbukkit.BukkitWorld;
//import org.bukkit.craftbukkit.inventory.BukkitInventoryFurnace;

public class BukkitFurnace extends BukkitBlockState implements Furnace {
    private final BukkitWorld world;
    private final TileEntityFurnace furnace;

    public BukkitFurnace(final Block block) {
        super(block);

        world = (BukkitWorld) block.getWorld();
        furnace = (TileEntityFurnace) world.getTileEntityAt(getX(), getY(), getZ());
    }

    public BukkitFurnace(int xCoord, int yCoord, int zCoord, World worldObj) {
		super( new BukkitBlock(new BukkitChunk(worldObj.getChunkFromBlockCoords(yCoord, zCoord)), xCoord, yCoord, zCoord));
		world = (BukkitWorld) Bukkit.getWorld(worldObj.getWorldInfo().getWorldName());
		furnace = (TileEntityFurnace) world.getTileEntityAt(xCoord, yCoord, zCoord);
	}

	public FurnaceInventory getInventory() {
        return new BukkitInventoryFurnace(furnace, this);
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
