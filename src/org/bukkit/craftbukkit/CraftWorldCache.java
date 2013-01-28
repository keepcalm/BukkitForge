package org.bukkit.craftbukkit;

import com.google.common.collect.Maps;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import org.bukkit.World;

import java.util.*;

public class CraftWorldCache
{

    private HashMap<Integer,CraftWorld> worlds = new HashMap<Integer,CraftWorld>();
    private HashMap<String,Integer> worldNameMapping = Maps.newHashMap();
    
    public CraftWorld get(String name)
    {
        if(worldNameMapping.containsKey(name))
            return get(worldNameMapping.get(name));
        return null;
    }

    public CraftWorld get(UUID id)
    {
        for( CraftWorld cw : worlds.values() )
        {
            if( cw.getUID().equals(id) )
            {
                return cw;
            }
        }
        return null;
    }

    public CraftWorld get(int dim)
    {
        cacheIfNotPresent(dim);
        return worlds.get(dim);
    }

    private void cacheIfNotPresent(int dim) {
        if(!existsInCache(dim) && existsInDM(dim))
        {
            cacheWorld(dim, DimensionManager.getWorld(dim));
        }
    }

    private boolean existsInCache(int dim) {
        return worlds.containsKey(dim);
    }

    private boolean existsInDM(int dim) {
        return Arrays.asList( DimensionManager.getIDs()).contains(dim);
    }

    private void cacheWorld(int dim, WorldServer world)
    {
        if(world == null) return;
        worlds.put(dim, new CraftWorld(world));
        worldNameMapping.put(safeName(world.provider.getDimensionName()), dim);
    }

    private String safeName(String name)
    {
        return name.toLowerCase().replace( ' ', '_');
    }

    public List<World> getWorldsAsList() {
        return new ArrayList<World>(worlds.values());
    }

    public void remove(int id) {
        if(worlds.containsKey(id))
        {
            worldNameMapping.remove(worldNameMapping.get(id));
            worlds.remove(id);
        }
    }
}
