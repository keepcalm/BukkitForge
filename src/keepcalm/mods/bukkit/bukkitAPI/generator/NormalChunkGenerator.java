package keepcalm.mods.bukkit.bukkitAPI.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import keepcalm.mods.bukkit.bukkitAPI.BukkitWorld;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

import org.bukkit.generator.BlockPopulator;
//import org.bukkit.craftbukkit.BukkitWorld;

public class NormalChunkGenerator extends InternalChunkGenerator {
    private final IChunkProvider provider;

    public NormalChunkGenerator(World world, long seed) {
        provider = world.getChunkProvider();
    }
    
    public NormalChunkGenerator(World world) {
    	this(world, world.getWorldInfo().getSeed());
    }

    public byte[] generate(org.bukkit.World world, Random random, int x, int z) {
        throw new UnsupportedOperationException("Not supported.");
    }

    public boolean canSpawn(org.bukkit.World world, int x, int z) {
        return ((BukkitWorld) world).getHandle().provider.canCoordinateBeSpawn(x, z);
    }

    public List<BlockPopulator> getDefaultPopulators(org.bukkit.World world) {
        return new ArrayList<BlockPopulator>();
    }

    public boolean isChunkLoaded(int i, int i1) {
        return provider.chunkExists(i, i1);
    }

    public Chunk getOrCreateChunk(int i, int i1) {
        return provider.provideChunk(i, i1);
    }

    public Chunk getChunkAt(int i, int i1) {
        return provider.loadChunk(i, i1);
    }

    public void getChunkAt(IChunkProvider icp, int i, int i1) {
        provider.populate(icp, i, i1);
    }

    public boolean saveChunks(boolean bln, IProgressUpdate ipu) {
        return provider.saveChunks(bln, ipu);
    }

    public boolean unloadChunks() {
        return provider.unload100OldestChunks();
    }

    public boolean canSave() {
        return provider.canSave();
    }

    public List<?> getMobsFor(EnumCreatureType ect, int i, int i1, int i2) {
        return provider.getPossibleCreatures(ect, i, i1, i2);
    }

    public ChunkPosition findNearestMapFeature(World world, String string, int i, int i1, int i2) {
        return provider.findClosestStructure(world, string, i, i1, i2);
    }

    public int getLoadedChunks() {
        return 0;
    }

    public String getName() {
        return "NormalWorldGenerator";
    }

	@Override
	public boolean chunkExists(int var1, int var2) {
		return provider.chunkExists(var1, var2);
	}

	@Override
	public ChunkPosition findClosestStructure(World var1, String var2,
			int var3, int var4, int var5) {
		return this.findNearestMapFeature(var1, var2, var3, var4, var5);
	}

	@Override
	public int getLoadedChunkCount() {
		return this.getLoadedChunks();
	}

	@Override
	public List<?> getPossibleCreatures(EnumCreatureType var1, int var2, int var3,
			int var4) {
		return this.getMobsFor(var1, var2, var3, var4);
	}

	@Override
	public Chunk loadChunk(int var1, int var2) {
		return this.getChunkAt(var1, var2);
	}

	@Override
	public String makeString() {
		return provider.makeString();
	}

	@Override
	public void populate(IChunkProvider var1, int var2, int var3) {
		provider.populate(var1, var2, var3);
	}

	@Override
	public Chunk provideChunk(int var1, int var2) {
		return this.getOrCreateChunk(var1, var2);
	}

	@Override
	public boolean unload100OldestChunks() {
		return this.unloadChunks();
	}

	@Override
	public void recreateStructures(int arg0, int arg1) {
		this.provider.recreateStructures(arg0, arg1);
	}
}
