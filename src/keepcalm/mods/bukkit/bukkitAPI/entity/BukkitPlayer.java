package keepcalm.mods.bukkit.bukkitAPI.entity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import keepcalm.mods.bukkit.bukkitAPI.BukkitConversationTracker;
import keepcalm.mods.bukkit.bukkitAPI.BukkitEffect;
import keepcalm.mods.bukkit.bukkitAPI.BukkitOfflinePlayer;
import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import keepcalm.mods.bukkit.bukkitAPI.BukkitSound;
import keepcalm.mods.bukkit.bukkitAPI.BukkitWorld;
import keepcalm.mods.bukkit.bukkitAPI.inventory.BukkitContainer;
import keepcalm.mods.bukkit.bukkitAPI.map.BukkitMapView;
import keepcalm.mods.bukkit.bukkitAPI.map.RenderData;
import keepcalm.mods.bukkit.forgeHandler.ForgeEventHandler;
import net.minecraft.src.BanEntry;
import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.EntityTracker;
import net.minecraft.src.EntityTrackerEntry;
import net.minecraft.src.EnumGameType;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NetServerHandler;
import net.minecraft.src.Packet131MapData;
import net.minecraft.src.Packet200Statistic;
import net.minecraft.src.Packet201PlayerInfo;
import net.minecraft.src.Packet202PlayerAbilities;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.Packet3Chat;
import net.minecraft.src.Packet4UpdateTime;
import net.minecraft.src.Packet53BlockChange;
import net.minecraft.src.Packet54PlayNoteBlock;
import net.minecraft.src.Packet61DoorChange;
import net.minecraft.src.Packet62LevelSound;
import net.minecraft.src.Packet6SpawnPosition;
import net.minecraft.src.Packet70GameEvent;
import net.minecraft.src.WorldServer;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.Validate;
import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ManuallyAbandonedConversationCanceller;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.player.PlayerUnregisterChannelEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.InventoryView.Property;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.StandardMessenger;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MapMaker;

@DelegateDeserialization(BukkitOfflinePlayer.class)
public class BukkitPlayer extends BukkitEntityHuman implements Player, CommandSender, Permissible {
    private long firstPlayed = 0;
    private long lastPlayed = 0;
    private boolean hasPlayedBefore = false;
    private final BukkitConversationTracker conversationTracker = new BukkitConversationTracker();
    private final Set<String> channels = new HashSet<String>();
    private final Map<String, Player> hiddenPlayers = new MapMaker().softValues().makeMap();
    private int hash = 0;
    private long playerOffset;
    private long playerCurrentTime;
	private boolean isTimeRelative;

    public BukkitPlayer(BukkitServer server, EntityPlayerMP entity) {
        super(server, entity);

        firstPlayed = System.currentTimeMillis();
    }

    public BukkitPlayer(EntityPlayerMP player) {
		this((BukkitServer) Bukkit.getServer(), player);
	}

	@Override
    public boolean isOp() {
        return server.getHandle().getConfigurationManager().getOps().contains(getName());
    }

    @Override
    public void setOp(boolean value) {
        if (value == isOp()) return;

        if (value) {
            server.getHandle().getConfigurationManager().addOp(getName());
        } else {
            server.getHandle().getConfigurationManager().removeOp(getName());
        }

        perm.recalculatePermissions();
    }

    public boolean isOnline() {
        for (Object obj : server.getHandle().getConfigurationManager().playerEntityList) {
            EntityPlayerMP player = (EntityPlayerMP) obj;
            if (player.username.equalsIgnoreCase(getName())) {
                return true;
            }
        }
        return false;
    }

    public InetSocketAddress getAddress() {
        if (getHandle().playerNetServerHandler == null) return null;

        SocketAddress addr = getHandle().playerNetServerHandler.netManager.getSocketAddress();
        if (addr instanceof InetSocketAddress) {
            return (InetSocketAddress) addr;
        } else {
            return null;
        }
    }

    @Override
    public double getEyeHeight() {
        return getEyeHeight(false);
    }

    @Override
    public double getEyeHeight(boolean ignoreSneaking) {
        if (ignoreSneaking) {
            return 1.62D;
        } else {
            if (isSneaking()) {
                return 1.54D;
            } else {
                return 1.62D;
            }
        }
    }

    public void sendRawMessage(String message) {
        if (getHandle().playerNetServerHandler == null) return;

        getHandle().playerNetServerHandler.sendPacketToPlayer(new Packet3Chat(message));
    }

    public void sendMessage(String message) {
        if (!conversationTracker.isConversingModaly()) {
            this.sendRawMessage(message);
        }
    }

    public void sendMessage(String[] messages) {
        for (String message : messages) {
            sendMessage(message);
        }
    }

    public String getDisplayName() {
        return ForgeEventHandler.playerDisplayNames.containsKey(getName()) ? ForgeEventHandler.playerDisplayNames.get(getName()) : getName();
    }

    public void setDisplayName(final String name) {
    	System.out.println("New name: " + name);
    	ForgeEventHandler.playerDisplayNames.put(getName(), name);
    }

    public String getPlayerListName() {
        return getHandle().username;
    }

    public void setPlayerListName(String name) {
       /* String oldName = getHandle().getEntityName();

        if (name == null) {
            name = getName();
        }

        if (oldName.equals(name)) {
            return;
        }

        if (name.length() > 16) {
            throw new IllegalArgumentException("Player list names can only be a maximum of 16 characters long");
        }

        // Collisions will make for invisible people
        for (int i = 0; i < server.getHandle().getDedicatedPlayerList().playerEntityList.size(); ++i) {
            if (((EntityPlayerMP) server.getHandle().getDedicatedPlayerList().playerEntityList.get(i)).getEntityName().equals(name)) {
                throw new IllegalArgumentException(name + " is already assigned as a player list name for someone");
            }
        }

        //getHandle().listName = name;

        // Change the name on the client side
        Packet201PlayerInfo oldpacket = new Packet201PlayerInfo(oldName, false, 9999);
        Packet201PlayerInfo packet = new Packet201PlayerInfo(name, true, getHandle().ping);
        for (int i = 0; i < server.getHandle().getDedicatedPlayerList().playerEntityList.size(); ++i) {
            EntityPlayerMP EntityPlayerMP = (EntityPlayerMP) server.getHandle().getDedicatedPlayerList().playerEntityList.get(i);
            if (EntityPlayerMP.playerNetServerHandler == null) continue;

            if (this.getEntity(server, EntityPlayerMP).canSee(this)) {
                EntityPlayerMP.playerNetServerHandler.sendPacketToPlayer(oldpacket);
                ((NetServerHandler) EntityPlayerMP.playerNetServerHandler).sendPacketToPlayer(packet);
            }
        }*/
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof OfflinePlayer)) {
            return false;
        }
        OfflinePlayer other = (OfflinePlayer) obj;
        if ((this.getName() == null) || (other.getName() == null)) {
            return false;
        }

        boolean nameEquals = this.getName().equalsIgnoreCase(other.getName());
        boolean idEquals = true;

        if (other instanceof BukkitPlayer) {
            idEquals = this.getEntityId() == ((BukkitPlayer) other).getEntityId();
        }

        return nameEquals && idEquals;
    }

    public void kickPlayer(String message) {
        if (getHandle().playerNetServerHandler == null) return;

        getHandle().playerNetServerHandler.kickPlayerFromServer(message == null ? "" : message);
    }

    public void setCompassTarget(Location loc) {
        if (getHandle().playerNetServerHandler == null) return;

        // Do not directly assign here, from the packethandler we'll assign it.
        getHandle().playerNetServerHandler.sendPacketToPlayer(new Packet6SpawnPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
    }

    public Location getCompassTarget() {
        return new Location(Bukkit.getWorld(getHandle().worldObj.getWorldInfo().getWorldName()), getHandle().getHomePosition().posX, getHandle().getHomePosition().posY, getHandle().getHomePosition().posZ);
    }

    public void chat(String msg) {
        if (getHandle().playerNetServerHandler == null) return;
        getHandle().playerNetServerHandler.handleChat(new Packet3Chat(msg));
        //getHandle().playerNetServerHandler.(msg, false);
    }

    public boolean performCommand(String command) {
        return server.dispatchCommand(this, command);
    }

    public void playNote(Location loc, byte instrument, byte note) {
        if (getHandle().playerNetServerHandler == null) return;

        int id = getHandle().worldObj.getBlockId(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        getHandle().playerNetServerHandler.sendPacketToPlayer(new Packet54PlayNoteBlock(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), id, instrument, note));
    }

    public void playNote(Location loc, Instrument instrument, Note note) {
        if (getHandle().playerNetServerHandler == null) return;

        int id = getHandle().worldObj.getBlockId(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        getHandle().playerNetServerHandler.sendPacketToPlayer(new Packet54PlayNoteBlock(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), id, instrument.getType(), note.getId()));
    }

    public void playSound(Location loc, Sound sound, float volume, float pitch) {
        if (loc == null || sound == null || getHandle().playerNetServerHandler == null) return;

        double x = loc.getBlockX() + 0.5;
        double y = loc.getBlockY() + 0.5;
        double z = loc.getBlockZ() + 0.5;

        Packet62LevelSound packet = new Packet62LevelSound(BukkitSound.getSound(sound), x, y, z, volume, pitch);
        getHandle().playerNetServerHandler.sendPacketToPlayer(packet);
    }

    public void playEffect(Location loc, Effect effect, int data) {
        if (getHandle().playerNetServerHandler == null) return;

        int packetData = effect.getId();
        Packet61DoorChange packet = new Packet61DoorChange(packetData, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), data, false);
        getHandle().playerNetServerHandler.sendPacketToPlayer(packet);
    }

    public <T> void playEffect(Location loc, Effect effect, T data) {
        if (data != null) {
            Validate.isTrue(data.getClass().equals(effect.getData()), "Wrong kind of data for this effect!");
        } else {
            Validate.isTrue(effect.getData() == null, "Wrong kind of data for this effect!");
        }

        int datavalue = data == null ? 0 : BukkitEffect.getDataValue(effect, data);
        playEffect(loc, effect, datavalue);
    }

    /*public void sendBlockChange(Location loc, Material material, byte data) {
        sendBlockChange(loc, material.getId(), data);
    }*/

    public void sendBlockChange(Location loc, int material, byte data) {
        if (getHandle().playerNetServerHandler == null) return;

        Packet53BlockChange packet = new Packet53BlockChange(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), ((BukkitWorld) loc.getWorld()).getHandle());

        packet.type = material;
        packet.metadata = data;
        getHandle().playerNetServerHandler.sendPacketToPlayer(packet);
    }

    public boolean sendChunkChange(Location loc, int sx, int sy, int sz, byte[] data) {
        if (getHandle().playerNetServerHandler == null) return false;

        /*
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        int cx = x >> 4;
        int cz = z >> 4;

        if (sx <= 0 || sy <= 0 || sz <= 0) {
            return false;
        }

        if ((x + sx - 1) >> 4 != cx || (z + sz - 1) >> 4 != cz || y < 0 || y + sy > 128) {
            return false;
        }

        if (data.length != (sx * sy * sz * 5) / 2) {
            return false;
        }

        Packet51MapChunk packet = new Packet51MapChunk(x, y, z, sx, sy, sz, data);

        getHandle().playerNetServerHandler.sendPacket(packet);

        return true;
        */

        throw new NotImplementedException("Chunk changes do not yet work"); // TODO: Chunk changes.
    }

    public void sendMap(MapView map) {
        if (getHandle().playerNetServerHandler == null) return;

        RenderData data = ((BukkitMapView) map).render(this);
        for (int x = 0; x < 128; ++x) {
            byte[] bytes = new byte[131];
            bytes[1] = (byte) x;
            for (int y = 0; y < 128; ++y) {
                bytes[y + 3] = data.buffer[y * 128 + x];
            }
            Packet131MapData packet = new Packet131MapData((short) Material.MAP.getId(), map.getId(), bytes);
            getHandle().playerNetServerHandler.sendPacketToPlayer(packet);
        }
    }

    @Override
    public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause cause) {
        if (getHandle().playerNetServerHandler == null) return false;

        // From = Players current Location
        Location from = this.getLocation();
        // To = Players new Location if Teleport is Successful
        Location to = location;
        // Create & Call the Teleport Event.
        PlayerTeleportEvent event = new PlayerTeleportEvent((Player) this, from, to, cause);
        server.getPluginManager().callEvent(event);
        // Return False to inform the Plugin that the Teleport was unsuccessful/cancelled.
        if (event.isCancelled() == true) {
            return false;
        }
        // Update the From Location
        from = event.getFrom();
        // Grab the new To Location dependent on whether the event was cancelled.
        to = event.getTo();
        // Grab the To and From World Handles.
        WorldServer fromWorld = ((BukkitWorld) from.getWorld()).getHandle();
        WorldServer toWorld = ((BukkitWorld) to.getWorld()).getHandle();
        // Grab the EntityPlayerMP
        EntityPlayerMP entity = getHandle();

        // Check if the fromWorld and toWorld are the same.
        if (fromWorld == toWorld) {
            entity.setPositionAndUpdate(location.getX(), location.getY(), location.getZ());
        } else {
            // Close any foreign inventory
            //if (getHandle().craftingInventory != getHandle().){
                getHandle().closeInventory();
            //}
            server.getHandle().getDedicatedPlayerList().transferPlayerToDimension(entity, toWorld.getWorldInfo().getDimension());
        }
        return true;
    }

    public void setSneaking(boolean sneak) {
        getHandle().setSneaking(sneak);
    }

    public boolean isSneaking() {
        return getHandle().isSneaking();
    }

    public boolean isSprinting() {
        return getHandle().isSprinting();
    }

    public void setSprinting(boolean sprinting) {
        getHandle().setSprinting(sprinting);
    }

    public void loadData() {
        server.getHandle().getDedicatedPlayerList().playerNBTManagerObj.readPlayerData(getHandle());
    }

    public void saveData() {
        server.getHandle().getDedicatedPlayerList().playerNBTManagerObj.writePlayerData(getHandle());
    }

    public void updateInventory() {
        //getHandle().updateInventory(getHandle().craftingInventory);
    }

    public void setSleepingIgnored(boolean isSleeping) {
        getHandle().sleeping = isSleeping;
        ((BukkitWorld) getWorld()).getHandle().updateAllPlayersSleepingFlag();
    }

    public boolean isSleepingIgnored() {
        return BukkitServer.instance().isFauxSleeping(this.getName());
    }

    public void awardAchievement(Achievement achievement) {
        sendStatistic(achievement.getId(), 1);
    }

    public void incrementStatistic(Statistic statistic) {
        incrementStatistic(statistic, 1);
    }

    public void incrementStatistic(Statistic statistic, int amount) {
        sendStatistic(statistic.getId(), amount);
    }

    public void incrementStatistic(Statistic statistic, Material material) {
        incrementStatistic(statistic, material, 1);
    }

    public void incrementStatistic(Statistic statistic, Material material, int amount) {
        if (!statistic.isSubstatistic()) {
            throw new IllegalArgumentException("Given statistic is not a substatistic");
        }
        if (statistic.isBlock() != material.isBlock()) {
            throw new IllegalArgumentException("Given material is not valid for this substatistic");
        }

        int mat = material.getId();

        if (!material.isBlock()) {
            mat -= 255;
        }

        sendStatistic(statistic.getId() + mat, amount);
    }

    private void sendStatistic(int id, int amount) {
        if (getHandle().playerNetServerHandler == null) return;

        while (amount > Byte.MAX_VALUE) {
            sendStatistic(id, Byte.MAX_VALUE);
            amount -= Byte.MAX_VALUE;
        }

        getHandle().playerNetServerHandler.sendPacketToPlayer(new Packet200Statistic(id, amount));
    }

    public void setPlayerTime(long time, boolean relative) {
        //getHandle().time = time;
        //getHandle().relativeTime = relative;
    	Packet4UpdateTime p = new Packet4UpdateTime();
    	this.isTimeRelative = relative;
    	p.time = relative ? getHandle().getServerForPlayer().getWorldTime() + time : time;
    	this.playerOffset = time - getHandle().getServerForPlayer().getWorldTime();
    	getHandle().playerNetServerHandler.sendPacketToPlayer(p);
    }

    public long getPlayerTimeOffset() {
        return this.playerOffset;
    }

    public long getPlayerTime() {
        return getHandle().getServerForPlayer().getWorldTime() + playerOffset;
    }

    public boolean isPlayerTimeRelative() {
        return isTimeRelative;
    }

    public void resetPlayerTime() {
        setPlayerTime(0, true);
    }

    public boolean isBanned() {
        return server.getHandle().getDedicatedPlayerList().getBannedPlayers().isBanned(getName().toLowerCase());
    }

    public void setBanned(boolean value) {
        if (value) {
            BanEntry entry = new BanEntry(getName().toLowerCase());
            server.getHandle().getDedicatedPlayerList().getBannedPlayers().put(entry);
        } else {
            server.getHandle().getDedicatedPlayerList().getBannedPlayers().remove(getName().toLowerCase());
        }

        server.getHandle().getDedicatedPlayerList().getBannedPlayers().saveToFileWithHeader();
    }

    public boolean isWhitelisted() {
        return server.getHandle().getDedicatedPlayerList().getWhiteListedPlayers().contains(getName().toLowerCase());
    }

    public void setWhitelisted(boolean value) {
        if (value) {
            server.getHandle().getDedicatedPlayerList().addToWhiteList(getName().toLowerCase());
        } else {
            server.getHandle().getDedicatedPlayerList().removeFromWhitelist(getName().toLowerCase());
        }
    }

    @Override
    public void setGameMode(GameMode mode) {
        if (getHandle().playerNetServerHandler == null) return;

        if (mode == null) {
            throw new IllegalArgumentException("Mode cannot be null");
        }

        if (mode != getGameMode()) {
            PlayerGameModeChangeEvent event = new PlayerGameModeChangeEvent(this, mode);
            server.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }

            getHandle().theItemInWorldManager.setGameType(EnumGameType.getByID(mode.getValue()));
            getHandle().playerNetServerHandler.sendPacketToPlayer(new Packet70GameEvent(3, mode.getValue()));
        }
    }

    @Override
    public GameMode getGameMode() {
        return GameMode.getByValue(getHandle().theItemInWorldManager.getGameType().getID());
    }

    public void giveExp(int exp) {
        getHandle().experienceTotal += (exp);
    }

    public float getExp() {
        return getHandle().experience;
    }

    public void setExp(float exp) {
        getHandle().experience = exp;
        //getHandle().xp = -1;
    }

    public int getLevel() {
        return getHandle().experienceLevel;
    }

    public void setLevel(int level) {
        getHandle().experienceLevel = level;
        //getHandle().lastSentExp = -1;
    }

    public int getTotalExperience() {
        return getHandle().experienceTotal;
    }

    public void setTotalExperience(int exp) {
        getHandle().experienceTotal = exp;
    }

    public float getExhaustion() {
        return getHandle().getFoodStats().foodExhaustionLevel;
    }

    public void setExhaustion(float value) {
        getHandle().getFoodStats().foodExhaustionLevel = value;
    }

    public float getSaturation() {
        return getHandle().getFoodStats().getSaturationLevel();
    }

    public void setSaturation(float value) {
        getHandle().getFoodStats().setFoodSaturationLevel(value);
    }

    public int getFoodLevel() {
        return getHandle().getFoodStats().getFoodLevel();
    }

    public void setFoodLevel(int value) {
        getHandle().getFoodStats().setFoodLevel(value);
    }

    public Location getBedSpawnLocation() {
        World world = getServer().getWorld(((BukkitServer) getServer()).getHandle().worldServerForDimension(0).getWorldInfo().getWorldName());
        if ((world != null) && (getHandle().getHomePosition() != null)) {
            return new Location(world, getHandle().getHomePosition().posX, getHandle().getHomePosition().posY, getHandle().getHomePosition().posZ);
        }
        return null;
    }

    public void setBedSpawnLocation(Location location) {
    	/* what does spawnForced mean? */
        getHandle().setSpawnChunk(new ChunkCoordinates(location.getBlockX(), location.getBlockY(), location.getBlockZ()), false);
        //getHandle().spawnWorld = location.getWorld().getName();
    }

    public void hidePlayer(Player player) {
        Validate.notNull(player, "hidden player cannot be null");
        if (getHandle().playerNetServerHandler == null) return;
        if (equals(player)) return;
        if (hiddenPlayers.containsKey(player.getName())) return;
        hiddenPlayers.put(player.getName(), player);

        //remove this player from the hidden player's EntityTrackerEntry
        EntityTracker tracker = ((WorldServer) entity.worldObj).getEntityTracker();
        EntityPlayerMP other = ((BukkitPlayer) player).getHandle();
        // TODO: Does this work?
        EntityTrackerEntry entry = (EntityTrackerEntry) tracker.trackedEntities.toArray()[other.entityId];
        if (entry != null) {
            entry.removePlayerFromTracker(getHandle());
        }

        //remove the hidden player from this player user list
        getHandle().playerNetServerHandler.sendPacketToPlayer(new Packet201PlayerInfo(player.getPlayerListName(), false, 9999));
    }

    public void showPlayer(Player player) {
        Validate.notNull(player, "shown player cannot be null");
        if (getHandle().playerNetServerHandler == null) return;
        if (equals(player)) return;
        if (!hiddenPlayers.containsKey(player.getName())) return;
        hiddenPlayers.remove(player.getName());

        EntityTracker tracker = ((WorldServer) entity.worldObj).getEntityTracker();
        EntityPlayerMP other = ((BukkitPlayer) player).getHandle();
        EntityTrackerEntry entry = (EntityTrackerEntry) tracker.trackedEntities.toArray()[other.entityId];
        
        // FIXME: What does this do???
        //getHandle().player.remove(Integer.valueOf(other.entityId)); // Should be called destroyQueue
        if (entry != null && !entry.trackedPlayers.contains(getHandle())) {
            entry.tryStartWachingThis(getHandle());
        }

        getHandle().playerNetServerHandler.sendPacketToPlayer(new Packet201PlayerInfo(player.getPlayerListName(), true, getHandle().ping));
    }

    public boolean canSee(Player player) {
        return !hiddenPlayers.containsKey(player.getName());
    }

    public Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();

        result.put("name", getName());

        return result;
    }

    public Player getPlayer() {
        return this;
    }

    @Override
    public EntityPlayerMP getHandle() {
        return (EntityPlayerMP) entity;
    }

    public void setHandle(final EntityPlayerMP entity) {
        super.setHandle(entity);
    }

    @Override
    public String toString() {
        return "BukkitPlayer{" + "name=" + getName() + '}';
    }

    @Override
    public int hashCode() {
        if (hash == 0 || hash == 485) {
            hash = 97 * 5 + (this.getName() != null ? this.getName().toLowerCase().hashCode() : 0);
        }
        return hash;
    }

    public long getFirstPlayed() {
        return firstPlayed;
    }

    public long getLastPlayed() {
        return lastPlayed;
    }

    public boolean hasPlayedBefore() {
        return hasPlayedBefore;
    }

    public void setFirstPlayed(long firstPlayed) {
        this.firstPlayed = firstPlayed;
    }

    public void readExtraData(NBTTagCompound nbttagcompound) {
        hasPlayedBefore = true;
        if (nbttagcompound.hasKey("bukkit")) {
            NBTTagCompound data = nbttagcompound.getCompoundTag("bukkit");

            if (data.hasKey("firstPlayed")) {
                firstPlayed = data.getLong("firstPlayed");
                lastPlayed = data.getLong("lastPlayed");
            }

            if (data.hasKey("newExp")) {
                EntityPlayerMP handle = getHandle();
                handle.experienceValue = data.getInteger("newExp");
                handle.experienceTotal = data.getInteger("newTotalExp");
                handle.experienceLevel = data.getInteger("newLevel");
                //handle.exp = data.getInteger("expToDrop");
                //handle.exp = data.getBoolean("keepLevel");
            }
        }
    }

    public void setExtraData(NBTTagCompound nbttagcompound) {
        if (!nbttagcompound.hasKey("bukkit")) {
            nbttagcompound.setCompoundTag("bukkit", new NBTTagCompound());
        }

        NBTTagCompound data = nbttagcompound.getCompoundTag("bukkit");
        EntityPlayerMP handle = getHandle();
        data.setInteger("newExp", handle.experienceValue);
        data.setInteger("newTotalExp", handle.experienceTotal);
        data.setInteger("newLevel", handle.experienceLevel);
        //data.setInt("expToDrop", handle.expToDrop);
        //data.setBoolean("keepLevel", handle.keepLevel);
        data.setLong("firstPlayed", getFirstPlayed());
        data.setLong("lastPlayed", System.currentTimeMillis());
    }

    public boolean beginConversation(Conversation conversation) {
        return conversationTracker.beginConversation(conversation);
    }

    public void abandonConversation(Conversation conversation) {
        conversationTracker.abandonConversation(conversation, new ConversationAbandonedEvent(conversation, new ManuallyAbandonedConversationCanceller()));
    }

    public void abandonConversation(Conversation conversation, ConversationAbandonedEvent details) {
        conversationTracker.abandonConversation(conversation, details);
    }

    public void acceptConversationInput(String input) {
        conversationTracker.acceptConversationInput(input);
    }

    public boolean isConversing() {
        return conversationTracker.isConversing();
    }

    public void sendPluginMessage(Plugin source, String channel, byte[] message) {
        StandardMessenger.validatePluginMessage(server.getMessenger(), source, channel, message);
        if (getHandle().playerNetServerHandler == null) return;

        if (channels.contains(channel)) {
            Packet250CustomPayload packet = new Packet250CustomPayload();
            packet.channel = channel;
            packet.length = message.length;
            packet.data = message;
            getHandle().playerNetServerHandler.sendPacketToPlayer(packet);
        }
    }

    public void addChannel(String channel) {
        if (channels.add(channel)) {
            server.getPluginManager().callEvent(new PlayerRegisterChannelEvent(this, channel));
        }
    }

    public void removeChannel(String channel) {
        if (channels.remove(channel)) {
            server.getPluginManager().callEvent(new PlayerUnregisterChannelEvent(this, channel));
        }
    }

    public Set<String> getListeningPluginChannels() {
        return ImmutableSet.copyOf(channels);
    }

    public void sendSupportedChannels() {
        if (getHandle().playerNetServerHandler == null) return;
        Set<String> listening = server.getMessenger().getIncomingChannels();

        if (!listening.isEmpty()) {
            Packet250CustomPayload packet = new Packet250CustomPayload();

            packet.channel = "REGISTER";
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            for (String channel : listening) {
                try {
                    stream.write(channel.getBytes("UTF8"));
                    stream.write((byte) 0);
                } catch (IOException ex) {
                    Logger.getLogger(BukkitPlayer.class.getName()).log(Level.SEVERE, "Could not send Plugin Channel REGISTER to " + getName(), ex);
                }
            }

            packet.data = stream.toByteArray();
            packet.length = packet.data.length;

            getHandle().playerNetServerHandler.sendPacketToPlayer(packet);
        }
    }

    public EntityType getType() {
        return EntityType.PLAYER;
    }

    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        server.getPlayerMetadata().setMetadata(this, metadataKey, newMetadataValue);
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) {
        return server.getPlayerMetadata().getMetadata(this, metadataKey);
    }

    @Override
    public boolean hasMetadata(String metadataKey) {
        return server.getPlayerMetadata().hasMetadata(this, metadataKey);
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        server.getPlayerMetadata().removeMetadata(this, metadataKey, owningPlugin);
    }

    @Override
    /**
     * TODO
     */
    public boolean setWindowProperty(Property prop, int value) {
        Container container = getHandle().openContainer;
       
        
        return true;
    }

    public void disconnect(String reason) {
        conversationTracker.abandonAllConversations();
        perm.clearPermissions();
    }

    public boolean isFlying() {
        return getHandle().capabilities.isFlying;
    }

    public void setFlying(boolean value) {
        if (!getAllowFlight() && value) {
            throw new IllegalArgumentException("Cannot make player fly if getAllowFlight() is false");
        }

        getHandle().capabilities.isFlying = value;
        
    }
    private void updateAbilities() {
    	Packet202PlayerAbilities j = new Packet202PlayerAbilities(getHandle().capabilities);
        getHandle().playerNetServerHandler.handlePlayerAbilities(j);
    }
    public boolean getAllowFlight() {
        return getHandle().capabilities.allowFlying;
    }

    public void setAllowFlight(boolean value) {
        if (isFlying() && !value) {
            getHandle().capabilities.isFlying = false;
        }

        getHandle().capabilities.allowFlying = value;
        updateAbilities();
    }

    @Override
    public int getNoDamageTicks() {
        if (getHandle().hurtResistantTime > 0) {
            return Math.max(getHandle().maxHurtResistantTime, getHandle().hurtResistantTime);
        } else {
            return getHandle().hurtResistantTime;
        }
    }

    public void setFlySpeed(float value) {
        validateSpeed(value);
        EntityPlayerMP player = getHandle();
        player.capabilities.setFlySpeed(value / 2f);
        updateAbilities();

    }

    public void setWalkSpeed(float value) {
        validateSpeed(value);
        EntityPlayerMP player = getHandle();
        player.capabilities.walkSpeed = value / 2f;
        updateAbilities();
    }

    public float getFlySpeed() {
        return getHandle().capabilities.getFlySpeed() * 2f;
    }

    public float getWalkSpeed() {
        return getHandle().capabilities.walkSpeed * 2f;
    }

    private void validateSpeed(float value) {
        if (value < 0) {
            if (value < -1f) {
                throw new IllegalArgumentException(value + " is too low");
            }
        } else {
            if (value > 1f) {
                throw new IllegalArgumentException(value + " is too high");
            }
        }
    }

	@Override
	public void sendBlockChange(Location loc, Material material, byte data) {
		this.sendBlockChange(loc, material.getId(), data);
		
	}

	@Override
	public void giveExpLevels(int amount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBedSpawnLocation(Location location, boolean force) {
		// TODO Auto-generated method stub
		
	}
}
