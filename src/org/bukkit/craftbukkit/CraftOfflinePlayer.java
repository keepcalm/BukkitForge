package org.bukkit.craftbukkit;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import keepcalm.mods.bukkit.BukkitContainer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.management.BanEntry;
import net.minecraft.world.chunk.storage.AnvilSaveHandler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.craftbukkit.entity.BukkitEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

@SerializableAs("Player")
public class CraftOfflinePlayer implements OfflinePlayer, ConfigurationSerializable {
    private final String name;
    private final CraftServer server;
    private final AnvilSaveHandler storage;

    public CraftOfflinePlayer(BukkitServer server, String name) {
        this.server = server;
        this.name = name;
        this.storage = (AnvilSaveHandler) server.getHandle().getConfigurationManager().playerNBTManagerObj;
    }

    public boolean isOnline() {
        return getPlayer() != null;
    }

    public String getName() {
        return name;
    }

    public Server getServer() {
        return server;
    }

    public boolean isOp() {
        return server.getHandle().getConfigurationManager().getOps().contains(getName().toLowerCase());
    }

    public void setOp(boolean value) {
        if (value == isOp()) return;

        if (value) {
            server.getHandle().getConfigurationManager().addOp(getName().toLowerCase());
        } else {
            server.getHandle().getConfigurationManager().removeOp(getName().toLowerCase());
        }
    }

    public boolean isBanned() {
        return server.getHandle().getConfigurationManager().getBannedPlayers().isBanned(name.toLowerCase());
    }

    public void setBanned(boolean value) {
        if (value) {
            BanEntry entry = new BanEntry(name.toLowerCase());
            server.getHandle().getConfigurationManager().getBannedPlayers().put(entry);
        } else {
            server.getHandle().getConfigurationManager().getBannedPlayers().remove(name.toLowerCase());
        }

        server.getHandle().getConfigurationManager().getBannedPlayers().saveToFileWithHeader();
    }

    public boolean isWhitelisted() {
        return server.getHandle().getConfigurationManager().getWhiteListedPlayers().contains(name.toLowerCase());
    }

    public void setWhitelisted(boolean value) {
        if (value) {
            server.getHandle().getConfigurationManager().addToWhiteList(name.toLowerCase());
        } else {
            server.getHandle().getConfigurationManager().removeFromWhitelist(name.toLowerCase());
        }
    }

    public Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();

        result.put("name", name);

        return result;
    }

    public static OfflinePlayer deserialize(Map<String, Object> args) {
        return Craft.getServer().getOfflinePlayer((String) args.get("name"));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[name=" + name + "]";
    }

    public Player getPlayer() {
        for (Object obj : server.getHandle().getConfigurationManager().playerEntityList) {
            EntityPlayerMP player = (EntityPlayerMP) obj;
            if (player.username.equalsIgnoreCase(getName())) {
                return (Player) ((player.playerNetServerHandler != null) ? CraftEntity.getEntity(server, player) : null);
            }
        }

        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof OfflinePlayer)) {
            return false;
        }
        OfflinePlayer other = (OfflinePlayer) obj;
        if ((this.getName() == null) || (other.getName() == null)) {
            return false;
        }
        return this.getName().equalsIgnoreCase(other.getName());
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.getName() != null ? this.getName().toLowerCase().hashCode() : 0);
        return hash;
    }

    private NBTTagCompound getData() {
    	return storage.getPlayerData(name);
    	
    }

    private NBTTagCompound getBukkitData() {
        NBTTagCompound result = getData();

        if (result != null) {
            if (!result.hasKey("bukkit")) {
                result.setCompoundTag("bukkit", new NBTTagCompound());
            }
            result = result.getCompoundTag("bukkit");
        }

        return result;
    }

    private File getDataFile() {
        return new File(storage.getSaveDirectoryName() + (storage.getSaveDirectoryName().endsWith("/") ? "" : "/") + "players", name + ".dat");
    }

    public long getFirstPlayed() {
        Player player = getPlayer();
        if (player != null) return player.getFirstPlayed();

        NBTTagCompound data = getBukkitData();

        if (data != null) {
            if (data.hasKey("firstPlayed")) {
                return data.getLong("firstPlayed");
            } else {
                File file = getDataFile();
                return file.lastModified();
            }
        } else {
            return 0;
        }
    }

    public long getLastPlayed() {
        Player player = getPlayer();
        if (player != null) return player.getLastPlayed();

        NBTTagCompound data = getBukkitData();

        if (data != null) {
            if (data.hasKey("lastPlayed")) {
                return data.getLong("lastPlayed");
            } else {
                File file = getDataFile();
                return file.lastModified();
            }
        } else {
            return 0;
        }
    }

    public boolean hasPlayedBefore() {
    	System.out.println("Has " + name + " played before? " + CraftContainer.users.containsKey(name));
        return CraftContainer.users.containsKey(name);// != null;
    }

    public Location getBedSpawnLocation() {
        NBTTagCompound data = getData();
        if (data == null) return null;

        if (data.hasKey("SpawnX") && data.hasKey("SpawnY") && data.hasKey("SpawnZ")) {
            String spawnWorld = data.getString("SpawnWorld");
            if (spawnWorld.equals("")) {
                spawnWorld = server.getWorlds().get(0).getName();
            }
            return new Location(server.getWorld(spawnWorld), data.getInteger("SpawnX"), data.getInteger("SpawnY"), data.getInteger("SpawnZ"));
        }
        return null;
    }

    public void setMetadata(String metadataKey, MetadataValue metadataValue) {
        server.getPlayerMetadata().setMetadata(this, metadataKey, metadataValue);
    }
}