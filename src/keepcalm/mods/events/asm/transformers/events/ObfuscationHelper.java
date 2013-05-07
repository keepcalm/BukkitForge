package keepcalm.mods.events.asm.transformers.events;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;


import com.google.common.collect.Maps;

public class ObfuscationHelper {
	private static final HashMap<String,String> mcpNames = Maps.newHashMap();
	private static final HashMap<String,String> obfNames = Maps.newHashMap();
	
	private static final Properties obfProps = new Properties();
	private static final Properties mcpProps = new Properties();
	static {
		boolean isObf = ObfuscationHelper.class.getClassLoader().getSystemResourceAsStream("net/minecraft/src") == null;
		
		if (isObf) {
			InputStream obf = ObfuscationHelper.class.getClassLoader().getResourceAsStream("obf.properties");
			
			if ((obf == null )) {
				throw new RuntimeException("BukkitForge was packaged incorrectly.\n\n" +
						"Please report this at https://github.com/keepcalm/BukkitForge/issues, and add the two .properties files in" +
						" https://github.com/keepcalm/BukkitForge/tree/master/utils to your BukkitForge JAR.");
			}
			
			try {
				obfProps.load(obf);
			} catch (IOException e) {
					System.out.println("Ouch...");
					throw new RuntimeException("BukkitForge failed to load a mappings file.\n\n" + 
						"Please report this at https://github.com/keepcalm/BukkitForge/issues, and add the two .properties files in" +
						" https://github.com/keepcalm/BukkitForge/tree/master/utils to your BukkitForge JAR.", e);
			}
			
			for (String k : obfProps.keySet().toArray(new String[0])) {
				obfNames.put(k, obfProps.getProperty(k));
			}
		}
		else {
			InputStream mcp = ObfuscationHelper.class.getClassLoader().getResourceAsStream("mcp.properties");
			
			try {
				mcpProps.load(mcp);
			}
			catch (IOException e) {
					throw new RuntimeException("BukkitForge failed to load a mappings file.\n\n" +
							"Check that your development environment is setup correctly, and install the two .properties files in utils/ into your mcp/src/ folder.", e);
				// meh, nvm - unused
			}
			
			for (String k : mcpProps.keySet().toArray(new String[0])) {
				mcpNames.put(k, mcpProps.getProperty(k));
			}
		}

	}
	
	public static HashMap<String,String> getMCPMappings() {
		return mcpNames;
	}
	
	public static HashMap<String,String> getObfMappings() {
		return obfNames;
	}
	
	public static HashMap<String,String> getRelevantMappings() {
		if (ObfuscationHelper.class.getClassLoader().getSystemResourceAsStream("net/minecraft/src") == null) {
			return obfNames;
		}
		else {
			return mcpNames;
		}
	}
	
}
