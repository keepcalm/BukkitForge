package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.effect.EntityWeatherEffect;

import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Weather;
//import org.bukkit.craftbukkit.BukkitServer;

public class CraftWeather extends BukkitEntity implements Weather {
    public CraftWeather(final BukkitServer server, final EntityWeatherEffect entity) {
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
