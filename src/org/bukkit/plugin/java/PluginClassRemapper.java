package org.bukkit.plugin.java;

import java.util.HashMap;

import keepcalm.mods.bukkit.BukkitContainer;

import org.objectweb.asm.commons.Remapper;

/**
 * Remap classes in Bukkit plugins for compatibility purposes
 */
public class PluginClassRemapper extends Remapper {
    private static final HashMap<String, String> packageRemap = new HashMap<String, String>();

    static {
        // Guava 10 is part of the Bukkit API, so plugins can use it, but FML includes Guava 12
        // To resolve this conflict, remap plugin usages to Guava 10 in a separate package
        packageRemap.put("com/google", "guava10/com/google");

        // TODO: remap to vcb2obf, too
    }

    @Override
    public String map(String typeName) {
        for (String inPackage : packageRemap.keySet()) {
            if (typeName.startsWith(inPackage)) {
                String newName = packageRemap.get(inPackage) + typeName.substring(inPackage.length());
                System.out.println("plugin remap class "+typeName+" -> "+newName);

                return newName;
            }
        }
        // no plugins have support for BForge, so they're doing something weird if it starts with our packages
        if (typeName.startsWith("org.bukkit.craftbukkit") || typeName.startsWith("keepcalm.mods.bukkit.bukkitAPI")) {
        	String newName = typeName.replace("org.bukkit.craftbukkit", "keepcalm.mods.bukkit.bukkitAPI");
        	newName = newName.replace("Craft", "Bukkit");
        	newName = newName.replace("Bukkiting", "Crafting");
        	if (BukkitContainer.DEBUG)
        		System.out.println("Plugin OBC access attempt diverted: " + typeName + " => " + newName);
        	return newName;
        }
        
        return typeName;
    }
}
