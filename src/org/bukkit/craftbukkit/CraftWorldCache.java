package org.bukkit.craftbukkit;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import org.bukkit.World;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.Map.Entry;

public class CraftWorldCache
{

    private HashMap<Integer,CraftWorld> worlds = new HashMap<Integer,CraftWorld>();
    private HashMap<String,Integer> worldNameMapping = Maps.newHashMap();
    
    private int recursiveGetCount = 0;
    
    public CraftWorld get(String name)
    {
        if(worldNameMapping.containsKey(safeName(name)))
            return get(worldNameMapping.get(safeName(name)));
        return null;
    }

    public CraftWorld get(UUID id)
    {
        for( World cw : getWorldsAsList())
        {
            if( cw.getUID().equals(id) )
            {
                return (CraftWorld)cw;
            }
        }
        return null;
    }

    public CraftWorld get(int dim)
    {
        cacheIfNotPresent(dim);
        return tryGetWorld(dim);
    }

    public void cacheIfNotPresent(int dim) {
        if(!existsInCache(dim) && existsInDM(dim) /*&& worlds.get(dim) != null*/)
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
    
    private void cacheWorld(int dim) {
    	
    	cacheWorld(dim, MinecraftServer.getServer().worldServerForDimension(dim));
    	
    }
    private String safeName(String name)
    {
        return name.toLowerCase().replace( ' ', '_');
    }

    private CraftWorld tryGetWorld(int dim) {
    	if (recursiveGetCount >= 5) {
    		return null;
    	}
    	if (worlds.get(dim) != null) {
    		this.recursiveGetCount = 0;
    		return worlds.get(dim);
    	}
		//GC'd!
		this.cacheWorld(dim);
		// try again
		recursiveGetCount++;
		return tryGetWorld(dim);
    }
    public List<World> getWorldsAsList() {
    	List<World> ret = Lists.newArrayList();
    	for (CraftWorld j : worlds.values()) {
    		ret.add(j);
    	}
    	return ret;
    }

    public void remove(int id) {
        if(worlds.containsKey(id))
        {
            worldNameMapping.remove(worldNameMapping.get(id));
            worlds.remove(id);
        }
    }
}
