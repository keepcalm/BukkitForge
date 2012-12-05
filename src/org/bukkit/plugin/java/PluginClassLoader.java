package org.bukkit.plugin.java;

import guava10.com.google.common.primitives.Bytes;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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
    	/*if (!name.contains("mchange"))
    	System.out.println("PluginClassLoader says HELLO, loading class " + name);
        if (name.startsWith("org.bukkit.") || name.startsWith("net.minecraft.")) {
            throw new ClassNotFoundException(name);
        }
        
        if (name.startsWith("com.google")) {
			System.out.println("Intercepting guava class: " + name);
			// guava
			
			
			if (guava10Classes .containsKey(name)) {
				return guava10Classes.get(name);
			}
			
			InputStream is = super.getResourceAsStream("/guava10/" + name.replace('.', '/'));
			BufferedInputStream bis = new BufferedInputStream(is);
			List<Byte> bytes = new ArrayList<Byte>();
			
			// room for error
			int last;
			try {
				while ((last = bis.read()) != -1) {
					bytes.add((byte) last);
				}
			} catch (IOException e) {
				return super.loadClass(name);
			}
			
			Class<?> clazz = this.defineClass(name, Bytes.toArray(bytes), 0, bytes.size());
			guava10Classes.put(name, clazz);
			return clazz;
			
		}*/
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
