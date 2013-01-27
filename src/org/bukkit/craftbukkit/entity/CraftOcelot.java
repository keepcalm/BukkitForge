package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.passive.EntityOcelot;

import org.apache.commons.lang.Validate;
import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ocelot;
//import org.bukkit.craftbukkit.CraftServer;

public class CraftOcelot extends BukkitTameableAnimal implements Ocelot {
    public CraftOcelot(BukkitServer server, EntityOcelot wolf) {
        super(server, wolf);
    }

    @Override
    public EntityOcelot getHandle() {
        return (EntityOcelot) entity;
    }

    public Type getCatType() {
        return Type.getType(getHandle().getTameSkin());
    }

    public void setCatType(Type type) {
        Validate.notNull(type, "Cat type cannot be null");
        getHandle().setTameSkin(type.getId());
    }

    @Override
    public EntityType getType() {
        return EntityType.OCELOT;
    }
}
