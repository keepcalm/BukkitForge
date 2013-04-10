package org.bukkit.plugin.java;

import org.bouncycastle.util.io.Streams;
import net.md_5.specialsource.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.*;

/**
 * A ClassLoader for plugins, to allow shared classes across multiple plugins
 */
public class PluginClassLoader extends URLClassLoader {
    private String nbtTest = "cd";
    private final JavaPluginLoader loader;
    private final Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
    // MCPC+ start
    private JarRemapper remapper;     // class remapper for this plugin, or null
    private RemapperPreprocessor remapperPreprocessor; // secondary; for inheritance & remapping reflection
    private boolean debug;            // classloader debugging

    private static HashMap<Integer,JarMapping> jarMappings = new HashMap<Integer, JarMapping>();
    private static final int F_USE_GUAVA10      = 1 << 1;
    private static final int F_GLOBAL_INHERIT   = 1 << 2;
    private static final int F_REMAP_OBCPRE     = 1 << 3;
    private static final int F_REMAP_NMS146     = 1 << 4;
    private static final int F_REMAP_OBC146     = 1 << 5;
    private static final int F_REMAP_NMS147     = 1 << 6;
    private static final int F_REMAP_NMS150     = 1 << 7;
    private static final int F_REMAP_NMS151     = 1 << 8;
    private static final int F_REMAP_OBC147     = 1 << 9;
    private static final int F_REMAP_OBC150     = 1 << 10;
    private static final int F_REMAP_NMSPRE_MASK= 0x0fff0000;  // "unversioned" NMS plugin version

    public final static String current = "v1_5_R2";

    // This trick bypasses Maven Shade's package rewriting when using String literals [same trick in jline]
    private final static String org_bukkit_craftbukkit = new String(new char[] {'o','r','g','/','b','u','k','k','i','t','/','c','r','a','f','t','b','u','k','k','i','t'});
    // MCPC+ end

    public PluginClassLoader(final JavaPluginLoader loader, final URL[] urls, final ClassLoader parent, PluginDescriptionFile pluginDescriptionFile) { // MCPC+ - add PluginDescriptionFile
        super(urls, parent);

        this.loader = loader;

        // MCPC+ start

        String pluginName = pluginDescriptionFile.getName();
        System.out.println(pluginName);

        // configure default remapper settings
        YamlConfiguration config = ((CraftServer)Bukkit.getServer()).configuration;
        boolean useCustomClassLoader = config.getBoolean("mcpc.plugin-settings.default.custom-class-loader", true);
        debug = config.getBoolean("mcpc.plugin-settings.default.debug", false);
        boolean useGuava10 = config.getBoolean("mcpc.plugin-settings.default.use-guava10", true);
        boolean remapNMS151 = config.getBoolean("mcpc.plugin-settings.default.remap-nms-v1_5_R2", true);
        boolean remapNMS150 = config.getBoolean("mcpc.plugin-settings.default.remap-nms-v1_5_R1", true);
        boolean remapNMS147 = config.getBoolean("mcpc.plugin-settings.default.remap-nms-v1_4_R1", true);
        boolean remapNMS146 = config.getBoolean("mcpc.plugin-settings.default.remap-nms-v1_4_6", true);
        String remapNMSPre = config.getString("mcpc.plugin-settings.default.remap-nms-pre", "false");
        boolean remapOBC150 = config.getBoolean("mcpc.plugin-settings.default.remap-obc-v1_5_R1", true);
        boolean remapOBC147 = config.getBoolean("mcpc.plugin-settings.default.remap-obc-v1_4_R1", false);
        boolean remapOBC146 = config.getBoolean("mcpc.plugin-settings.default.remap-obc-v1_4_6", false);
        boolean remapOBCPre = config.getBoolean("mcpc.plugin-settings.default.remap-obc-pre", false);
        boolean globalInherit = config.getBoolean("mcpc.plugin-settings.default.global-inheritance", true);
        boolean pluginInherit = config.getBoolean("mcpc.plugin-settings.default.plugin-inheritance", true);
        boolean reflectFields = config.getBoolean("mcpc.plugin-settings.default.remap-reflect-field", true);

        // plugin-specific overrides
        useCustomClassLoader = config.getBoolean("mcpc.plugin-settings."+pluginName+".custom-class-loader", useCustomClassLoader);
        debug = config.getBoolean("mcpc.plugin-settings."+pluginName+".debug", debug);
        useGuava10 = config.getBoolean("mcpc.plugin-settings."+pluginName+".use-guava10", useGuava10);
        remapNMS151 = config.getBoolean("mcpc.plugin-settings."+pluginName+".remap-nms-v1_5_R2", remapNMS151);
        remapNMS150 = config.getBoolean("mcpc.plugin-settings."+pluginName+".remap-nms-v1_5_R1", remapNMS150);
        remapNMS147 = config.getBoolean("mcpc.plugin-settings."+pluginName+".remap-nms-v1_4_R1", remapNMS147);
        remapNMS146 = config.getBoolean("mcpc.plugin-settings."+pluginName+".remap-nms-v1_4_6", remapNMS146);
        remapNMSPre = config.getString("mcpc.plugin-settings."+pluginName+".remap-nms-pre", remapNMSPre);
        remapOBC150 = config.getBoolean("mcpc.plugin-settings."+pluginName+".remap-obc-v1_5_R1", remapOBC150);
        remapOBC147 = config.getBoolean("mcpc.plugin-settings."+pluginName+".remap-obc-v1_4_R1", remapOBC147);
        remapOBC146 = config.getBoolean("mcpc.plugin-settings."+pluginName+".remap-obc-v1_4_6", remapOBC146);
        remapOBCPre = config.getBoolean("mcpc.plugin-settings."+pluginName+".remap-obc-pre", remapOBCPre);
        globalInherit = config.getBoolean("mcpc.plugin-settings."+pluginName+".global-inheritance", globalInherit);
        pluginInherit = config.getBoolean("mcpc.plugin-settings."+pluginName+".plugin-inheritance", pluginInherit);
        reflectFields = config.getBoolean("mcpc.plugin-settings."+pluginName+".remap-reflect-field", reflectFields);

        if (debug) {
            System.out.println("PluginClassLoader debugging enabled for "+pluginName);
        }

        if (!useCustomClassLoader) {
            remapper = null;
            return;
        }

        int flags = 0;
        if (useGuava10) flags |= F_USE_GUAVA10;
        if (remapNMS151) flags |= F_REMAP_NMS151;
        if (remapNMS150) flags |= F_REMAP_NMS150;
        if (remapNMS147) flags |= F_REMAP_NMS147;
        if (remapNMS146) flags |= F_REMAP_NMS146;
        if (!remapNMSPre.equals("false")) {
            if      (remapNMSPre.equals("1.5.1")) flags |= 0x01510000;
            else if (remapNMSPre.equals("1.5.0")) flags |= 0x01500000;
            else if (remapNMSPre.equals("1.5"))   flags |= 0x01500000;
            else if (remapNMSPre.equals("1.4.7")) flags |= 0x01470000;
            else if (remapNMSPre.equals("1.4.6")) flags |= 0x01460000;
            else if (remapNMSPre.equals("1.4.5")) flags |= 0x01450000;
            else if (remapNMSPre.equals("1.4.4")) flags |= 0x01440000;
            else if (remapNMSPre.equals("1.4.2")) flags |= 0x01420000;
            else if (remapNMSPre.equals("1.3.2")) flags |= 0x01320000;
            else if (remapNMSPre.equals("1.3.1")) flags |= 0x01310000;
            else if (remapNMSPre.equals("1.2.5")) flags |= 0x01250000;
            else {
                System.out.println("Unsupported nms-remap-pre version '"+remapNMSPre+"', disabling");
            }
        }
        if (remapOBC150) flags |= F_REMAP_OBC150;
        if (remapOBC147) flags |= F_REMAP_OBC147;
        if (remapOBC146) flags |= F_REMAP_OBC146;
        if (remapOBCPre) flags |= F_REMAP_OBCPRE;
        if (globalInherit) flags |= F_GLOBAL_INHERIT;

        JarMapping jarMapping = getJarMapping(flags);

        // Load inheritance map
        if ((flags & F_GLOBAL_INHERIT) != 0) {
            if (debug) {
                System.out.println("Enabling global inheritance remapping");
            }
            jarMapping.setInheritanceMap(loader.getGlobalInheritanceMap());
            jarMapping.setFallbackInheritanceProvider(new URLClassLoaderInheritanceProvider(this, debug));
        }

        remapper = new JarRemapper(jarMapping);

        if (pluginInherit || reflectFields) {
            remapperPreprocessor = new RemapperPreprocessor(
                    pluginInherit ? loader.getGlobalInheritanceMap() : null,
                    reflectFields ? jarMapping : null);
            remapperPreprocessor.debug = debug;
        } else {
            remapperPreprocessor = null;
        }
    }

    /**
     * Load NMS mappings from CraftBukkit mc-dev to repackaged srgnames for FML runtime deobf
     *
     * @param jarMapping An existing JarMappings instance to load into
     * @param obfVersion CraftBukkit version with internal obfuscation counter identifier
     *                   >=1.4.7 this is the major version + R#. v1_4_R1=1.4.7, v1_5_R1=1.5, v1_5_R2=1.5.1..
     *                   For older versions (including pre-safeguard) it is the full Minecraft version number
     * @throws IOException
     */
    private void loadNmsMappings(JarMapping jarMapping, String obfVersion) throws IOException {
        Map<String, String> relocations = new HashMap<String, String>();
        // mc-dev jar to CB, apply version shading (aka plugin safeguard)
        relocations.put("net.minecraft.server", "net.minecraft.server." + obfVersion);

        jarMapping.loadMappings(
                new BufferedReader(new InputStreamReader(loader.getClass().getClassLoader().getResourceAsStream("mappings/"+obfVersion+"/cb2numpkg.srg"))),
                new ShadeRelocationSimulator(relocations),
                null, false);

        // resolve naming conflict in FML/CB
        jarMapping.methods.put("net/minecraft/server/"+obfVersion+"/PlayerConnection/getPlayer ()Lorg/bukkit/craftbukkit/entity/CraftPlayer;", "getPlayerB");

        // remap bouncycastle to Forge's included copy, not the vanilla obfuscated copy (not in MCPC+), see #133
        jarMapping.packages.put("net/minecraft/"+obfVersion+"/org/bouncycastle", "org/bouncycastle");
    }

    private JarMapping getJarMapping(int flags) {
        JarMapping jarMapping;

        if (jarMappings.containsKey(flags)) {
            if (debug) {
                System.out.println("Mapping reused for "+Integer.toHexString(flags));
            }
            return jarMappings.get(flags);
        }

        jarMapping = new JarMapping();
        try {

            if ((flags & F_USE_GUAVA10) != 0) {
                // Guava 10 is part of the Bukkit API, so plugins can use it, but FML includes Guava 12
                // To resolve this conflict, remap plugin usages to Guava 10 in a separate package
                // Most plugins should keep this enabled, unless they want a newer Guava
                jarMapping.packages.put("com/google/common", "guava10/com/google/common");
            }

            if ((flags & F_REMAP_NMS151) != 0) {
            	jarMapping.packages.put(org_bukkit_craftbukkit+"/v1_5_R2", org_bukkit_craftbukkit);
                //loadNmsMappings(jarMapping, "v1_5_R2");
            }

            if ((flags & F_REMAP_NMS150) != 0) {
            	jarMapping.packages.put(org_bukkit_craftbukkit+"/v1_5_R1", org_bukkit_craftbukkit);
                //loadNmsMappings(jarMapping, "v1_5_R1");
            }


            if ((flags & F_REMAP_NMS147) != 0) {
            	jarMapping.packages.put(org_bukkit_craftbukkit+"/v1_4_R1", org_bukkit_craftbukkit);
                //loadNmsMappings(jarMapping, "v1_4_R1");
            }

            if ((flags & F_REMAP_NMS146) != 0) {
            	jarMapping.packages.put(org_bukkit_craftbukkit+"/v1_4_6", org_bukkit_craftbukkit);
                //loadNmsMappings(jarMapping, "v1_4_6");
            }

            if ((flags & F_REMAP_OBC150) != 0) {
                jarMapping.packages.put(org_bukkit_craftbukkit+"/v1_5_R1", org_bukkit_craftbukkit);
            }

            if ((flags & F_REMAP_OBC147) != 0) {
                jarMapping.packages.put(org_bukkit_craftbukkit+"/v1_4_R1", org_bukkit_craftbukkit);
            }

            if ((flags & F_REMAP_OBC146) != 0) {
                // Remap OBC v1_4_6  to v1_4_R1 (or current) for 1.4.6 plugin compatibility
                // Note this should only be mapped statically - since plugins MAY use reflection to determine the OBC version
                jarMapping.packages.put(org_bukkit_craftbukkit+"/v1_4_6", org_bukkit_craftbukkit);
            }

            if ((flags & F_REMAP_OBCPRE) != 0) {
                // enabling unversioned obc not currently compatible with versioned obc plugins (overmapped) -
                // admins should enable remap-obc-pre on a per-plugin basis, as needed
                //jarMapping.packages.put(org_bukkit_craftbukkit+"/v1_4_R1", org_bukkit_craftbukkit+"/v1_4_R1");

                // then map unversioned to current version
                jarMapping.packages.put(org_bukkit_craftbukkit+"/libs/org/objectweb/asm", "org/objectweb/asm"); // ?
                jarMapping.packages.put(org_bukkit_craftbukkit, org_bukkit_craftbukkit);
            }

            if ((flags & F_REMAP_NMSPRE_MASK) != 0) {
                String filename;
                switch (flags & F_REMAP_NMSPRE_MASK)
                {
                    case 0x01510000: filename = "mappings/v1_5_R2/cb2numpkg.srg"; break;
                    case 0x01500000: filename = "mappings/v1_5_R1/cb2numpkg.srg"; break;
                    case 0x01470000: filename = "mappings/v1_4_R1/cb2numpkg.srg"; break;
                    case 0x01460000: filename = "mappings/v1_4_6/cb2numpkg.srg"; break;
                    case 0x01450000: filename = "mappings/v1_4_5/cb2numpkg.srg"; break;
                    case 0x01440000: filename = "mappings/v1_4_4/cb2numpkg.srg"; break;
                    case 0x01420000: filename = "mappings/v1_4_2/cb2numpkg.srg"; break;
                    case 0x01320000: filename = "mappings/v1_3_2/cb2numpkg.srg"; break;
                    case 0x01310000: filename = "mappings/v1_3_1/cb2numpkg.srg"; break;
                    case 0x01250000: filename = "mappings/v1_2_5/cb2numpkg.srg"; break;
                    default: throw new IllegalArgumentException("Invalid unversioned mapping flags: "+Integer.toHexString(flags & F_REMAP_NMSPRE_MASK)+" in "+Integer.toHexString(flags));
                }

                jarMapping.loadMappings(
                        new BufferedReader(new InputStreamReader(loader.getClass().getClassLoader().getResourceAsStream(filename))),
                        null, // no version relocation!
                        null, false);
            }

            System.out.println("Mapping loaded "+jarMapping.packages.size()+" packages, "+jarMapping.classes.size()+" classes, "+jarMapping.fields.size()+" fields, "+jarMapping.methods.size()+" methods, flags "+Integer.toHexString(flags));

            jarMappings.put(flags, jarMapping);
            return jarMapping;
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }
    // MCPC+ end

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
            if (debug) {
                System.out.println("Unexpected plugin findClass on OBC/NMS: name="+name+", checkGlobal="+checkGlobal+"; returning not found");
            }
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
    private Class<?> remappedFindClass(String name) throws ClassNotFoundException {
        Class<?> result = null;

        try {
            // Load the resource to the name
            String path = name.replace('.', '/').concat(".class");
            URL url = this.findResource(path);
            if (url != null) {
                InputStream stream = url.openStream();
                if (stream != null) {
                    byte[] bytecode = null;

                    // Reflection remap and inheritance extract
                    if (remapperPreprocessor != null) {
                        // add to inheritance map
                        bytecode = remapperPreprocessor.preprocess(stream);
                        if (bytecode == null) stream = url.openStream();
                    }

                    if (bytecode == null) {
                        bytecode = Streams.readAll(stream);
                    }

                    // Remap the classes
                    byte[] remappedBytecode = remapper.remapClassFile(bytecode);

                    if (debug) {
                        File file = new File("remapped-plugin-classes/"+name+".class");
                        file.getParentFile().mkdirs();
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream(file);
                            fileOutputStream.write(remappedBytecode);
                            fileOutputStream.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }

                    // Define (create) the class using the modified byte code
                    // The top-child class loader is used for this to prevent access violations
                    // Set the codesource to the jar, not within the jar, for compatibility with
                    // plugins that do new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()))
                    // instead of using getResourceAsStream - see https://github.com/MinecraftPortCentral/MCPC-Plus/issues/75
                    JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection(); // parses only
                    URL jarURL = jarURLConnection.getJarFileURL();
                    CodeSource codeSource = new CodeSource(jarURL, new CodeSigner[0]);

                    result = this.defineClass(name, remappedBytecode, 0, remappedBytecode.length, codeSource);
                    if (result != null) {
                        // Resolve it - sets the class loader of the class
                        this.resolveClass(result);
                    }
                }
            }
        } catch (Throwable t) {
            if (debug) {
                System.out.println("remappedFindClass("+name+") exception: "+t);
                t.printStackTrace();
            }
            throw new ClassNotFoundException("Failed to remap class "+name, t);
        }

        return result;
    }
    // MCPC+ end
}
