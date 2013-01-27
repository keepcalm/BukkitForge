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
        packageRemap.put("com/google/common", "guava10/com/google/common");

        // Remap OBC v1_4_6  to v1_4_R1 (or current) for 1.4.6 plugin compatibility
        // Note this should only be mapped statically - since plugins MAY use reflection to determine the OBC version
        packageRemap.put("org/bukkit/craftbukkit/v1_4_6", "org/bukkit/craftbukkit/v1_4_R1");

        // TODO: remap to vcb2obf, too
    }

    @Override
    public String map(String typeName) {
        for (String inPackage : packageRemap.keySet()) {
            if (typeName.startsWith(inPackage)) {
                String newName = packageRemap.get(inPackage) + typeName.substring(inPackage.length());

                return newName;
            }
        }
        return typeName;
    }
}
