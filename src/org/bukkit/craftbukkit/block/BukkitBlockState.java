package org.bukkit.craftbukkit.block;

import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.BukkitChunk;
import org.bukkit.craftbukkit.BukkitWorld;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class BukkitBlockState implements BlockState {
    private final BukkitWorld world;
    private final BukkitChunk chunk;
    private final int x;
    private final int y;
    private final int z;
    protected int type;
    protected MaterialData data;
    protected final byte light;

    public BukkitBlockState(final Block block) {
        this.world = (BukkitWorld) block.getWorld();
        this.x = block.getX();
        this.y = block.getY();
        this.z = block.getZ();
        this.type = block.getTypeId();
        this.light = block.getLightLevel();
        this.chunk = (BukkitChunk) block.getChunk();

        createData(block.getData());
    }

    public static BukkitBlockState getBlockState(net.minecraft.world.World world, int x, int y, int z) {
    	/* A bit overcomplicated... */
        return new BukkitBlockState(new BukkitBlock(new BukkitChunk(world.getChunkFromBlockCoords(x, z)), x, y, z));
    }

    public World getWorld() {
        return world;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public Chunk getChunk() {
        return chunk;
    }

    public void setData(final MaterialData data) {
        Material mat = getType();

        if ((mat == null) || (mat.getData() == null)) {
            this.data = data;
        } else {
            if ((data.getClass() == mat.getData()) || (data.getClass() == MaterialData.class)) {
                this.data = data;
            } else {
                throw new IllegalArgumentException("Provided data is not of type "
                        + mat.getData().getName() + ", found " + data.getClass().getName());
            }
        }
    }

    public MaterialData getData() {
        return data;
    }

    public void setType(final Material type) {
        setTypeId(type.getId());
    }

    public boolean setTypeId(final int type) {
        if (this.type != type) {
            this.type = type;

            createData((byte) 0);
        }
        return true;
    }

    public Material getType() {
        return Material.getMaterial(getTypeId());
    }

    public int getTypeId() {
        return type;
    }

    public byte getLightLevel() {
        return light;
    }

    public Block getBlock() {
        return world.getBlockAt(x, y, z);
    }

    public boolean update() {
        return update(false);
    }

    public boolean update(boolean force) {
        Block block = getBlock();

        synchronized (block) {
            if (block.getType() != this.getType()) {
                if (force) {
                    block.setTypeId(this.getTypeId());
                } else {
                    return false;
                }
            }

            block.setData(getRawData());
            world.getHandle().notifyBlockChange(x, y, z, block.getTypeId());
        }

        return true;
    }

    private void createData(final byte data) {
        Material mat = getType();
        if (mat == null || mat.getData() == null) {
            this.data = new MaterialData(type, data);
        } else {
            this.data = mat.getNewData(data);
        }
    }

    public byte getRawData() {
        return data.getData();
    }

    public Location getLocation() {
        return new Location(world, x, y, z);
    }

    public void setRawData(byte data) {
        this.data.setData(data);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BukkitBlockState other = (BukkitBlockState) obj;
        if (this.world != other.world && (this.world == null || !this.world.equals(other.world))) {
            return false;
        }
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        if (this.z != other.z) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        if (this.data != other.data && (this.data == null || !this.data.equals(other.data))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + (this.world != null ? this.world.hashCode() : 0);
        hash = 73 * hash + this.x;
        hash = 73 * hash + this.y;
        hash = 73 * hash + this.z;
        hash = 73 * hash + this.type;
        hash = 73 * hash + (this.data != null ? this.data.hashCode() : 0);
        return hash;
    }

    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        chunk.getBukkitWorld().getBlockMetadata().setMetadata(getBlock(), metadataKey, newMetadataValue);
    }

    public List<MetadataValue> getMetadata(String metadataKey) {
        return chunk.getBukkitWorld().getBlockMetadata().getMetadata(getBlock(), metadataKey);
    }

    public boolean hasMetadata(String metadataKey) {
        return chunk.getBukkitWorld().getBlockMetadata().hasMetadata(getBlock(), metadataKey);
    }

    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        chunk.getBukkitWorld().getBlockMetadata().removeMetadata(getBlock(), metadataKey, owningPlugin);
    }

	@Override
	public Location getLocation(Location loc) {
		return this.getBlock().getLocation();
	}
}
