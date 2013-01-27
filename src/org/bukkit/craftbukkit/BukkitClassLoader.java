package org.bukkit.craftbukkit;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

public class BukkitClassLoader extends ClassLoader {
	
	@SuppressWarnings("rawtypes")
	private HashMap<String,Class> classes = new HashMap<String, Class>();
	
	public BukkitClassLoader(ClassLoader parent) {
		super(parent);
		System.out.println("Hello there, my parent is " + parent.getClass().getName());
	}
	
	@Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
         return findClass(className);
    }

    @Override
    public Class<?> findClass(String className) {
        System.out.println("Examining: " + className);
        byte classByte[];
        Class<?> result = null;
        result = (Class<?>) classes.get(className);
        if (result != null) {
            return result;
        }

        try {
            return findSystemClass(className);
        } catch (Exception e) {
        }
        try {
            String classPath = ((String) ClassLoader.getSystemResource(className.replace('.', File.separatorChar) + ".class").getFile()).substring(1);
            classByte = loadClassData(classPath);
            result = defineClass(className, classByte, 0, classByte.length, null);
            classes.put(className, result);
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    private byte[] loadClassData(String className) throws IOException {

        File f;
        f = new File(className);
        int size = (int) f.length();
        byte buff[] = new byte[size];
        FileInputStream fis = new FileInputStream(f);
        DataInputStream dis = new DataInputStream(fis);
        dis.readFully(buff);
        dis.close();
        return buff;
    }

}
