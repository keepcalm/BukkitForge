package keepcalm.mods.bukkit.bukkitAPI;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import keepcalm.mods.bukkit.bukkitAPI.block.BukkitBlock;
import keepcalm.mods.bukkit.bukkitAPI.entity.BukkitEntity;
import keepcalm.mods.bukkit.bukkitAPI.entity.BukkitItem;
import keepcalm.mods.bukkit.bukkitAPI.entity.BukkitLightningStrike;
import keepcalm.mods.bukkit.bukkitAPI.entity.BukkitMinecart;
import keepcalm.mods.bukkit.bukkitAPI.entity.BukkitPlayer;
import keepcalm.mods.bukkit.bukkitAPI.generator.CustomChunkGenerator;
import keepcalm.mods.bukkit.bukkitAPI.generator.NetherChunkGenerator;
import keepcalm.mods.bukkit.bukkitAPI.generator.NormalChunkGenerator;
import keepcalm.mods.bukkit.bukkitAPI.item.BukkitItemStack;
import keepcalm.mods.bukkit.bukkitAPI.metadata.BlockMetadataStore;
import keepcalm.mods.bukkit.bukkitAPI.utils.LongHash;
import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.BlockWood;
import net.minecraft.src.ChunkCoordIntPair;
import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.EmptyChunk;
import net.minecraft.src.EntityArrow;
import net.minecraft.src.EntityBlaze;
import net.minecraft.src.EntityBoat;
import net.minecraft.src.EntityCaveSpider;
import net.minecraft.src.EntityChicken;
import net.minecraft.src.EntityCow;
import net.minecraft.src.EntityCreeper;
import net.minecraft.src.EntityDragon;
import net.minecraft.src.EntityEgg;
import net.minecraft.src.EntityEnderCrystal;
import net.minecraft.src.EntityEnderman;
import net.minecraft.src.EntityExpBottle;
import net.minecraft.src.EntityFallingSand;
import net.minecraft.src.EntityFireball;
import net.minecraft.src.EntityFishHook;
import net.minecraft.src.EntityGhast;
import net.minecraft.src.EntityGiantZombie;
import net.minecraft.src.EntityIronGolem;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityLightningBolt;
import net.minecraft.src.EntityMagmaCube;
import net.minecraft.src.EntityMinecart;
import net.minecraft.src.EntityMooshroom;
import net.minecraft.src.EntityOcelot;
import net.minecraft.src.EntityPainting;
import net.minecraft.src.EntityPig;
import net.minecraft.src.EntityPigZombie;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.EntitySheep;
import net.minecraft.src.EntitySilverfish;
import net.minecraft.src.EntitySkeleton;
import net.minecraft.src.EntitySlime;
import net.minecraft.src.EntitySmallFireball;
import net.minecraft.src.EntitySnowball;
import net.minecraft.src.EntitySnowman;
import net.minecraft.src.EntitySpider;
import net.minecraft.src.EntitySquid;
import net.minecraft.src.EntityTNTPrimed;
import net.minecraft.src.EntityVillager;
import net.minecraft.src.EntityWolf;
import net.minecraft.src.EntityXPOrb;
import net.minecraft.src.EntityZombie;
import net.minecraft.src.Explosion;
import net.minecraft.src.Packet4UpdateTime;
import net.minecraft.src.Packet61DoorChange;
import net.minecraft.src.TileEntity;
import net.minecraft.src.WorldGenBigMushroom;
import net.minecraft.src.WorldGenBigTree;
import net.minecraft.src.WorldGenForest;
import net.minecraft.src.WorldGenHugeTrees;
import net.minecraft.src.WorldGenShrub;
import net.minecraft.src.WorldGenTaiga1;
import net.minecraft.src.WorldGenTaiga2;
import net.minecraft.src.WorldGenTrees;
import net.minecraft.src.WorldGenerator;
import net.minecraft.src.WorldProvider;
import net.minecraft.src.WorldServer;
import net.minecraftforge.common.DimensionManager;

import org.apache.commons.lang.Validate;
import org.bukkit.BlockChangeDelegate;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Difficulty;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Boat;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.ComplexLivingEntity;
import org.bukkit.entity.Cow;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Golem;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.PoweredMinecart;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.StorageMinecart;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Weather;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.SpawnChangeEvent;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.StandardMessenger;
import org.bukkit.util.Vector;
//import org.bukkit.craftbukkit.entity.*;
//import org.bukkit.craftbukkit.metadata.BlockMetadataStore;
//import org.bukkit.craftbukkit.block.BukkitBlock;
//import org.bukkit.craftbukkit.inventory.BukkitItemStack;
//import keepcalm.mods.bukkit.bukkitAPI.utils.LongHash;

public class BukkitWorld implements World {
    private final WorldServer world;
    private Environment environment;
    private final BukkitServer server = (BukkitServer) Bukkit.getServer();
    private ChunkGenerator generator;
    private final List<BlockPopulator> populators = new ArrayList<BlockPopulator>();
    private final BlockMetadataStore blockMetadata = new BlockMetadataStore(this);
    private int monsterSpawn = -1;
    private int animalSpawn = -1;
    private int waterAnimalSpawn = -1;
    private int ambientSpawn = -1;
    private static final Random rand = new Random();

    public BukkitWorld(WorldServer world, ChunkGenerator gen, Environment env) {
        this.world = world;
        this.generator = gen;

        environment = env;
    }
    

    

	public Block getBlockAt(int x, int y, int z) {
        return getChunkAt(x >> 4, z >> 4).getBlock(x & 0xF, y & 0xFF, z & 0xF);
    }

    public int getBlockTypeIdAt(int x, int y, int z) {
        return world.getBlockId(x, y, z);
    }

    public int getHighestBlockYAt(int x, int z) {
        if (!isChunkLoaded(x >> 4, z >> 4)) {
            loadChunk(x >> 4, z >> 4);
        }

        return world.getTopSolidOrLiquidBlock(x, z);
    }

    public Location getSpawnLocation() {
        ChunkCoordinates spawn = world.getSpawnPoint();
        return new Location(this, spawn.posX, spawn.posY, spawn.posZ);
    }

    public boolean setSpawnLocation(int x, int y, int z) {
        try {
            Location previousLocation = getSpawnLocation();
            world.getWorldInfo().setSpawnPosition(x, y, z);

            // Notify anyone who's listening.
            SpawnChangeEvent event = new SpawnChangeEvent(this, previousLocation);
            server.getPluginManager().callEvent(event);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Chunk getChunkAt(int x, int z) {
        return new BukkitChunk(this.world.theChunkProviderServer.provideChunk(x, z));
    }

    public Chunk getChunkAt(Block block) {
        return getChunkAt(block.getX() >> 4, block.getZ() >> 4);
    }

    public boolean isChunkLoaded(int x, int z) {
    	world.theChunkProviderServer.loadChunkOnProvideRequest = false;
    	net.minecraft.src.Chunk c = world.theChunkProviderServer.provideChunk(x, z);
    	world.theChunkProviderServer.loadChunkOnProvideRequest = true;
    	if (c instanceof EmptyChunk) {
    		
    		return false;
    	}
        return true;
    }

    public Chunk[] getLoadedChunks() {
        Object[] chunks = world.theChunkProviderServer.loadedChunks.toArray();
        org.bukkit.Chunk[] craftChunks = new BukkitChunk[chunks.length];

        for (int i = 0; i < chunks.length; i++) {
            net.minecraft.src.Chunk chunk = (net.minecraft.src.Chunk) chunks[i];
            craftChunks[i] = new BukkitChunk(chunk);
        }

        return craftChunks;
    }

    public void loadChunk(int x, int z) {
        loadChunk(x, z, true);
    }

    public boolean unloadChunk(Chunk chunk) {
        return unloadChunk(chunk.getX(), chunk.getZ());
    }

    public boolean unloadChunk(int x, int z) {
        return unloadChunk(x, z, true);
    }

    public boolean unloadChunk(int x, int z, boolean save) {
        return unloadChunk(x, z, save, false);
    }

    public boolean unloadChunkRequest(int x, int z) {
        return unloadChunkRequest(x, z, true);
    }

    public boolean unloadChunkRequest(int x, int z, boolean safe) {
        if (safe && isChunkInUse(x, z)) {
            return false;
        }

        world.theChunkProviderServer.unloadChunksIfNotNearSpawn(x, z);

        return true;
    }

    public boolean unloadChunk(int x, int z, boolean save, boolean safe) {
        if (safe && isChunkInUse(x, z)) {
            return false;
        }

        net.minecraft.src.Chunk chunk = world.theChunkProviderServer.provideChunk(x, z);
        if (chunk.isModified) {   // If chunk had previously been queued to save, must do save to avoid loss of that data
            save = true;
        }

        chunk.entityLists = null; // Always remove entities - even if discarding, need to get them out of world table

        if (save && !(chunk instanceof EmptyChunk)) {
            world.theChunkProviderServer.safeSaveChunk(chunk);
            //world.theChunkProviderServer.saveChunkNOP(chunk);
        }

        world.theChunkProviderServer.chunksToUnload.remove(ChunkCoordIntPair.chunkXZ2Int(x,z));
        world.theChunkProviderServer.loadedChunks.remove(LongHash.toLong(x, z));

        return true;
    }

    public boolean regenerateChunk(int x, int z) {
        unloadChunk(x, z, false, false);

        world.theChunkProviderServer.chunksToUnload.remove(ChunkCoordIntPair.chunkXZ2Int(x,z));

        net.minecraft.src.Chunk chunk = null;

        if (world.theChunkProviderServer.currentChunkProvider == null) {
            chunk = world.theChunkProviderServer.defaultEmptyChunk;
        } else {
            chunk = world.theChunkProviderServer.currentChunkProvider.provideChunk(x, z);
        }

        chunkLoadPostProcess(chunk, x, z);

        refreshChunk(x, z);

        return chunk != null;
    }

    public boolean refreshChunk(int x, int z) {
        if (!isChunkLoaded(x, z)) {
            return false;
        }

        int px = x << 4;
        int pz = z << 4;

        // If there are more than 64 updates to a chunk at once, it will update all 'touched' sections within the chunk
        // And will include biome data if all sections have been 'touched'
        // This flags 65 blocks distributed across all the sections of the chunk, so that everything is sent, including biomes
        int height = getMaxHeight() / 16;
        for (int idx = 0; idx < 64; idx++) {
        	int id = world.getBlockId(px + (idx / height), ((idx % height) * 16), pz);
            world.notifyBlockChange(px + (idx / height), ((idx % height) * 16), pz, id);
        }
        int id = world.getBlockId(px + 15, (height * 16) - 1, pz + 15);
        world.notifyBlockChange(px + 15, (height * 16) - 1, pz + 15, id);

        return true;
    }

    public boolean isChunkInUse(int x, int z) {
    	return world.getPlayerManager().getOrCreateChunkWatcher(x,z,false).playersInChunk.size() == 0;
        
    }

    public boolean loadChunk(int x, int z, boolean generate) {
        if (generate) {
            // Use the default variant of loadChunk when generate == true.
            return world.theChunkProviderServer.provideChunk(x, z) != null;
        }

        world.theChunkProviderServer.chunksToUnload.remove(ChunkCoordIntPair.chunkXZ2Int(x,z));
        net.minecraft.src.Chunk chunk = (net.minecraft.src.Chunk) world.theChunkProviderServer.loadedChunkHashMap.getValueByKey(LongHash.toLong(x, z));

        if (chunk == null) {
            chunk = world.theChunkProviderServer.loadChunk(x, z);

            chunkLoadPostProcess(chunk, x, z);
        }
        return chunk != null;
    }

    @SuppressWarnings("unchecked")
    private void chunkLoadPostProcess(net.minecraft.src.Chunk chunk, int x, int z) {
        if (chunk != null) {
            world.theChunkProviderServer.loadedChunkHashMap.add(LongHash.toLong(x, z), chunk);

            //chunk.entity();

            if (!chunk.isChunkLoaded && world.theChunkProviderServer.chunkExists(x + 1, z + 1) && world.theChunkProviderServer.chunkExists(x, z + 1) && world.theChunkProviderServer.chunkExists(x + 1, z)) {
                world.theChunkProviderServer.loadChunk(x, z);
            }

            if (world.theChunkProviderServer.chunkExists(x - 1, z) && !world.theChunkProviderServer.provideChunk(x - 1, z).isChunkLoaded && world.theChunkProviderServer.chunkExists(x - 1, z + 1) && world.theChunkProviderServer.chunkExists(x, z + 1) && world.theChunkProviderServer.chunkExists(x - 1, z)) {
                world.theChunkProviderServer.loadChunk(x - 1, z);
            }

            if (world.theChunkProviderServer.chunkExists(x, z - 1) && !world.theChunkProviderServer.provideChunk(x, z - 1).isChunkLoaded && world.theChunkProviderServer.chunkExists(x + 1, z - 1) && world.theChunkProviderServer.chunkExists(x, z - 1) && world.theChunkProviderServer.chunkExists(x + 1, z)) {
                world.theChunkProviderServer.loadChunk( x, z - 1);
            }

            if (world.theChunkProviderServer.chunkExists(x - 1, z - 1) && !world.theChunkProviderServer.provideChunk(x - 1, z - 1).isChunkLoaded && world.theChunkProviderServer.chunkExists(x - 1, z - 1) && world.theChunkProviderServer.chunkExists(x, z - 1) && world.theChunkProviderServer.chunkExists(x - 1, z)) {
                world.theChunkProviderServer.loadChunk( x - 1, z - 1);
            }
        }
    }

    public boolean isChunkLoaded(Chunk chunk) {
        return isChunkLoaded(chunk.getX(), chunk.getZ());
    }

    public void loadChunk(Chunk chunk) {
        loadChunk(chunk.getX(), chunk.getZ());
        //((BukkitChunk) getChunkAt(chunk.getX(), chunk.getZ())).getHandle().bukkitChunk = chunk;
    }

    public WorldServer getHandle() {
        return world;
    }

    public org.bukkit.entity.Item dropItem(Location loc, ItemStack item) {
        Validate.notNull(item, "Cannot drop a Null item.");
        Validate.isTrue(item.getTypeId() != 0, "Cannot drop AIR.");
        BukkitItemStack clone = new BukkitItemStack(item);
        EntityItem entity = new EntityItem(world, loc.getX(), loc.getY(), loc.getZ(), clone.getHandle());
        entity.delayBeforeCanPickup = 10;
        world.spawnEntityInWorld(entity);
        // TODO this is inconsistent with how Entity.getBukkitEntity() works.
        // However, this entity is not at the moment backed by a server entity class so it may be left.
        return new BukkitItem((BukkitServer) Bukkit.getServer(), entity);
    }

    public org.bukkit.entity.Item dropItemNaturally(Location loc, ItemStack item) {
        double xs = world.rand.nextFloat() * 0.7F + (1.0F - 0.7F) * 0.5D;
        double ys = world.rand.nextFloat() * 0.7F + (1.0F - 0.7F) * 0.5D;
        double zs = world.rand.nextFloat() * 0.7F + (1.0F - 0.7F) * 0.5D;
        loc = loc.clone();
        loc.setX(loc.getX() + xs);
        loc.setY(loc.getY() + ys);
        loc.setZ(loc.getZ() + zs);
        return dropItem(loc, item);
    }

    public Arrow spawnArrow(Location loc, Vector velocity, float speed, float spread) {
        EntityArrow arrow = new EntityArrow(world);
        arrow.setPositionAndRotation(loc.getX(), loc.getY(), loc.getZ(), 0, 0);
        arrow.motionX = velocity.getX();
        arrow.motionY = velocity.getY();
        arrow.motionZ = velocity.getZ();
        world.spawnEntityInWorld(arrow);
        //arrow.fire(velocity.getX(), velocity.getY(), velocity.getZ(), speed, spread);
        return (Arrow)BukkitEntity.getEntity((BukkitServer) Bukkit.getServer(), arrow);
    }

    @Deprecated
    public LivingEntity spawnCreature(Location loc, CreatureType creatureType) {
        return spawnCreature(loc, creatureType.toEntityType());
    }

    @Deprecated
    public LivingEntity spawnCreature(Location loc, EntityType creatureType) {
        Validate.isTrue(creatureType.isAlive(), "EntityType not instance of LivingEntity");
        return (LivingEntity) spawnEntity(loc, creatureType);
    }

    public Entity spawnEntity(Location loc, EntityType entityType) {
        return spawn(loc, entityType.getEntityClass());
    }

    public LightningStrike strikeLightning(Location loc) {
        EntityLightningBolt lightning = new EntityLightningBolt(world, loc.getX(), loc.getY(), loc.getZ());
        world.addWeatherEffect(lightning);
        return new BukkitLightningStrike(server, lightning);
    }

    public LightningStrike strikeLightningEffect(Location loc) {
        EntityLightningBolt lightning = new EntityLightningBolt(world, loc.getX(), loc.getY(), loc.getZ());
        world.addWeatherEffect(lightning);
        return new BukkitLightningStrike(server, lightning);
    }

    public boolean generateTree(Location loc, TreeType type) {
        return generateTree(loc, type, (World) world);
    }

    public boolean generateTree(Location loc, TreeType type, World delegate) {
    	net.minecraft.src.World world = ((BukkitWorld) delegate).getHandle();
        WorldGenerator gen;
        switch (type) {
        case BIG_TREE:
            gen = new WorldGenBigTree(true);
            break;
        case BIRCH:
            gen = new WorldGenForest(true);
            break;
        case REDWOOD:
            gen = new WorldGenTaiga2(true);
            break;
        case TALL_REDWOOD:
            gen = new WorldGenTaiga1();
            break;
        case JUNGLE:
            gen = new WorldGenHugeTrees(true, 10 + rand.nextInt(20), 3, 3);
            break;
        case SMALL_JUNGLE:
            gen = new WorldGenTrees(true, 4 + rand.nextInt(7), 3, 3, false);
            break;
        case JUNGLE_BUSH:
            gen = new WorldGenShrub(3, 0);
            break;
        case RED_MUSHROOM:
            gen = new WorldGenBigMushroom(1);
            break;
        case BROWN_MUSHROOM:
            gen = new WorldGenBigMushroom(0);
            break;
        case SWAMP:
        	
        	gen = new WorldGenTrees(true, 4 + rand.nextInt(7), 0, 0, true);
            break;
        case TREE:
        default:
            gen = new WorldGenTrees(true);
            break;
        }

        return gen.generate(world, rand, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    public boolean generateTree(Location loc, TreeType type, BlockChangeDelegate delegate) {
    	//World w = delegate.
    	return this.generateTree(loc, type, (World) world);
    }
    public TileEntity getTileEntityAt(final int x, final int y, final int z) {
        return world.getBlockTileEntity(x, y, z);
    }

    public String getName() {
        return world.getWorldInfo().getWorldName();
    }

    @Deprecated
    public long getId() {
        return world.getWorldInfo().getSeed();
    }

    public UUID getUID() {
        return new UUID(world.getSeed(), 60000);
    }

    @Override
    public String toString() {
        return "BukkitWorld{name=" + getName() + '}';
    }

    public long getTime() {
        long time = getFullTime() % 24000;
        if (time < 0) time += 24000;
        return time;
    }

    public void setTime(long time) {
        long margin = (time - getFullTime()) % 24000;
        if (margin < 0) margin += 24000;
        setFullTime(getFullTime() + margin);
    }

    public long getFullTime() {
        return world.getWorldTime();
    }

    public void setFullTime(long time) {
        world.setWorldTime(time);

        // Forces the client to update to the new time immediately
        for (Player p : getPlayers()) {
            BukkitPlayer cp = (BukkitPlayer) p;
            if (cp.getHandle().playerNetServerHandler == null) continue;

            (server).getHandle().getDedicatedPlayerList().updateTimeAndWeatherForPlayer(cp.getHandle(), world);
        }
    }

    public boolean createExplosion(double x, double y, double z, float power) {
        return createExplosion(x, y, z, power, false);
    }

    public boolean createExplosion(double x, double y, double z, float power, boolean setFire) {
    	Explosion boom = world.createExplosion(null, x, y, z, power, setFire);
    	return true;
 
    	
    }

    public boolean createExplosion(Location loc, float power) {
        return createExplosion(loc, power, false);
    }

    public boolean createExplosion(Location loc, float power, boolean setFire) {
        return createExplosion(loc.getX(), loc.getY(), loc.getZ(), power, setFire);
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment env) {
    	
        if (environment != env) {
            environment = env;
            Bukkit.getLogger().severe("Changing the world's environment is highly dangerous!");
            world.provider = WorldProvider.getProviderForDimension(environment.getId());
        }
    }

    public Block getBlockAt(Location location) {
        return getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public int getBlockTypeIdAt(Location location) {
        return getBlockTypeIdAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public int getHighestBlockYAt(Location location) {
        return getHighestBlockYAt(location.getBlockX(), location.getBlockZ());
    }

    public Chunk getChunkAt(Location location) {
        return getChunkAt(location.getBlockX() >> 4, location.getBlockZ() >> 4);
    }

    public ChunkGenerator getGenerator() {
        return generator;
    }

    public List<BlockPopulator> getPopulators() {
        return populators;
    }

    public Block getHighestBlockAt(int x, int z) {
        return getBlockAt(x, getHighestBlockYAt(x, z), z);
    }

    public Block getHighestBlockAt(Location location) {
        return getHighestBlockAt(location.getBlockX(), location.getBlockZ());
    }

    public Biome getBiome(int x, int z) {
        return BukkitBlock.biomeBaseToBiome(this.world.getBiomeGenForCoords(x, z));
    }

    public void setBiome(int x, int z, Biome bio) {
        BiomeGenBase bb = BukkitBlock.biomeToBiomeGenBase(bio);
        if (this.world.theChunkProviderServer.chunkExists(x, z)) {
            net.minecraft.src.Chunk chunk = this.world.getChunkFromChunkCoords(x, z);

            if (chunk != null) {
                byte[] biomevals = chunk.getBiomeArray();
                biomevals[((z & 0xF) << 4) | (x & 0xF)] = (byte)bb.biomeID;
            }
        }
    }

    public double getTemperature(int x, int z) {
        return this.world.getBiomeGenForCoords(x, z).temperature;
    }

    public double getHumidity(int x, int z) {
        return this.world.getBiomeGenForCoords(x, z).rainfall;
    }

    public List<Entity> getEntities() {
        List<Entity> list = new ArrayList<Entity>();

        for (Object o : world.loadedEntityList) {
            if (o instanceof net.minecraft.src.Entity) {
                net.minecraft.src.Entity mcEnt = (net.minecraft.src.Entity) o;
                Entity bukkitEntity = BukkitEntity.getEntity(server, mcEnt);

                // Assuming that bukkitEntity isn't null
                if (bukkitEntity != null) {
                    list.add(bukkitEntity);
                }
            }
        }

        return list;
    }

    public List<LivingEntity> getLivingEntities() {
        List<LivingEntity> list = new ArrayList<LivingEntity>();

        for (Object o : world.loadedEntityList) {
            if (o instanceof net.minecraft.src.Entity) {
                net.minecraft.src.Entity mcEnt = (net.minecraft.src.Entity) o;
                Entity bukkitEntity = BukkitEntity.getEntity(server, mcEnt);

                // Assuming that bukkitEntity isn't null
                if (bukkitEntity != null && bukkitEntity instanceof LivingEntity) {
                    list.add((LivingEntity) bukkitEntity);
                }
            }
        }

        return list;
    }

    @SuppressWarnings("unchecked")
    @Deprecated
    public <T extends Entity> Collection<T> getEntitiesByClass(Class<T>... classes) {
        return (Collection<T>)getEntitiesByClasses(classes);
    }

    @SuppressWarnings("unchecked")
    public <T extends Entity> Collection<T> getEntitiesByClass(Class<T> clazz) {
        Collection<T> list = new ArrayList<T>();

        for (Object entity: world.loadedEntityList) {
            if (entity instanceof net.minecraft.src.Entity) {
                Entity bukkitEntity = BukkitEntity.getEntity(server, (net.minecraft.src.Entity) entity);

                if (bukkitEntity == null) {
                    continue;
                }

                Class<?> bukkitClass = bukkitEntity.getClass();

                if (clazz.isAssignableFrom(bukkitClass)) {
                    list.add((T) bukkitEntity);
                }
            }
        }

        return list;
    }

    public Collection<Entity> getEntitiesByClasses(Class<?>... classes) {
        Collection<Entity> list = new ArrayList<Entity>();

        for (Object entity: world.loadedEntityList) {
            if (entity instanceof net.minecraft.src.Entity) {
                Entity bukkitEntity = BukkitEntity.getEntity(server, (net.minecraft.src.Entity) entity);

                if (bukkitEntity == null) {
                    continue;
                }

                Class<?> bukkitClass = bukkitEntity.getClass();

                for (Class<?> clazz : classes) {
                    if (clazz.isAssignableFrom(bukkitClass)) {
                        list.add(bukkitEntity);
                        break;
                    }
                }
            }
        }

        return list;
    }

    public List<Player> getPlayers() {
        List<Player> list = new ArrayList<Player>();

        for (Object o : world.loadedEntityList) {
            if (o instanceof net.minecraft.src.Entity) {
                net.minecraft.src.Entity mcEnt = (net.minecraft.src.Entity) o;
                Entity bukkitEntity = BukkitEntity.getEntity(server, (net.minecraft.src.Entity) mcEnt);

                if ((bukkitEntity != null) && (bukkitEntity instanceof Player)) {
                    list.add((Player) bukkitEntity);
                }
            }
        }

        return list;
    }

    public void save() {
        try {
            boolean oldSave = world.canNotSave;

            world.canNotSave = false;
            world.saveAllChunks(true, null);

            world.canNotSave = oldSave;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean isAutoSave() {
        return !world.canNotSave;
    }

    public void setAutoSave(boolean value) {
        world.canNotSave = !value;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.getHandle().difficultySetting = difficulty.getValue();
    }

    public Difficulty getDifficulty() {
        return Difficulty.getByValue(this.getHandle().difficultySetting);
    }

    public BlockMetadataStore getBlockMetadata() {
        return blockMetadata;
    }

    public boolean hasStorm() {
        return world.getWorldInfo().isThundering();
    }

    public void setStorm(boolean hasStorm) {
        BukkitServer server = this.server;

        WeatherChangeEvent weather = new WeatherChangeEvent((org.bukkit.World) this, hasStorm);
        server.getPluginManager().callEvent(weather);
        if (!weather.isCancelled()) {
            world.getWorldInfo().setThundering(hasStorm);

            // These numbers are from Minecraft
            if (hasStorm) {
                setWeatherDuration(rand.nextInt(12000) + 12000);
            } else {
                setWeatherDuration(rand.nextInt(168000) + 12000);
            }
        }
    }

    public int getWeatherDuration() {
        return world.getWorldInfo().getRainTime();
    }

    public void setWeatherDuration(int duration) {
        world.getWorldInfo().setRainTime(duration);
    }

    public boolean isThundering() {
        return hasStorm() && world.getWorldInfo().isThundering();
    }

    public void setThundering(boolean thundering) {
        if (thundering && !hasStorm()) setStorm(true);
        BukkitServer server = this.server;

        ThunderChangeEvent thunder = new ThunderChangeEvent((org.bukkit.World) this, thundering);
        server.getPluginManager().callEvent(thunder);
        if (!thunder.isCancelled()) {
            world.getWorldInfo().setThundering(thundering);

            // These numbers are from Minecraft
            if (thundering) {
                setThunderDuration(rand.nextInt(12000) + 3600);
            } else {
                setThunderDuration(rand.nextInt(168000) + 12000);
            }
        }
    }

    public int getThunderDuration() {
        return world.getWorldInfo().getThunderTime();
    }

    public void setThunderDuration(int duration) {
        world.getWorldInfo().setThunderTime(duration);
    }

    public long getSeed() {
        return world.getWorldInfo().getSeed();
    }
    public boolean getPVP() {
        return world.getMinecraftServer().isPVPEnabled();
    }
    public void setPVP(boolean pvp) {
        world.getMinecraftServer().setAllowPvp(pvp);
    }

    public void playEffect(Player player, Effect effect, int data) {
        playEffect(player.getLocation(), effect, data, 0);
    }

    public void playEffect(Location location, Effect effect, int data) {
        playEffect(location, effect, data, 64);
    }

    public <T> void playEffect(Location loc, Effect effect, T data) {
        playEffect(loc, effect, data, 64);
    }

    public <T> void playEffect(Location loc, Effect effect, T data, int radius) {
        if (data != null) {
            Validate.isTrue(data.getClass().equals(effect.getData()), "Wrong kind of data for this effect!");
        } else {
            Validate.isTrue(effect.getData() == null, "Wrong kind of data for this effect!");
        }

        int datavalue = data == null ? 0 : BukkitEffect.getDataValue(effect, data);
        playEffect(loc, effect, datavalue, radius);
    }

    public void playEffect(Location location, Effect effect, int data, int radius) {
        Validate.notNull(location, "Location cannot be null");
        Validate.notNull(effect, "Effect cannot be null");
        Validate.notNull(location.getWorld(), "World cannot be null");
        int packetData = effect.getId();
        Packet61DoorChange packet = new Packet61DoorChange(packetData, location.getBlockX(), location.getBlockY(), location.getBlockZ(), data, false);
        int distance;
        radius *= radius;

        for (Player player : getPlayers()) {
            if (((EntityPlayerMP) ((BukkitPlayer) player).getHandle()).playerNetServerHandler == null) continue;
            if (!location.getWorld().equals(player.getWorld())) continue;

            distance = (int) player.getLocation().distanceSquared(location);
            if (distance <= radius) {
            	((EntityPlayerMP) ((BukkitPlayer) player).getHandle()).playerNetServerHandler.sendPacketToPlayer(packet);
            }
        }
    }

    public <T extends Entity> T spawn(Location location, Class<T> clazz) throws IllegalArgumentException {
        return spawn(location, clazz, SpawnReason.CUSTOM);
    }

    public FallingBlock spawnFallingBlock(Location location, org.bukkit.Material material, byte data) throws IllegalArgumentException {
        Validate.notNull(location, "Location cannot be null");
        Validate.notNull(material, "Material cannot be null");
        Validate.isTrue(material.isBlock(), "Material must be a block");

        double x = location.getBlockX() + 0.5;
        double y = location.getBlockY() + 0.5;
        double z = location.getBlockZ() + 0.5;

        EntityFallingSand entity = new EntityFallingSand(world, x, y, z, material.getId(), data);
        entity.ticksExisted = 1; // ticksLived

        world.spawnEntityInWorld(entity);//, SpawnReason.CUSTOM);
        return (FallingBlock) BukkitEntity.getEntity(server, entity);
    }

    public FallingBlock spawnFallingBlock(Location location, int blockId, byte blockData) throws IllegalArgumentException {
        return spawnFallingBlock(location, org.bukkit.Material.getMaterial(blockId), blockData);
    }

    @SuppressWarnings("unchecked")
    public <T extends Entity> T spawn(Location location, Class<T> clazz, SpawnReason reason) throws IllegalArgumentException {
        if (location == null || clazz == null) {
            throw new IllegalArgumentException("Location or entity class cannot be null");
        }

        net.minecraft.src.Entity entity = null;

        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        float pitch = location.getPitch();
        float yaw = location.getYaw();

        // order is important for some of these
        if (Boat.class.isAssignableFrom(clazz)) {
            entity = new EntityBoat(world, x, y, z);
        } else if (FallingBlock.class.isAssignableFrom(clazz)) {
            x = location.getBlockX();
            y = location.getBlockY();
            z = location.getBlockZ();
            int type = world.getBlockId((int) x, (int) y, (int) z);
            int data = world.getBlockMetadata((int) x, (int) y, (int) z);

            entity = new EntityFallingSand(world, x + 0.5, y + 0.5, z + 0.5, type, data);
        } else if (Projectile.class.isAssignableFrom(clazz)) {
            if (Snowball.class.isAssignableFrom(clazz)) {
                entity = new EntitySnowball(world, x, y, z);
            } else if (Egg.class.isAssignableFrom(clazz)) {
                entity = new EntityEgg(world, x, y, z);
            } else if (Arrow.class.isAssignableFrom(clazz)) {
                entity = new EntityArrow(world);
                entity.setPositionAndRotation(x, y, z, 0, 0);
            } else if (ThrownExpBottle.class.isAssignableFrom(clazz)) {
                entity = new EntityExpBottle(world);
                entity.setPositionAndRotation(x, y, z, 0, 0);
            } else if (Fireball.class.isAssignableFrom(clazz)) {
                    entity = new EntitySmallFireball(world);
                }
                ((EntityFireball) entity).setPositionAndRotation(x, y, z, yaw, pitch);
                Vector direction = location.getDirection().multiply(10);
                ((EntityFireball) entity).accelerationX = direction.getX();
                ((EntityFireball) entity).accelerationY = direction.getY();
                ((EntityFireball) entity).accelerationZ = direction.getZ();
                
                //(direction.getX(), direction.getY(), direction.getZ());
        } else if (Minecart.class.isAssignableFrom(clazz)) {
            if (PoweredMinecart.class.isAssignableFrom(clazz)) {
                entity = new EntityMinecart(world, x, y, z, BukkitMinecart.Type.PoweredMinecart.getId());
            } else if (StorageMinecart.class.isAssignableFrom(clazz)) {
                entity = new EntityMinecart(world, x, y, z, BukkitMinecart.Type.StorageMinecart.getId());
            } else {
                entity = new EntityMinecart(world, x, y, z, BukkitMinecart.Type.Minecart.getId());
            }
        } /*else if (EnderSignal.class.isAssignableFrom(clazz)) {
            entity = new EntityEnderSignal(world, x, y, z);
        } */else if (EnderCrystal.class.isAssignableFrom(clazz)) {
            entity = new EntityEnderCrystal(world);
            entity.setPositionAndRotation(x, y, z, 0, 0);
        } else if (LivingEntity.class.isAssignableFrom(clazz)) {
            if (Chicken.class.isAssignableFrom(clazz)) {
                entity = new EntityChicken(world);
            } else if (Cow.class.isAssignableFrom(clazz)) {
                if (MushroomCow.class.isAssignableFrom(clazz)) {
                    entity = new EntityMooshroom(world);
                } else {
                    entity = new EntityCow(world);
                }
            } else if (Golem.class.isAssignableFrom(clazz)) {
                if (Snowman.class.isAssignableFrom(clazz)) {
                    entity = new EntitySnowman(world);
                } else if (IronGolem.class.isAssignableFrom(clazz)) {
                    entity = new EntityIronGolem(world);
                }
            } else if (Creeper.class.isAssignableFrom(clazz)) {
                entity = new EntityCreeper(world);
            } else if (Ghast.class.isAssignableFrom(clazz)) {
                entity = new EntityGhast(world);
            } else if (Pig.class.isAssignableFrom(clazz)) {
                entity = new EntityPig(world);
            } else if (Player.class.isAssignableFrom(clazz)) {
                // need a net server handler for this one
            } else if (Sheep.class.isAssignableFrom(clazz)) {
                entity = new EntitySheep(world);
            } else if (Skeleton.class.isAssignableFrom(clazz)) {
                entity = new EntitySkeleton(world);
            } else if (Slime.class.isAssignableFrom(clazz)) {
                if (MagmaCube.class.isAssignableFrom(clazz)) {
                    entity = new EntityMagmaCube(world);
                } else {
                    entity = new EntitySlime(world);
                }
            } else if (Spider.class.isAssignableFrom(clazz)) {
                if (CaveSpider.class.isAssignableFrom(clazz)) {
                    entity = new EntityCaveSpider(world);
                } else {
                    entity = new EntitySpider(world);
                }
            } else if (Squid.class.isAssignableFrom(clazz)) {
                entity = new EntitySquid(world);
            } else if (Tameable.class.isAssignableFrom(clazz)) {
                if (Wolf.class.isAssignableFrom(clazz)) {
                    entity = new EntityWolf(world);
                } else if (Ocelot.class.isAssignableFrom(clazz)) {
                    entity = new EntityOcelot(world);
                }
            } else if (PigZombie.class.isAssignableFrom(clazz)) {
                entity = new EntityPigZombie(world);
            } else if (Zombie.class.isAssignableFrom(clazz)) {
                entity = new EntityZombie(world);
            } else if (Giant.class.isAssignableFrom(clazz)) {
                entity = new EntityGiantZombie(world);
            } else if (Silverfish.class.isAssignableFrom(clazz)) {
                entity = new EntitySilverfish(world);
            } else if (Enderman.class.isAssignableFrom(clazz)) {
                entity = new EntityEnderman(world);
            } else if (Blaze.class.isAssignableFrom(clazz)) {
                entity = new EntityBlaze(world);
            } else if (Villager.class.isAssignableFrom(clazz)) {
                entity = new EntityVillager(world);
            } else if (ComplexLivingEntity.class.isAssignableFrom(clazz)) {
                if (EnderDragon.class.isAssignableFrom(clazz)) {
                    entity = new EntityDragon(world);
                }
            }

            if (entity != null) {
                entity.setLocationAndAngles(x, y, z, pitch, yaw);
            }
        } else if (Painting.class.isAssignableFrom(clazz)) {
            Block block = getBlockAt(location);
            BlockFace face = BlockFace.SELF;
            if (block.getRelative(BlockFace.EAST).getTypeId() == 0) {
                face = BlockFace.EAST;
            } else if (block.getRelative(BlockFace.NORTH).getTypeId() == 0) {
                face = BlockFace.NORTH;
            } else if (block.getRelative(BlockFace.WEST).getTypeId() == 0) {
                face = BlockFace.WEST;
            } else if (block.getRelative(BlockFace.SOUTH).getTypeId() == 0) {
                face = BlockFace.SOUTH;
            }
            int dir;
            switch (face) {
            case EAST:
            default:
                dir = 0;
                break;
            case NORTH:
                dir = 1;
                break;
            case WEST:
                dir = 2;
                break;
            case SOUTH:
                dir = 3;
                break;
            }
            entity = new EntityPainting(world, (int) x, (int) y, (int) z, dir);
            if (((EntityPainting) entity).isDead) {
                entity = null;
            }
        } else if (TNTPrimed.class.isAssignableFrom(clazz)) {
            entity = new EntityTNTPrimed(world, x, y, z);
        } else if (ExperienceOrb.class.isAssignableFrom(clazz)) {
            entity = new EntityXPOrb(world, x, y, z, 0);
        } else if (Weather.class.isAssignableFrom(clazz)) {
            // not sure what this can do
            entity = new EntityLightningBolt(world, x, y, z);
        } else if (LightningStrike.class.isAssignableFrom(clazz)) {
            // what is this, I don't even
        } else if (Fish.class.isAssignableFrom(clazz)) {
            // this is not a fish, it's a bobber, and it's probably useless
            entity = new EntityFishHook(world);
            entity.setLocationAndAngles(x, y, z, pitch, yaw);
        }

        if (entity != null) {
            world.spawnEntityInWorld(entity);//, reason);
            return (T) BukkitEntity.getEntity(server, entity);
        }

        throw new IllegalArgumentException("Cannot spawn an entity for " + clazz.getName());
    }

    public ChunkSnapshot getEmptyChunkSnapshot(int x, int z, boolean includeBiome, boolean includeBiomeTempRain) {
        return BukkitChunk.getEmptyChunkSnapshot(x, z, this, includeBiome, includeBiomeTempRain);
    }

    public void setSpawnFlags(boolean allowMonsters, boolean allowAnimals) {
        world.setAllowedSpawnTypes(allowMonsters, allowAnimals);
    }

    public boolean getAllowAnimals() {
        return world.spawnPeacefulMobs;
    }

    public boolean getAllowMonsters() {
        return world.spawnHostileMobs;
    }

    public int getMaxHeight() {
        return world.getHeight();
    }

    public int getSeaLevel() {
        return 64;
    }

    public boolean getKeepSpawnInMemory() {
        return DimensionManager.shouldLoadSpawn(world.getWorldInfo().getDimension());
    }
    /**
     * FIXME - broken
     */
    public void setKeepSpawnInMemory(boolean keepLoaded) {
        //return true;
    }

    @Override
    public int hashCode() {
        return getUID().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        final BukkitWorld other = (BukkitWorld) obj;

        return this.getUID() == other.getUID();
    }

    public File getWorldFolder() {
        return new File(world.getSaveHandler().getSaveDirectoryName());
    }

    public void explodeBlock(Block block, float yield) {
        // First of all, don't explode fire
        if (block.getType().equals(org.bukkit.Material.AIR) || block.getType().equals(org.bukkit.Material.FIRE)) {
            return;
        }
        int blockId = block.getTypeId();
        int blockX = block.getX();
        int blockY = block.getY();
        int blockZ = block.getZ();
        // following code is lifted from Explosion.a(boolean), and modified
        net.minecraft.src.Block.blocksList[blockId].dropBlockAsItemWithChance(this.world, blockX, blockY, blockZ, block.getData(), yield, 0);
        block.setType(org.bukkit.Material.AIR);
        // not sure what this does, seems to have something to do with the 'base' material of a block.
        // For example, WOODEN_STAIRS does something with WOOD in this method
        net.minecraft.src.Block.blocksList[blockId].onBlockDestroyedByExplosion(this.world, blockX, blockY, blockZ);//(this.world, blockX, blockY, blockZ);
    }

    public void sendPluginMessage(Plugin source, String channel, byte[] message) {
        StandardMessenger.validatePluginMessage(server.getMessenger(), source, channel, message);

        for (Player player : getPlayers()) {
            player.sendPluginMessage(source, channel, message);
        }
    }

    public Set<String> getListeningPluginChannels() {
        Set<String> result = new HashSet<String>();

        for (Player player : getPlayers()) {
            result.addAll(player.getListeningPluginChannels());
        }

        return result;
    }

    public org.bukkit.WorldType getWorldType() {
        return org.bukkit.WorldType.getByName(world.getWorldInfo().getTerrainType().getWorldTypeName());
    }

    public boolean canGenerateStructures() {
        return world.getWorldInfo().isMapFeaturesEnabled();
    }

    public long getTicksPerAnimalSpawns() {
        return 400L;
    }

    /**
     * FIXME
     * @deprecated
     */
    public void setTicksPerAnimalSpawns(int ticksPerAnimalSpawns) {
        //
    }

    public long getTicksPerMonsterSpawns() {
        return 400L;
    }
    /**
     * FIXME
     * @deprecated
     */
    public void setTicksPerMonsterSpawns(int ticksPerMonsterSpawns) {
        //world.ticksPerMonsterSpawns = ticksPerMonsterSpawns;
    }

    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        server.getWorldMetadata().setMetadata(this, metadataKey, newMetadataValue);
    }

    public List<MetadataValue> getMetadata(String metadataKey) {
        return server.getWorldMetadata().getMetadata(this, metadataKey);
    }

    public boolean hasMetadata(String metadataKey) {
        return server.getWorldMetadata().hasMetadata(this, metadataKey);
    }

    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        server.getWorldMetadata().removeMetadata(this, metadataKey, owningPlugin);
    }

    public int getMonsterSpawnLimit() {
        if (monsterSpawn < 0) {
            return server.getMonsterSpawnLimit();
        }

        return monsterSpawn;
    }

    public void setMonsterSpawnLimit(int limit) {
        monsterSpawn = limit;
    }

    public int getAnimalSpawnLimit() {
        if (animalSpawn < 0) {
            return server.getAnimalSpawnLimit();
        }

        return animalSpawn;
    }

    public void setAnimalSpawnLimit(int limit) {
        animalSpawn = limit;
    }

    public int getWaterAnimalSpawnLimit() {
        if (waterAnimalSpawn < 0) {
            return server.getWaterAnimalSpawnLimit();
        }

        return waterAnimalSpawn;
    }

    public void setWaterAnimalSpawnLimit(int limit) {
        waterAnimalSpawn = limit;
    }

    public void playSound(Location loc, Sound sound, float volume, float pitch) {
        if (loc == null || sound == null) return;

        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        // TODO
        getHandle().playSound(x, y, z, BukkitSound.getSound(sound), volume, pitch);
    }

	@Override
	public int getAmbientSpawnLimit() {
		// TODO Auto-generated method stub
		return ambientSpawn;
	}

	@Override
	public void setAmbientSpawnLimit(int limit) {
		ambientSpawn = limit;
	}

	@Override
	public String[] getGameRules() {
		return world.getGameRules().getRules();
	}

	@Override
	public String getGameRuleValue(String rule) {
		return world.getGameRules().getGameRuleStringValue(rule);
	}

	@Override
	public boolean setGameRuleValue(String rule, String value) {
		world.getGameRules().addGameRule(rule, value);
		return true;
	}

	@Override
	public boolean isGameRule(String rule) {
		return world.getGameRules().hasRule(rule);
	}
}
