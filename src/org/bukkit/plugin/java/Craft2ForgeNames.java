package org.bukkit.plugin.java;

public enum Craft2ForgeNames {
	EntityHuman("net.minecraft.src.EntityPlayer");
	
	
	private String forgeEquiv;
	
	private Craft2ForgeNames(String forgeEquiv) {
		this.forgeEquiv = forgeEquiv;
	}
	
	public static Craft2ForgeNames getClass(Class<?> clazz) {
		if (clazz.getCanonicalName().contains("net.minecraft.server")) {
			String targ = clazz.getCanonicalName().split(".")[-1];
			
			if (targ.toLowerCase() == "entityhuman") {
				return EntityHuman;
			}
			// todo
			//else if (targ.toLowerCase() == )
		}
		
		return null;
	}
}
