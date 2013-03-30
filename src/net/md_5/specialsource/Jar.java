/**
 * Copyright (c) 2012-2013, md_5. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * The name of the author may not be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.md_5.specialsource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import com.google.common.base.Joiner;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

/**
 * This class wraps one or more {@link JarFile}s enabling quick access to the jar's main
 * class, as well as the ability to get the {@link InputStream} of a class file,
 * and speedy lookups to see if the jar contains the specified class.
 */
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Jar {

    private final List<JarFile> jarFiles;
    private final String main;
    private final String filename;
    private final LinkedHashMap<String, JarFile> jarForResource;
    private Set<String> contains = new HashSet<String>();
    private Map<String, ClassNode> classes = new HashMap<String, ClassNode>();

    public boolean containsClass(String clazz) {
        return contains.contains(clazz) ? true : getClass(clazz) != null;
    }

    public InputStream getResource(String name) throws IOException {
        JarFile jarFile = jarForResource.get(name);
        if (jarFile == null) {
            return null;
        }

        ZipEntry e = jarFile.getEntry(name);

        return e == null ? null : jarFile.getInputStream(e);
    }

    public InputStream getClass(String clazz) {
        try {
            InputStream inputStream = getResource(clazz + ".class");

            if (inputStream != null) {
                contains.add(clazz);
            }
            return inputStream;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public ClassNode getNode(String clazz) {
        try {
            ClassNode cache = classes.get(clazz);
            if (cache != null) {
                return cache;
            }
            InputStream is = getClass(clazz);
            if (is != null) {
                ClassReader cr = new ClassReader(getClass(clazz));
                ClassNode node = new ClassNode();
                cr.accept(node, 0);
                classes.put(clazz, node);
                return node;
            } else {
                return null;
            }
        } catch (IOException ex) {
            System.out.println(clazz);
            throw new RuntimeException(ex);
        }
    }

    public String getMain() {
        return main;
    }

    public String getFilename() {
        return filename;
    }

    /**
     * Get all filenames in the jar (preserves archive order)
     */
    public Set<String> getEntryNames() {
        return jarForResource.keySet(); // yes, LinkedHashMap keySet() is ordered even though Set normally is not!
    }

    private static LinkedHashMap<String, JarFile> collectJarFiles(List<JarFile> jarFiles) {
        LinkedHashMap<String, JarFile> jarForResource = new LinkedHashMap<String, JarFile>(); // ordered

        // map resource filename to jar file it is within
        for (JarFile jarFile : jarFiles) {
            for (Enumeration<JarEntry> entr = jarFile.entries(); entr.hasMoreElements();) {
                JarEntry entry = entr.nextElement();
                String name = entry.getName();

                /*
                if (jarForResource.containsKey(name)) {
                    System.out.println("INFO: overwriting "+entry.getName()+" from "+jarForResource.get(name).getName()+" with "+jarFile.getName());
                }
                */

                jarForResource.put(name, jarFile);
            }
            // continue through each jar file, overwriting subsequent classes in multiple jars ("jar mods")
        }

        return jarForResource;
    }

    private static String getMainClassName(Manifest manifest) {
        if (manifest != null) {
            Attributes attributes = manifest.getMainAttributes();
            if (attributes != null) {
                String mainClassName = attributes.getValue("Main-Class");
                if (mainClassName != null) {
                    return mainClassName.replace('.', '/');
                }
            }
        }

        return null;
    }

    public static Jar init(String jar) throws IOException {
        File file = new File(jar);
        return init(file);
      }

      public static Jar init(File file) throws IOException {
        List files = new ArrayList();

        files.add(file);

        return init(files);
      }

      public static Jar init(List<File> files) throws IOException {
        if (files.size() == 0) {
          throw new IllegalArgumentException("Jar init requires at least one file");
        }

        List jarFiles = new ArrayList(files.size());
        List filenames = new ArrayList();

        for (File file : files) {
          filenames.add(file.getName());
          jarFiles.add(new JarFile(file));
        }

        LinkedHashMap jarForResource = collectJarFiles(jarFiles);

        String filename = Joiner.on(" + ").join(filenames);
        String main = getMainClassName(((JarFile)jarFiles.get(0)).getManifest());

        return new Jar(jarFiles, main, filename, jarForResource);
      }

      public String toString()
      {
        return "Jar(jarFiles=" + this.jarFiles + ", main=" + getMain() + ", filename=" + getFilename() + ", jarForResource=" + this.jarForResource + ", contains=" + this.contains + ", classes=" + this.classes + ")";
      }

      public boolean equals(Object o)
      {
        if (o == this)
          return true;
        if (!(o instanceof Jar))
          return false;
        Jar other = (Jar)o;
        if (!other.canEqual(this))
          return false;
        Object this$jarFiles = this.jarFiles;
        Object other$jarFiles = other.jarFiles;
        if (this$jarFiles == null ? other$jarFiles != null : !this$jarFiles.equals(other$jarFiles))
          return false;
        Object this$main = getMain();
        Object other$main = other.getMain();
        if (this$main == null ? other$main != null : !this$main.equals(other$main))
          return false;
        Object this$filename = getFilename();
        Object other$filename = other.getFilename();
        if (this$filename == null ? other$filename != null : !this$filename.equals(other$filename))
          return false;
        Object this$jarForResource = this.jarForResource;
        Object other$jarForResource = other.jarForResource;
        if (this$jarForResource == null ? other$jarForResource != null : !this$jarForResource.equals(other$jarForResource))
          return false;
        Object this$contains = this.contains;
        Object other$contains = other.contains;
        if (this$contains == null ? other$contains != null : !this$contains.equals(other$contains))
          return false;
        Object this$classes = this.classes;
        Object other$classes = other.classes;
        return this$classes == null ? other$classes == null : this$classes.equals(other$classes);
      }

      public boolean canEqual(Object other)
      {
        return other instanceof Jar;
      }

      public int hashCode()
      {
        int PRIME = 31;
        int result = 1;
        Object $jarFiles = this.jarFiles;
        result = result * 31 + ($jarFiles == null ? 0 : $jarFiles.hashCode());
        Object $main = getMain();
        result = result * 31 + ($main == null ? 0 : $main.hashCode());
        Object $filename = getFilename();
        result = result * 31 + ($filename == null ? 0 : $filename.hashCode());
        Object $jarForResource = this.jarForResource;
        result = result * 31 + ($jarForResource == null ? 0 : $jarForResource.hashCode());
        Object $contains = this.contains;
        result = result * 31 + ($contains == null ? 0 : $contains.hashCode());
        Object $classes = this.classes;
        result = result * 31 + ($classes == null ? 0 : $classes.hashCode());
        return result;
      }

      private Jar(List<JarFile> jarFiles, String main, String filename, LinkedHashMap<String, JarFile> jarForResource)
      {
        this.contains = new HashSet();
        this.classes = new HashMap(); this.jarFiles = jarFiles; this.main = main; this.filename = filename; this.jarForResource = jarForResource;
      }
}
