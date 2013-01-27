package org.bukkit.plugin.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.md_5.specialsource.IInheritanceProvider;
import net.md_5.specialsource.JarMapping;
import net.md_5.specialsource.JarRemapper;
import net.md_5.specialsource.RemappedRuntimeInheritanceProvider;
import net.md_5.specialsource.RuntimeInheritanceProvider;
import net.md_5.specialsource.ShadeRelocationSimulator;
import net.md_5.specialsource.URLClassLoaderInheritanceProvider;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.plugin.PluginDescriptionFile;

/**
 * A ClassLoader for plugins, to allow shared classes across multiple plugins
 */
public class PluginClassLoader extends URLClassLoader {
    private String nbtTest = "cd";
    private final JavaPluginLoader loader;
    private final Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
    // MCPC+ start
    JarRemapper remapper;   // class remapper for this plugin
    // MCPC+ end

    public PluginClassLoader(final JavaPluginLoader loader, final URL[] urls, final ClassLoader parent, PluginDescriptionFile pluginDescriptionFile) { // MCPC+ - add PluginDescriptionFile
        super(urls, parent);

        this.loader = loader;

        // MCPC+ start

        String pluginName = pluginDescriptionFile.getName();

        // configure default remapper settings
        YamlConfiguration config = ((CraftServer)Bukkit.getServer()).bukkitConfig;
        boolean useCustomClassLoader = config.getBoolean("bukkitforge.plugin-settings.default.custom-class-loader", true);
        boolean useGuava10 = config.getBoolean("bukkitforge.plugin-settings.default.use-guava10", true);
        boolean remapNMS147 = config.getBoolean("bukkitforge.plugin-settings.default.remap-nms-v1_4_R1", true);
        boolean remapNMS146 = config.getBoolean("bukkitforge.plugin-settings.default.remap-nms-v1_4_6", true);
        boolean remapOBC146 = config.getBoolean("bukkitforge.plugin-settings.default.remap-obc-v1_4_6", true);
        boolean remapOBC147 = config.getBoolean("bukkitforge.plugin-settings.default.remap-obc-v1_4_7", true);
        // plugin-specific overrides
        useCustomClassLoader = config.getBoolean("bukkitforge.plugin-settings."+pluginName+".custom-class-loader", useCustomClassLoader);
        useGuava10 = config.getBoolean("bukkitforge.plugin-settings."+pluginName+".use-guava10", true);
        remapNMS147 = config.getBoolean("bukkitforge.plugin-settings."+pluginName+".remap-nms-v1_4_R1", remapNMS147);
        remapNMS146 = config.getBoolean("bukkitforge.plugin-settings."+pluginName+".remap-nms-v1_4_6", remapNMS146);
        remapOBC146 = config.getBoolean("bukkitforge.plugin-settings."+pluginName+".remap-obc-v1_4_6", remapOBC146);
        remapOBC147 = config.getBoolean("bukkitforge.plugin-settings."+pluginName+".remap-obc-v1_4_7", remapOBC147);
        

        if (!useCustomClassLoader) {
            remapper = null;
            return;
        }

        try {
            JarMapping jarMapping = new JarMapping();

            if (useGuava10) {
                // Guava 10 is part of the Bukkit API, so plugins can use it, but FML includes Guava 12
                // To resolve this conflict, remap plugin usages to Guava 10 in a separate package
                // Most plugins should keep this enabled, unless they want a newer Guava
                jarMapping.packages.put("com/google/common", "guava10/com/google/common");
            }

            if (remapNMS147) {
                Map<String, String> relocations147 = new HashMap<String, String>();
                // mc-dev jar to CB, apply version shading (aka plugin safeguard) over cb2obf
                relocations147.put("net.minecraft.server", "net.minecraft.server.v1_4_R1");
                relocations147.put("org.bouncycastle", "net.minecraft.v1_4_R1.org.bouncycastle");
                ShadeRelocationSimulator shader = new ShadeRelocationSimulator(relocations147);

                jarMapping.loadMappings(
                        new BufferedReader(new InputStreamReader(loader.getClass().getClassLoader().getResourceAsStream("147cb2obf.csrg"))),
                        new ShadeRelocationSimulator(relocations147));
            }

            if (remapNMS146) {
                Map<String, String> relocations146 = new HashMap<String, String>();
                relocations146.put("net.minecraft.server", "net.minecraft.server.v1_4_6");
                relocations146.put("org.bouncycastle", "net.minecraft.v1_4_6.org.bouncycastle");

                jarMapping.loadMappings(
                        new BufferedReader(new InputStreamReader(loader.getClass().getClassLoader().getResourceAsStream("146cb2obf.csrg"))),
                        new ShadeRelocationSimulator(relocations146));
            }

            if (remapOBC146) {
                // Remap OBC v1_4_6  to v1_4_R1 (or current) for 1.4.6 plugin compatibility
                // Note this should only be mapped statically - since plugins MAY use reflection to determine the OBC version
            	jarMapping.loadMappings(new BufferedReader(new InputStreamReader(loader.getClass().getClassLoader().getResourceAsStream("146cb2bf.csrg"))), new ShadeRelocationSimulator(new HashMap<String, String>()));
                //jarMapping.packages.put("org/bukkit/craftbukkit/v1_4_6", "keepcalm/mods/bukkit/bukkitAPI");
            }
            
            if (remapOBC147) {
            	jarMapping.loadMappings(new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("147cb2bf.csrg"))), new ShadeRelocationSimulator(new HashMap<String, String>()));
            }
            
            //System.out.println("Loaded "+ jarMapping.classes.size()+" classes, "+ jarMapping.fields.size()+" fields, "+ jarMapping.methods.size()+" methods");

            // Inheritance chain lookup
            List<IInheritanceProvider> inheritanceProviderList = new ArrayList<IInheritanceProvider>();

            inheritanceProviderList.add(new URLClassLoaderInheritanceProvider(this)); // plugin jar
            inheritanceProviderList.add(new RemappedRuntimeInheritanceProvider(jarMapping)); // obfuscated NMS
            inheritanceProviderList.add(new RuntimeInheritanceProvider()); // everything else

            remapper = new JarRemapper(jarMapping, inheritanceProviderList);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        // MCPC+ end
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
        if (name.startsWith("org.bukkit.") || name.startsWith("net.minecraft.")) {
            throw new ClassNotFoundException(name);
        }
        Class<?> result = classes.get(name);

        if (result == null) {
            if (checkGlobal) {
                result = loader.getClassByName(name);
            }

            if (result == null) {
                // MCPC+ start - custom loader, if enabled
                if (remapper == null) {
                    result = super.findClass(name);
                } else {
                    result = remappedFindClass(name);
                }
                // MCPC+ end

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

    // MCPC+ start
    private Class<?> remappedFindClass(String name) {
        Class<?> result = null;

        try {
            // Load the resource to the name
            String path = name.replace('.', '/').concat(".class");
            URL url = this.findResource(path);
            if (url != null) {
                InputStream stream = url.openStream();
                if (stream != null) {
                    // Remap the classes
                            /*
                            ClassReader classReader = new ClassReader(bytecode);
                            ClassWriter classWriter = new ClassWriter(classReader, 0);
                            classReader.accept(new RemappingClassAdapter(classWriter, new PluginClassRemapper()), ClassReader.EXPAND_FRAMES);
                            byte[] remappedBytecode = classWriter.toByteArray();
                            */
                    byte[] remappedBytecode = remapper.remapClassFile(stream);

                    // Define (create) the class using the modified byte code
                    // The top-child class loader is used for this to prevent access violations
                    CodeSource codeSource = new CodeSource(url, new CodeSigner[0]);
                    result = this.defineClass(name, remappedBytecode, 0, remappedBytecode.length, codeSource);
                    if (result != null) {
                        // Resolve it - sets the class loader of the class
                        this.resolveClass(result);
                    }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return result;
    }
    // MCPC+ end
}