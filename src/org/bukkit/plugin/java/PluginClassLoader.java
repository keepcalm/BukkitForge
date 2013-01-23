package org.bukkit.plugin.java;

import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.RemappingClassAdapter;

import com.google.common.io.ByteStreams;

/**
 * A ClassLoader for plugins, to allow shared classes across multiple plugins
 */
public class PluginClassLoader extends URLClassLoader {
    //private String nbtTest = "cd";
    private final JavaPluginLoader loader;
    private final Map<String, Class<?>> classes = new HashMap<String, Class<?>>();

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
        if (name.startsWith("org.bukkit.") || name.startsWith("net.minecraft.")) {
            throw new ClassNotFoundException(name);
        }
        Class<?> result = classes.get(name);

        if (result == null) {
            if (checkGlobal) {
                result = loader.getClassByName(name);
            }

            if (result == null) {
                // MCPC+ start - custom loader
                //result = super.findClass(name);

                try {
                    // Load the resource to the name
                    String path = name.replace('.', '/').concat(".class");
                    URL url = this.findResource(path);
                    if (url != null) {
                        InputStream stream = url.openStream();
                        if (stream != null) {
                            byte[] bytecode = ByteStreams.toByteArray(stream);

                            // Remap the classes
                            ClassReader classReader = new ClassReader(bytecode);
                            ClassWriter classWriter = new ClassWriter(classReader, 0);
                            classReader.accept(new RemappingClassAdapter(classWriter, new PluginClassRemapper()), ClassReader.EXPAND_FRAMES);
                            byte[] remappedBytecode = classWriter.toByteArray();

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
}