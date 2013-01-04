package keepcalm.mods.bukkit.bukkitAPI.generator;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import keepcalm.mods.bukkit.bukkitAPI.block.BukkitBlock;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.feature.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenVillage;

import org.bukkit.Bukkit;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
//import net.minecraft.src.ChunkSection;
//import org.bukkit.craftbukkit.block.BukkitBlock;

public class CustomChunkGenerator extends InternalChunkGenerator {
    private final ChunkGenerator generator;
    private final WorldServer world;
    private final Random random;
    private HashMap<int[],net.minecraft.world.chunk.Chunk> loadedChunks = new HashMap();
    private final MapGenStronghold strongholdGen = new MapGenStronghold();
    private final MapGenVillage villageGen = new MapGenVillage();
    private final MapGenScatteredFeature scatteredGen = new MapGenScatteredFeature();
    private final MapGenMineshaft mineshaftGen = new MapGenMineshaft();

    private static class CustomBiomeGrid implements BiomeGrid {
        BiomeGenBase[] biome;

        public Biome getBiome(int x, int z) {
            return BukkitBlock.BiomeGenBaseToBiome(biome[(z << 4) | x]);
        }

        public void setBiome(int x, int z, Biome bio) {
           biome[(z << 4) | x] = BukkitBlock.biomeToBiomeGenBase(bio);
        }
    }

    public CustomChunkGenerator(World world, long seed, ChunkGenerator generator) {
        this.world = (WorldServer) world;
        this.generator = generator;

        this.random = new Random(seed);
    }

    

	public boolean isChunkLoaded(int x, int z) {
        return true;
    }

    public Chunk getOrCreateChunk(int x, int z) {
        random.setSeed((long) x * 341873128712L + (long) z * 132897987541L);

        Chunk chunk;

        // Get default biome data for chunk
        CustomBiomeGrid biomegrid = new CustomBiomeGrid();
        biomegrid.biome = new BiomeGenBase[256];
        world.getWorldChunkManager().getBiomeGenAt(biomegrid.biome, x << 4, z << 4, 16, 16, false);

        // Try extended block method (1.2+)
        short[][] xbtypes = generator.generateExtBlockSections(BukkitServer.instance().getWorld(world.getWorldInfo().getDimension()), this.random, x, z, biomegrid);
        if (xbtypes != null) {
            chunk = new Chunk(this.world, x, z);

            ExtendedBlockStorage[] csect = chunk.getBlockStorageArray();
            int scnt = Math.min(csect.length, xbtypes.length);

            // Loop through returned sections
            for (int sec = 0; sec < scnt; sec++) {
                if (xbtypes[sec] == null) {
                    continue;
                }
                byte[] secBlkID = new byte[4096]; // Allocate blk ID bytes
                byte[] secExtBlkID = (byte[]) null; // Delay getting extended ID nibbles
                short[] bdata = xbtypes[sec];
                // Loop through data, 2 blocks at a time
                for (int i = 0, j = 0; i < bdata.length; i += 2, j++) {
                    short b1 = bdata[i];
                    short b2 = bdata[i + 1];
                    byte extb = (byte) ((b1 >> 8) | ((b2 >> 4) & 0xF0));

                    secBlkID[i] = (byte) b1;
                    secBlkID[(i + 1)] = (byte) b2;

                    if (extb != 0) { // If extended block ID data
                        if (secExtBlkID == null) { // Allocate if needed
                            secExtBlkID = new byte[2048];
                        }
                        secExtBlkID[j] = extb;
                    }
                }
                // Build chunk section
                csect[sec] = new ExtendedBlockStorage(sec << 4, false);//, secBlkID, secExtBlkID);
            }
        }
        else { // Else check for byte-per-block section data
            byte[][] btypes = generator.generateBlockSections(getWorld(world), this.random, x, z, biomegrid);

            if (btypes != null) {
                chunk = new Chunk(this.world, x, z);

                ExtendedBlockStorage[] csect = chunk.getBlockStorageArray();
                int scnt = Math.min(csect.length, btypes.length);

                for (int sec = 0; sec < scnt; sec++) {
                    if (btypes[sec] == null) {
                        continue;
                    }
                    csect[sec] = new ExtendedBlockStorage(sec << 4, false);//, btypes[sec], null);
                }
            }
            else { // Else, fall back to pre 1.2 method
                @SuppressWarnings("deprecation")
                byte[] types = generator.generate(getWorld(world), this.random, x, z);
                int ydim = types.length / 256;
                int scnt = ydim / 16;

                chunk = new Chunk(this.world, x, z); // Create empty chunk

                ExtendedBlockStorage[] csect = chunk.getBlockStorageArray();

                scnt = Math.min(scnt, csect.length);
                // Loop through sections
                for (int sec = 0; sec < scnt; sec++) {
                    ExtendedBlockStorage cs = null; // Add sections when needed
                    byte[] csbytes = (byte[]) null;

                    for (int cy = 0; cy < 16; cy++) {
                        int cyoff = cy | (sec << 4);

                        for (int cx = 0; cx < 16; cx++) {
                            int cxyoff = (cx * ydim * 16) + cyoff;

                            for (int cz = 0; cz < 16; cz++) {
                                byte blk = types[cxyoff + (cz * ydim)];

                                if (blk != 0) { // If non-empty
                                    if (cs == null) { // If no section yet, get one
                                        cs = csect[sec] = new ExtendedBlockStorage(sec << 4, false);
                                        csbytes = cs.getBlockLSBArray();
                                    }
                                    csbytes[(cy << 8) | (cz << 4) | cx] = blk;
                                }
                            }
                        }
                    }
                    // If section built, finish prepping its state
                    if (cs != null) {
                        cs.createBlockMSBArray();
                    }
                }
            }
        }
        // Set biome grid
        byte[] biomeIndex = chunk.getBiomeArray();
        for (int i = 0; i < biomeIndex.length; i++) {
            biomeIndex[i] = (byte) (biomegrid.biome[i].biomeID & 0xFF);
        }
        // Initialize lighting
        chunk.generateSkylightMap();

        return chunk;
    }

    private org.bukkit.World getWorld(WorldServer world2) {
		return BukkitServer.instance().getWorld(world2.getWorldInfo().getDimension());
	}

	public void getChunkAt(IChunkProvider icp, int i, int i1) {
        // Nothing!
    }

    public boolean saveChunks(boolean bln, IProgressUpdate ipu) {
        return true;
    }

    public boolean unloadChunks() {
        return false;
    }

    public boolean canSave() {
        return true;
    }

    @SuppressWarnings("deprecation")
    public byte[] generate(org.bukkit.World world, Random random, int x, int z) {
        return generator.generate(world, random, x, z);
    }

    public byte[][] generateBlockSections(org.bukkit.World world, Random random, int x, int z, BiomeGrid biomes) {
        return generator.generateBlockSections(world, random, x, z, biomes);
    }

    public short[][] generateExtBlockSections(org.bukkit.World world, Random random, int x, int z, BiomeGrid biomes) {
        return generator.generateExtBlockSections(world, random, x, z, biomes);
    }

    public Chunk getChunkAt(int x, int z) {
        return getOrCreateChunk(x, z);
    }

    @Override
    public boolean canSpawn(org.bukkit.World world, int x, int z) {
        return generator.canSpawn(world, x, z);
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(org.bukkit.World world) {
        return generator.getDefaultPopulators(world);
    }

    public List<?> getMobsFor(EnumCreatureType type, int x, int y, int z) {
        BiomeGenBase biomebase = world.getBiomeGenForCoords(x, z);

        return biomebase == null ? null : biomebase.getSpawnableList(type);
    }
    
    public List<?> getPossibleCreatures(EnumCreatureType type, int x, int y, int z) {
    	return this.getMobsFor(type, x, y, z);
    }

    public ChunkPosition findNearestMapFeature(World world, String type, int x, int y, int z) {
        return "Stronghold".equals(type) && this.strongholdGen != null ? this.strongholdGen.getNearestInstance(world, x, y, z) : null;
    }

    public int getLoadedChunks() {
        return 0;
    }

    public String getName() {
        return "CustomChunkGenerator";
    }

	@Override
	public boolean chunkExists(int x, int z) {
		return this.getChunkAt(x, z) == null ? false : true;
	}

	@Override
	public ChunkPosition findClosestStructure(World var1, String var2,
			int x, int y, int z) {
		
		return this.findNearestMapFeature(var1, var2, x, y, z);
	}

	@Override
	public int getLoadedChunkCount() {
		return this.getLoadedChunks();
	}

	@Override
	public Chunk loadChunk(int x, int z) {
		return this.getChunkAt(x, z);
	}

	@Override
	public String makeString() {
		return "CustomChunkGenerator";
	}

	@Override
	public void populate(IChunkProvider var1, int var2, int var3) {
		// WOO!
		
	}

	@Override
	public Chunk provideChunk(int x, int z) {
		return this.getOrCreateChunk(x, z);
	}

	@Override
	public boolean unload100OldestChunks() {
		return this.unloadChunks();
	}



	@Override
	/**
	 * Generate structure
	 * @param x
	 * @param y
	 */
	public void recreateStructures(int arg0, int arg1) {
		
		this.strongholdGen.generate(this, this.world, arg0, arg1, (byte[])null);
		this.mineshaftGen.generate(this, this.world, arg0, arg1, (byte[])null);
		this.scatteredGen.generate(this, this.world, arg0, arg1, (byte[])null);
		this.villageGen.generate(this, this.world, arg0, arg1, (byte[])null);
	}



	
}
