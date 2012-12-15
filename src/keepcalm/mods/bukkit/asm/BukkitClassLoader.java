package keepcalm.mods.bukkit.asm;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.common.primitives.Bytes;

import cpw.mods.fml.relauncher.RelaunchClassLoader;

/**
 * Redirects plugin access from guava 12 to guava 10
 * abandoned for the moment
 * @author keepcalm
 *
 */
public class BukkitClassLoader extends RelaunchClassLoader {
	private ClassLoader realClassLoader;
	
	private Map<String,Class<?>> guava10Classes = new HashMap<String, Class<?>>();
	
	public BukkitClassLoader(URL[] urls, ClassLoader parent) {
		super(urls);
		// cpw's classloader
		realClassLoader = parent;
		
	}
	
	@Override
	@SuppressWarnings("all")
	public Class<?> findClass(String name) throws ClassNotFoundException {
		if (name.startsWith("com.google")) {
			System.out.println("Intercepting guava class: " + name);
			// guava
			boolean isMyClass = true;
			try {
				throw new ClassNotFoundException(name);
			}
			catch (ClassNotFoundException e) {
				List<StackTraceElement> examine = new ArrayList<StackTraceElement>();
				for (StackTraceElement x : e.getStackTrace()) {
					String cn = x.getClassName().toLowerCase(Locale.ENGLISH);
					//System.out.print(cn);
					if (!cn.contains("classloader") && !cn.contains("accesscontroller") && !cn.contains("reflect") && !cn.contains("java.lang") && !cn.contains("keepcalm") && !cn.contains("com.google") && !cn.contains("org.bukkit")) {
						//System.out.println(" doesn't contain ClassLoader or AccesController!");
						examine.add(x);
					}
					
					//System.out.println("Class: " + x.getClassName() + " Method: " + x.getMethodName());
				}
				System.out.println("Classes of intrest:");
				for (StackTraceElement j : examine) {
					System.out.println("Class: " + j.getClassName() + " Method: " + j.getMethodName());
					String cn = j.getClassName().toLowerCase();
					Class<?> clazz =  realClassLoader.loadClass(name);
					
					if (cn.contains("cpw") || cn.contains("forge") || cn.contains("net.minecraft")) {
						return clazz;
					}
				}
			}
			
			if (guava10Classes.containsKey(name)) {
				return guava10Classes.get(name);
			}
			
			InputStream is = realClassLoader.getResourceAsStream("/guava10/" + name.replace('.', '/'));
			BufferedInputStream bis = new BufferedInputStream(is);
			List<Byte> bytes = new ArrayList<Byte>();
			
			// room for error
			int last;
			try {
				while ((last = bis.read()) != -1) {
					bytes.add((byte) last);
				}
			} catch (IOException e) {
				return realClassLoader.loadClass(name);
			}
			
			Class<?> clazz = this.defineClass(name, Bytes.toArray(bytes), 0, bytes.size());
			guava10Classes.put(name, clazz);
			return clazz;
			
		}
			
			
		//System.out.println("Ignoring non-related class " + name);
		return realClassLoader.loadClass(name);
	}
	
	

}
