package org.bukkit.craftbukkit.entity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import keepcalm.mods.bukkit.BukkitContainer;
import keepcalm.mods.bukkit.forgeHandler.ForgeEventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet131MapData;
import net.minecraft.network.packet.Packet200Statistic;
import net.minecraft.network.packet.Packet202PlayerAbilities;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.network.packet.Packet4UpdateTime;
import net.minecraft.network.packet.Packet53BlockChange;
import net.minecraft.network.packet.Packet54PlayNoteBlock;
import net.minecraft.network.packet.Packet61DoorChange;
import net.minecraft.network.packet.Packet62LevelSound;
import net.minecraft.network.packet.Packet6SpawnPosition;
import net.minecraft.network.packet.Packet70GameEvent;
import net.minecraft.server.management.BanEntry;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.EnumGameType;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.Validate;
import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ManuallyAbandonedConversationCanceller;
import org.bukkit.craftbukkit.CraftConversationTracker;
import org.bukkit.craftbukkit.CraftEffect;
import org.bukkit.craftbukkit.CraftOfflinePlayer;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftSound;
import org.bukkit.craftbukkit.CraftTeleporter;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.map.CraftMapView;
import org.bukkit.craftbukkit.map.RenderData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerUnregisterChannelEvent;
import org.bukkit.inventory.InventoryView.Property;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.StandardMessenger;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MapMaker;

import cpw.mods.fml.common.network.FMLNetworkHandler;

@DelegateDeserialization(CraftOfflinePlayer.class)
public class CraftPlayer extends CraftHumanEntity implements Player, CommandSender {
	private long firstPlayed = 0;
	private long lastPlayed = 0;
	//private boolean hasPlayedBefore = false;
	private final CraftConversationTracker conversationTracker = new CraftConversationTracker();
	private final Set<String> channels = new HashSet<String>();
	private final Map<String, Player> hiddenPlayers = new MapMaker().softValues().makeMap();
	private int hash = 0;
	private long playerOffset;
	//private long playerCurrentTime;
	private boolean isTimeRelative;

	public CraftPlayer(CraftServer server, EntityPlayerMP entity) {
		super(server, entity);
		perm.recalculatePermissions();
	}

	public CraftPlayer(EntityPlayerMP player) {
		this((CraftServer) Bukkit.getServer(), player);
	}

	@Override
	public String getName() {
		return getHandle().username;
	}

	@Override
	public boolean isOp() {
		try {
			return server.getHandle().getConfigurationManager().getOps().contains(getName().toLowerCase());
		}
		catch (NullPointerException e) {
			return false;
		}
	}

	@Override
	public void setOp(boolean value) {
		if (value == isOp()) return;

		if (value) {
			server.getHandle().getConfigurationManager().addOp(getName().toLowerCase());
		} else {
			server.getHandle().getConfigurationManager().removeOp(getName().toLowerCase());
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
		return ForgeEventHandler.playerDisplayNames.containsKey(getHandle().username) ? ForgeEventHandler.playerDisplayNames.get(getHandle().username) : getHandle().username;
	}

	public void setDisplayName(final String name) {
		ForgeEventHandler.playerDisplayNames.put(getHandle().username, name);
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
        for (int i = 0; i < server.getHandle().getConfigurationManager().playerEntityList.size(); ++i) {
            if (((EntityPlayerMP) server.getHandle().getConfigurationManager().playerEntityList.get(i)).getEntityName().equals(name)) {
                throw new IllegalArgumentException(name + " is already assigned as a player list name for someone");
            }
        }

        //getHandle().listName = name;

        // Change the name on the client side
        Packet201PlayerInfo oldpacket = new Packet201PlayerInfo(oldName, false, 9999);
        Packet201PlayerInfo packet = new Packet201PlayerInfo(name, true, getHandle().ping);
        for (int i = 0; i < server.getHandle().getConfigurationManager().playerEntityList.size(); ++i) {
            EntityPlayerMP EntityPlayerMP = (EntityPlayerMP) server.getHandle().getConfigurationManager().playerEntityList.get(i);
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

		if (other instanceof CraftPlayer) {
			idEquals = this.getEntityId() == ((CraftPlayer) other).getEntityId();
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
		return new Location(CraftServer.instance().getWorld(getHandle().worldObj.provider.dimensionId), getHandle().getHomePosition().posX, getHandle().getHomePosition().posY, getHandle().getHomePosition().posZ);
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

		Packet62LevelSound packet = new Packet62LevelSound(CraftSound.getSound(sound), x, y, z, volume, pitch);
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

		int datavalue = data == null ? 0 : CraftEffect.getDataValue(effect, data);
		playEffect(loc, effect, datavalue);
	}

	/*public void sendBlockChange(Location loc, Material material, byte data) {
        sendBlockChange(loc, material.getId(), data);
    }*/

	public void sendBlockChange(Location loc, int material, byte data) {
		if (getHandle().playerNetServerHandler == null) return;

		Packet53BlockChange packet = new Packet53BlockChange(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), ((CraftWorld) loc.getWorld()).getHandle());

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

		RenderData data = ((CraftMapView) map).render(this);
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
		net.minecraft.entity.player.EntityPlayerMP/*was:EntityPlayer*/ entity = getHandle();

		if (getHealth() == 0 || entity.isDead/*was:dead*/) {
			return false;
		}

		if (entity.playerNetServerHandler/*was:playerConnection*/ == null || entity.playerNetServerHandler/*was:playerConnection*/.connectionClosed/*was:disconnected*/) {
			return false;
		}

		if (entity.ridingEntity/*was:vehicle*/ != null || entity.riddenByEntity/*was:passenger*/ != null) {
			return false;
		}

		// From = Players current Location
		Location from = this.getLocation();
		// To = Players new Location if Teleport is Successful
		Location to = location;
		// Create & Call the Teleport Event.
		PlayerTeleportEvent event = new PlayerTeleportEvent((Player) this, from, to, cause);
		server.getPluginManager().callEvent(event);

		// Return False to inform the Plugin that the Teleport was unsuccessful/cancelled.
		if (event.isCancelled()) {
			return false;
		}

		// Update the From Location
		from = event.getFrom();
		// Grab the new To Location dependent on whether the event was cancelled.
		to = event.getTo();
		// Grab the To and From World Handles.
		CraftWorld fromWorld = (CraftWorld) from.getWorld();
		CraftWorld toWorld = (CraftWorld) to.getWorld();
		// Check if the fromWorld and toWorld are the same.
		// we can use != because the point in memory will be the same
		if (fromWorld == toWorld) {
            if (BukkitContainer.DEBUG)
			    System.out.println("monodim TP: " + fromWorld + " > " + toWorld);
			entity.playerNetServerHandler.setPlayerLocation(location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw());
		} else {
			// Close any foreign inventory
            if (BukkitContainer.DEBUG)
                System.out.println("interdim TP" + fromWorld + " > " + toWorld + " ( to dimension " + toWorld.getHandle().provider.dimensionId);
			if (getHandle().openContainer != getHandle().inventoryContainer)
				getHandle().closeInventory();
			
			entity.mcServer.getConfigurationManager().transferPlayerToDimension(entity, toWorld.getHandle().provider.dimensionId, new CraftTeleporter(toWorld.getHandle()));
			entity.onUpdate();
			//entity.setLocationAndAngles(to.getX(), to.getY(), to.getZ(), to.getYaw(), to.getPitch());
			entity.playerNetServerHandler.setPlayerLocation(location.getX(), location.getY(),location.getZ(),location.getYaw(), location.getPitch());
		}

		// toWorld.refreshChunk(entity.chunkCoordX, entity.chunkCoordZ);
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
		server.getHandle().getConfigurationManager().playerNBTManagerObj.readPlayerData(getHandle());
	}

	public void saveData() {
		server.getHandle().getConfigurationManager().playerNBTManagerObj.writePlayerData(getHandle());
	}

	public void updateInventory() {
		//getHandle().updateInventory(getHandle().craftingInventory);f
		getHandle().updateHeldItem();
	}

	public void setSleepingIgnored(boolean isSleeping) {
		// FIXME - this will explode MC
		//getHandle().sleeping = isSleeping;
		//((CraftWorld) getWorld()).getHandle().updateAllPlayersSleepingFlag();
		CraftServer.setPlayerFauxSleeping(getName(), isSleeping);
	}

	public boolean isSleepingIgnored() {
		return CraftServer.instance().isFauxSleeping(this.getName());
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
		return server.getHandle().getConfigurationManager().getBannedPlayers().isBanned(getName().toLowerCase());
	}

	public void setBanned(boolean value) {
		if (value) {
			BanEntry entry = new BanEntry(getName().toLowerCase());
			server.getHandle().getConfigurationManager().getBannedPlayers().put(entry);
		} else {
			server.getHandle().getConfigurationManager().getBannedPlayers().remove(getName().toLowerCase());
		}

		server.getHandle().getConfigurationManager().getBannedPlayers().saveToFileWithHeader();
	}

	public boolean isWhitelisted() {
		return server.getHandle().getConfigurationManager().getWhiteListedPlayers().contains(getName().toLowerCase());
	}

	public void setWhitelisted(boolean value) {
		if (value) {
			server.getHandle().getConfigurationManager().addToWhiteList(getName().toLowerCase());
		} else {
			server.getHandle().getConfigurationManager().removeFromWhitelist(getName().toLowerCase());
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
		NBTTagCompound nbt = new NBTTagCompound();
		getHandle().getFoodStats().writeNBT(nbt);
		nbt.setInteger("foodLevel", value);
		getHandle().getFoodStats().readNBT(nbt);
	}

	public Location getBedSpawnLocation() {
		World world = ((CraftServer)getServer()).getWorld(0);
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
		if (getHandle().playerNetServerHandler/*was:playerConnection*/ == null) return;
		if (equals(player)) return;
		if (hiddenPlayers.containsKey(player.getName())) return;
		hiddenPlayers.put(player.getName(), player);

		//remove this player from the hidden player's EntityTrackerEntry
		net.minecraft.entity.EntityTracker/*was:EntityTracker*/ tracker = ((net.minecraft.world.WorldServer/*was:WorldServer*/) entity.worldObj/*was:world*/).getEntityTracker();
		net.minecraft.entity.player.EntityPlayerMP/*was:EntityPlayer*/ other = ((CraftPlayer) player).getHandle();
		net.minecraft.entity.EntityTrackerEntry/*was:EntityTrackerEntry*/ entry = (net.minecraft.entity.EntityTrackerEntry/*was:EntityTrackerEntry*/) tracker.trackedEntityIDs/*was:trackedEntities*/.lookup/*was:get*/(other.entityId/*was:id*/);
		if (entry != null) {
			entry.removePlayerFromTracker/*was:clear*/(getHandle());
		}

		//remove the hidden player from this player user list
		getHandle().playerNetServerHandler/*was:playerConnection*/.sendPacketToPlayer/*was:sendPacket*/(new net.minecraft.network.packet.Packet201PlayerInfo/*was:Packet201PlayerInfo*/(player.getPlayerListName(), false, 9999));   
	}

	public void showPlayer(Player player) {
        	Validate.notNull(player, "shown player cannot be null");
        	if (getHandle().playerNetServerHandler == null) return;
       		if (equals(player)) return;
        	if (!hiddenPlayers.containsKey(player.getName())) return;
        	hiddenPlayers.remove(player.getName());

		net.minecraft.entity.EntityTracker/*was:EntityTracker*/ tracker = ((net.minecraft.world.WorldServer/*was:WorldServer*/) entity.worldObj/*was:world*/).getEntityTracker();
		net.minecraft.entity.player.EntityPlayerMP/*was:EntityPlayer*/ other = ((CraftPlayer) player).getHandle();
		EntityTrackerEntry entry = (EntityTrackerEntry) tracker.trackedEntityIDs.lookup(other.entityId);
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
		return "CraftPlayer{" + "name=" + getName() + '}';
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
		return BukkitContainer.users.containsKey(getName().toLowerCase());
	}

	public void setFirstPlayed(long firstPlayed) {
		this.firstPlayed = firstPlayed;
	}

	public void readExtraData(NBTTagCompound nbttagcompound) {
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
			FMLNetworkHandler.handlePacket250Packet(packet, 
					getHandle().playerNetServerHandler.netManager, 
					getHandle().playerNetServerHandler);
			//getHandle().playerNetServerHandler.sendPacketToPlayer(packet);
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
					Logger.getLogger(CraftPlayer.class.getName()).log(Level.SEVERE, "Could not send Plugin Channel REGISTER to " + getName(), ex);
				}
			}

			packet.data = stream.toByteArray();
			packet.length = packet.data.length;

			FMLNetworkHandler.handlePacket250Packet(packet, getHandle().playerNetServerHandler.netManager, getHandle().playerNetServerHandler);

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
	 * TODO - Involes making lots of tileEntityxxxs in Containers public
	 */
	public boolean setWindowProperty(Property prop, int value) {

		Container container = getHandle().openContainer;
		InventoryType type;
		if (container instanceof ContainerRepair) {
			type = InventoryType.ANVIL;
			// nothing to do
			return true;
		}
		else if (container instanceof ContainerBeacon) {
			type = InventoryType.BEACON;
			return true;
		}
		else if (container instanceof ContainerBrewingStand) {
			type = InventoryType.BREWING;
			//ContainerBrewingStand brew = (ContainerBrewingStand) container;

		}
		else if (container instanceof ContainerChest) {
			type = InventoryType.CHEST;
		}
		else if (container instanceof ContainerDispenser) {
			type = InventoryType.DISPENSER;
		}
		else if (container instanceof ContainerEnchantment) {
			type = InventoryType.ENCHANTING;
		}
		else if (container instanceof ContainerFurnace) {
			type = InventoryType.FURNACE;
		}
		else if (container instanceof ContainerMerchant) {
			type = InventoryType.MERCHANT;
		}
		else if (container instanceof ContainerPlayer) {
			type = InventoryType.PLAYER;
		}
		else if (container instanceof ContainerWorkbench) {
			type = InventoryType.WORKBENCH;
		}
		else {

			// fail
			return true;
		}
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

		/*Packet202PlayerAbilities pack = getAbilitiesPacket();
		pack.setFlying(value);
		this.updateAbilities(pack);*/
		getHandle().capabilities.isFlying = true;
		getHandle().sendPlayerAbilities();
		//getHandle().capabilities.allowFlying = true;
		//updateAbilities(getHandle().capabilities);
		//Compile! updateAbilities(new Packet202PlayerAbilities(getHandle().capabilities));

	}
	private void updateAbilities(Packet202PlayerAbilities j) {
		//Packet202PlayerAbilities j = new Packet202PlayerAbilities(getHandle().capabilities);
		getHandle().playerNetServerHandler.handlePlayerAbilities(j);
		PlayerCapabilities pl = getHandle().capabilities;
		pl.allowFlying = j.getAllowFlying();
		pl.disableDamage = j.getDisableDamage();
		pl.isCreativeMode = j.isCreativeMode();
		//pl.setFlySpeed(j.getFlySpeed());
	}

	private Packet202PlayerAbilities getAbilitiesPacket() {
		return new Packet202PlayerAbilities(getHandle().capabilities);
	}
	public boolean getAllowFlight() {
		return getHandle().capabilities.allowFlying;
	}

	public void setAllowFlight(boolean value) {
		getHandle().capabilities.allowFlying = true;
		updateAbilities(new Packet202PlayerAbilities(getHandle().capabilities));
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
		Packet202PlayerAbilities pack = getAbilitiesPacket();
		pack.setFlySpeed(value);

		updateAbilities(pack);

	}

	public void setWalkSpeed(float value) {
		validateSpeed(value);
		Packet202PlayerAbilities pack = getAbilitiesPacket();
		pack.setWalkSpeed(value);
		updateAbilities(pack);
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
		getHandle().experienceLevel += amount;
	}

	@Override
	public void setBedSpawnLocation(Location location, boolean force) {
		getHandle().setSpawnChunk(new ChunkCoordinates(location.getBlockX(),  location.getBlockY(), location.getBlockZ()), force);
	}

	@Override
	public void setTexturePack(String url) {
		// assume 16
		getHandle().requestTexturePackLoad(url, 16);
	}
}
