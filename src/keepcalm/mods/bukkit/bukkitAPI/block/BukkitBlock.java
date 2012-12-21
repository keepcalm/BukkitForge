package keepcalm.mods.bukkit.bukkitAPI.block;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import keepcalm.mods.bukkit.bukkitAPI.BukkitChunk;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.biome.BiomeGenBase;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BlockVector;
//import org.bukkit.craftbukkit.BukkitChunk;

public class BukkitBlock implements Block {
    private final BukkitChunk chunk;
    private final int x;
    private final int y;
    private final int z;
    private static final Biome BIOME_MAPPING[];
    private static final BiomeGenBase BIOMEBASE_MAPPING[];

    public BukkitBlock(BukkitChunk chunk, int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.chunk = chunk;
    }

    

	public World getWorld() {
        return chunk.getWorld();
    }

    public Location getLocation() {
        return new Location(getWorld(), x, y, z);
    }

    public BlockVector getVector() {
        return new BlockVector(x, y, z);
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

    public void setData(final byte data) {
        chunk.getHandle().worldObj.setBlockMetadataWithNotify(x, y, z, data);
    }

    public void setData(final byte data, boolean applyPhysics) {
        if (applyPhysics) {
            chunk.getHandle().worldObj.setBlockMetadataWithNotify(x, y, z, data);
        } else {
            chunk.getHandle().worldObj.setBlockMetadata(x, y, z, data);
        }
    }

    public byte getData() {
        return (byte) chunk.getHandle().getBlockMetadata(this.x & 0xF, this.y & 0xFF, this.z & 0xF);
    }

    public void setType(final Material type) {
        setTypeId(type.getId());
    }

    public boolean setTypeId(final int type) {
        return chunk.getHandle().worldObj.setBlockWithNotify(x, y, z, type);
    }

    public boolean setTypeId(final int type, final boolean applyPhysics) {
        if (applyPhysics) {
            return setTypeId(type);
        } else {
            return chunk.getHandle().worldObj.setBlock(x, y, z, type);
        }
    }

    public boolean setTypeIdAndData(final int type, final byte data, final boolean applyPhysics) {
        if (applyPhysics) {
            return chunk.getHandle().worldObj.setBlockAndMetadataWithNotify(x, y, z, type, data);
        } else {
            boolean success = chunk.getHandle().worldObj.setBlockAndMetadata(x, y, z, type, data);
            if (success) {
                chunk.getHandle().worldObj.notifyBlockChange(x, y, z, type);
            }
            return success;
        }
    }

    public Material getType() {
        return Material.getMaterial(getTypeId());
    }

    public int getTypeId() {
        return chunk.getHandle().getBlockID(this.x & 0xF, this.y & 0xFF, this.z & 0xF);
    }

    public byte getLightLevel() {
        return (byte) chunk.getHandle().worldObj.getLightBrightness(this.x, this.y, this.z);
    }

    public byte getLightFromSky() {
        return (byte) chunk.getHandle().getSavedLightValue(EnumSkyBlock.Sky, this.x & 0xF, this.y & 0xFF, this.z & 0xF);
    }

    public byte getLightFromBlocks() {
        return (byte) chunk.getHandle().getSavedLightValue(EnumSkyBlock.Block, this.x & 0xF, this.y & 0xFF, this.z & 0xF);
    }


    public Block getFace(final BlockFace face) {
        return getRelative(face, 1);
    }

    public Block getFace(final BlockFace face, final int distance) {
        return getRelative(face, distance);
    }

    public Block getRelative(final int modX, final int modY, final int modZ) {
        return getWorld().getBlockAt(getX() + modX, getY() + modY, getZ() + modZ);
    }

    public Block getRelative(BlockFace face) {
        return getRelative(face, 1);
    }

    public Block getRelative(BlockFace face, int distance) {
        return getRelative(face.getModX() * distance, face.getModY() * distance, face.getModZ() * distance);
    }

    public BlockFace getFace(final Block block) {
        BlockFace[] values = BlockFace.values();

        for (BlockFace face : values) {
            if ((this.getX() + face.getModX() == block.getX()) &&
                (this.getY() + face.getModY() == block.getY()) &&
                (this.getZ() + face.getModZ() == block.getZ())
            ) {
                return face;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return "BukkitBlock{" + "chunk=" + chunk + ",x=" + x + ",y=" + y + ",z=" + z + ",type=" + getType() + ",data=" + getData() + '}';
    }

    /**
     * Notch uses a 0-5 to mean DOWN, UP, EAST, WEST, NORTH, SOUTH
     * in that order all over. This method is convenience to convert for us.
     *
     * @return BlockFace the BlockFace represented by this number
     */
    public static BlockFace notchToBlockFace(int notch) {
        switch (notch) {
        case 0:
            return BlockFace.DOWN;
        case 1:
            return BlockFace.UP;
        case 2:
            return BlockFace.EAST;
        case 3:
            return BlockFace.WEST;
        case 4:
            return BlockFace.NORTH;
        case 5:
            return BlockFace.SOUTH;
        default:
            return BlockFace.SELF;
        }
    }

    public static int blockFaceToNotch(BlockFace face) {
        switch (face) {
        case DOWN:
            return 0;
        case UP:
            return 1;
        case EAST:
            return 2;
        case WEST:
            return 3;
        case NORTH:
            return 4;
        case SOUTH:
            return 5;
        default:
            return 7; // Good as anything here, but technically invalid
        }
    }

    public BlockState getState() {
        Material material = getType();

        switch (material) {
        case SIGN:
        case SIGN_POST:
        case WALL_SIGN:
            return new BukkitSign(this);
        case CHEST:
            return new BukkitChest(this);
        case BURNING_FURNACE:
        case FURNACE:
            return new BukkitFurnace(this);
        case DISPENSER:
            return new BukkitDispenser(this);
        case MOB_SPAWNER:
            return new BukkitCreatureSpawner(this);
        case NOTE_BLOCK:
            return new BukkitNoteBlock(this);
        case JUKEBOX:
            return new BukkitJukebox(this);
        case BREWING_STAND:
            return new BukkitBrewingStand(this);
        default:
            return new BukkitBlockState(this);
        }
    }

    public Biome getBiome() {
        return getWorld().getBiome(x, z);
    }

    public void setBiome(Biome bio) {
        getWorld().setBiome(x, z, bio);
    }

    public static Biome biomeBaseToBiome(BiomeGenBase base) {
        if (base == null) {
            return null;
        }

        return BIOME_MAPPING[base.biomeID];
    }

    public static BiomeGenBase biomeToBiomeGenBase(Biome bio) {
        if (bio == null) {
            return null;
        }
        return BIOMEBASE_MAPPING[bio.ordinal()];
    }

    public double getTemperature() {
        return getWorld().getTemperature(x, z);
    }

    public double getHumidity() {
        return getWorld().getHumidity(x, z);
    }

    public boolean isBlockPowered() {
        return chunk.getHandle().worldObj.isBlockGettingPowered(x, y, z);
    }

    public boolean isBlockIndirectlyPowered() {
        return chunk.getHandle().worldObj.isBlockIndirectlyGettingPowered(x, y, z);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof BukkitBlock)) return false;
        BukkitBlock other = (BukkitBlock) o;

        return this.x == other.x && this.y == other.y && this.z == other.z && this.getWorld().equals(other.getWorld());
    }

    @Override
    public int hashCode() {
        return this.y << 24 ^ this.x ^ this.z ^ this.getWorld().hashCode();
    }

    public boolean isBlockFacePowered(BlockFace face) {
        return isBlockPowered();
    }

    public boolean isBlockFaceIndirectlyPowered(BlockFace face) {
        return isBlockIndirectlyPowered();
    }

    public int getBlockPower(BlockFace face) {
        int power = 0;
        BlockRedstoneWire wire = (BlockRedstoneWire) net.minecraft.block.Block.redstoneWire;
        net.minecraft.world.World world = chunk.getHandle().worldObj;
        /*if ((face == BlockFace.DOWN || face == BlockFace.SELF) && world.isBlockFacePowered(x, y - 1, z, 0)) power = wire(world, x, y - 1, z, power);
        if ((face == BlockFace.UP || face == BlockFace.SELF) && world.isBlockFacePowered(x, y + 1, z, 1)) power = wire.getPower(world, x, y + 1, z, power);
        if ((face == BlockFace.EAST || face == BlockFace.SELF) && world.isBlockFacePowered(x, y, z - 1, 2)) power = wire.getPower(world, x, y, z - 1, power);
        if ((face == BlockFace.WEST || face == BlockFace.SELF) && world.isBlockFacePowered(x, y, z + 1, 3)) power = wire.getPower(world, x, y, z + 1, power);
        if ((face == BlockFace.NORTH || face == BlockFace.SELF) && world.isBlockFacePowered(x - 1, y, z, 4)) power = wire.getPower(world, x - 1, y, z, power);
        if ((face == BlockFace.SOUTH || face == BlockFace.SELF) && world.isBlockFacePowered(x + 1, y, z, 5)) power = wire.getPower(world, x + 1, y, z, power);*/
        return isBlockIndirectlyPowered() || isBlockPowered() ? 15 : 0;
    }

    public int getBlockPower() {
        return getBlockPower(BlockFace.SELF);
    }

    public boolean isEmpty() {
        return getType() == Material.AIR;
    }

    public boolean isLiquid() {
        return (getType() == Material.WATER) || (getType() == Material.STATIONARY_WATER) || (getType() == Material.LAVA) || (getType() == Material.STATIONARY_LAVA);
    }

    public PistonMoveReaction getPistonMoveReaction() {
        return PistonMoveReaction.getById(net.minecraft.block.Block.blocksList[this.getTypeId()].blockMaterial.getMaterialMobility());
    }

    private boolean itemCausesDrops(ItemStack item) {
        net.minecraft.block.Block block = net.minecraft.block.Block.blocksList[this.getTypeId()];
        net.minecraft.item.Item itemType = item != null ? net.minecraft.item.Item.itemsList[item.getTypeId()] : null;
        return block != null && (block.blockMaterial.func_85157_q() || (itemType != null && itemType.canHarvestBlock(block)));
    }

    public boolean breakNaturally() {
        net.minecraft.block.Block block = net.minecraft.block.Block.blocksList[this.getTypeId()];
        byte data = getData();

        setTypeId(Material.AIR.getId());
        if (block != null) {
            block.dropBlockAsItemWithChance(chunk.getHandle().worldObj, x, y, z, data, 1.0F, 0);
            return true;
        }
        return false;
    }

    public boolean breakNaturally(ItemStack item) {
        if (itemCausesDrops(item)) {
            return breakNaturally();
        } else {
            return setTypeId(Material.AIR.getId());
        }
    }

    public Collection<ItemStack> getDrops() {
        List<ItemStack> drops = new ArrayList<ItemStack>();

        net.minecraft.block.Block block = net.minecraft.block.Block.blocksList[this.getTypeId()];
        if (block != null) {
            byte data = getData();
            // based on nms.Block.dropNaturally
            int count = block.quantityDropped(chunk.getHandle().worldObj.rand);
            for (int i = 0; i < count; ++i) {
                int item = block.idDropped(data, chunk.getHandle().worldObj.rand, 0);
                if (item > 0) {
                    drops.add(new ItemStack(item, 1, (short) block.getDamageValue(chunk.getHandle().worldObj, x, y, z)));
                }
            }
        }
        return drops;
    }

    public Collection<ItemStack> getDrops(ItemStack item) {
        if (itemCausesDrops(item)) {
            return getDrops();
        } else {
            return Collections.emptyList();
        }
    }

    /* Build biome index based lookup table for BiomeGenBase to Biome mapping */
    static {
        BIOME_MAPPING = new Biome[BiomeGenBase.biomeList.length];
        BIOMEBASE_MAPPING = new BiomeGenBase[Biome.values().length];
        BIOME_MAPPING[BiomeGenBase.beach.biomeID] = Biome.BEACH;
        
        BIOME_MAPPING[BiomeGenBase.swampland.biomeID] = Biome.SWAMPLAND;
        
        BIOME_MAPPING[BiomeGenBase.forest.biomeID] = Biome.FOREST;
        
        BIOME_MAPPING[BiomeGenBase.taiga.biomeID] = Biome.TAIGA;
        
        BIOME_MAPPING[BiomeGenBase.desert.biomeID] = Biome.DESERT;
        
        BIOME_MAPPING[BiomeGenBase.plains.biomeID] = Biome.PLAINS;
        
        BIOME_MAPPING[BiomeGenBase.hell.biomeID] = Biome.HELL;
        
        BIOME_MAPPING[BiomeGenBase.sky.biomeID] = Biome.SKY;
        
        BIOME_MAPPING[BiomeGenBase.river.biomeID] = Biome.RIVER;
        
        BIOME_MAPPING[BiomeGenBase.extremeHills.biomeID] = Biome.EXTREME_HILLS;
        
        BIOME_MAPPING[BiomeGenBase.ocean.biomeID] = Biome.OCEAN;
        
        BIOME_MAPPING[BiomeGenBase.frozenOcean.biomeID] = Biome.FROZEN_OCEAN;
        
        BIOME_MAPPING[BiomeGenBase.frozenRiver.biomeID] = Biome.FROZEN_RIVER;
        
        BIOME_MAPPING[BiomeGenBase.icePlains.biomeID] = Biome.ICE_PLAINS;
        
        BIOME_MAPPING[BiomeGenBase.iceMountains.biomeID] = Biome.ICE_MOUNTAINS;
        
        BIOME_MAPPING[BiomeGenBase.mushroomIsland.biomeID] = Biome.MUSHROOM_ISLAND;
        
        BIOME_MAPPING[BiomeGenBase.mushroomIslandShore.biomeID] = Biome.MUSHROOM_SHORE;
        
        BIOME_MAPPING[BiomeGenBase.desertHills.biomeID] = Biome.DESERT_HILLS;
        
        BIOME_MAPPING[BiomeGenBase.forestHills.biomeID] = Biome.FOREST_HILLS;
        
        BIOME_MAPPING[BiomeGenBase.taigaHills.biomeID] = Biome.TAIGA_HILLS;
        
        //BIOME_MAPPING[BiomeGenBase.mo.biomeID] = Biome.SMALL_MOUNTAINS;
        
        BIOME_MAPPING[BiomeGenBase.jungle.biomeID] = Biome.JUNGLE;
        
        BIOME_MAPPING[BiomeGenBase.jungleHills.biomeID] = Biome.JUNGLE_HILLS;
        
        
        
        /* Sanity check - we should have a record for each record in the BiomeGenBase.a table */
        /* Helps avoid missed biomes when we upgrade bukkit to new code with new biomes */
        boolean setSmallMountains = true;
        for (int i = 0; i < BIOME_MAPPING.length; i++) {
        	
            if ((BiomeGenBase.biomeList[i] != null) && (BIOME_MAPPING[i] == null) && setSmallMountains) {
                Bukkit.getLogger().warning("Missing Biome mapping for BiomeGenBase[" + i + "] - The Biome missing is " + BiomeGenBase.biomeList[i].biomeName);
            }
            else if ((BiomeGenBase.biomeList[i] != null) && (BIOME_MAPPING[i] == null) && !setSmallMountains) {
            	BIOME_MAPPING[i] = Biome.SMALL_MOUNTAINS;
            	setSmallMountains = true;
            }
            if (BIOME_MAPPING[i] != null) {  /* Build reverse mapping for setBiome */
                BIOMEBASE_MAPPING[BIOME_MAPPING[i].ordinal()] = BiomeGenBase.biomeList[i];
            }
        }
    }

    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        chunk.getBukkitWorld().getBlockMetadata().setMetadata(this, metadataKey, newMetadataValue);
    }

    public List<MetadataValue> getMetadata(String metadataKey) {
        return chunk.getBukkitWorld().getBlockMetadata().getMetadata(this, metadataKey);
    }

    public boolean hasMetadata(String metadataKey) {
        return chunk.getBukkitWorld().getBlockMetadata().hasMetadata(this, metadataKey);
    }

    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        chunk.getBukkitWorld().getBlockMetadata().removeMetadata(this, metadataKey, owningPlugin);
    }



	@Override
	public Location getLocation(Location loc) {
		if (loc == null) return null;
		loc.setX(x);
		loc.setY(y);
		loc.setZ(z);
		loc.setWorld(getWorld());
		return loc;
	}
}
