package org.bukkit.craftbukkit.v1_5_R2.block;

import net.minecraft.tileentity.TileEntityMobSpawner;

import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_5_R2.CraftWorld;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.EntityType;
//import org.bukkit.craftbukkit.CraftWorld;

public class CraftCreatureSpawner extends CraftBlockState implements CreatureSpawner {
    private final CraftWorld world;
    private final TileEntityMobSpawner spawner;

    public CraftCreatureSpawner(final Block block) {
        super(block);

        world = (CraftWorld) block.getWorld();
        spawner = (TileEntityMobSpawner) world.getTileEntityAt(getX(), getY(), getZ());
    }

    @Deprecated
    public CreatureType getCreatureType() {
        return CreatureType.fromName(spawner.func_98049_a().getEntityNameToSpawn());
    }

    public EntityType getSpawnedType() {
        return EntityType.fromName(spawner.func_98049_a().getEntityNameToSpawn());
    }

    @Deprecated
    public void setCreatureType(CreatureType creatureType) {
        spawner.func_98049_a().setMobID(creatureType.getName());
    }

    public void setSpawnedType(EntityType entityType) {
        if (entityType == null || entityType.getName() == null) {
            throw new IllegalArgumentException("Can't spawn EntityType " + entityType + " from mobspawners!");
        }

        spawner.func_98049_a().setMobID(entityType.getName());
    }

    @Deprecated
    public String getCreatureTypeId() {
        return spawner.func_98049_a().getEntityNameToSpawn();
    }

    @Deprecated
    public void setCreatureTypeId(String creatureName) {
        setCreatureTypeByName(creatureName);
    }

    public String getCreatureTypeName() {
        return spawner.func_98049_a().getEntityNameToSpawn();
    }

    public void setCreatureTypeByName(String creatureType) {
        // Verify input
        EntityType type = EntityType.fromName(creatureType);
        if (type == null) {
            return;
        }
        setSpawnedType(type);
    }

    public int getDelay() {
        return spawner.func_98049_a().spawnDelay;
    }

    public void setDelay(int delay) {
        spawner.func_98049_a().spawnDelay = delay;
    }

}
