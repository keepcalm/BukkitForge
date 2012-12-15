package org.bukkit.plugin.java;

//import guava10.com.google.common.primitives.Bytes;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import keepcalm.mods.bukkit.asm.BukkitContainer;

/**
 * A ClassLoader for plugins, to allow shared classes across multiple plugins
 */
public class PluginClassLoader extends URLClassLoader {
    private final JavaPluginLoader loader;
    private final Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
	private Map<String, Class<?>> guava10Classes = new HashMap<String, Class<?>>();

    public PluginClassLoader(final JavaPluginLoader loader, final URL[] urls, final ClassLoader parent) {
        super(urls, parent);

        this.loader = loader;
    }

    @Override
    public void addURL(URL url) { // Override for access level!
        super.addURL(url);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return findClass(name, true);
    }

    protected Class<?> findClass(String name, boolean checkGlobal) throws ClassNotFoundException {
    	if (name.startsWith("net.minecraft.server")) {
    		BukkitContainer.bukkitLogger.severe("A plugin is trying to access a minecraft class, this WILL crash the server. Please report this error message at github:");
    		throw new ClassNotFoundException(name);
    	}
        Class<?> result = classes.get(name);

        if (result == null) {
            if (checkGlobal) {
                result = loader.getClassByName(name);
            }

            if (result == null) {
                result = super.findClass(name);

                if (result != null) {
                    loader.setClass(name, result);
                }
            }

            classes.put(name, result);
        }

        return result;
    }

    public Set<String> getClasses() {
        return classes.keySet();
    }
}
