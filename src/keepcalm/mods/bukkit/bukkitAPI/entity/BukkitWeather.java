package keepcalm.mods.bukkit.bukkitAPI.entity;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import net.minecraft.src.EntityWeatherEffect;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Weather;
//import org.bukkit.craftbukkit.BukkitServer;

public class BukkitWeather extends BukkitEntity implements Weather {
    public BukkitWeather(final BukkitServer server, final EntityWeatherEffect entity) {
        super(server, entity);
    }

    @Override
    public EntityWeatherEffect getHandle() {
        return (EntityWeatherEffect) entity;
    }

    @Override
    public String toString() {
        return "BukkitWeather";
    }

    public EntityType getType() {
        return EntityType.WEATHER;
    }
}
