package org.bukkit.plugin.java;

import org.objectweb.asm.commons.Remapper;

import java.util.HashMap;

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
        if (typeName.startsWith("org.bukkit.craftbukkit")) {
        	String newName = typeName.replace("org.bukkit.craftbukkit", "keepcalm.mods.bukkit.bukkitAPI");
        	newName = newName.replace("Craft", "Bukkit");
        	newName = newName.replace("Bukkiting", "Crafting");
        	return newName;
        }
        
        return typeName;
    }
}
