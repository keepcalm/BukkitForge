package keepcalm.mods.bukkit.asm.transformers.events;

import com.google.common.collect.Maps;

import java.util.HashMap;

public class ObfuscationHelper {
	private static final HashMap<String,String> mcpNames = Maps.newHashMap();
	private static final HashMap<String,String> obfNames = Maps.newHashMap();
	
	static {
		mcpNames.put("itemStackClassName", "net.minecraft.item.ItemStack");
		obfNames.put("itemStackClassName", "ur");

		mcpNames.put("itemStackTryPlace", "tryPlaceItemIntoWorld");
		obfNames.put("itemStackTryPlace", "a");

		mcpNames.put("worldClassName", "net.minecraft.world.World");
		obfNames.put("worldClassName", "yc");

		mcpNames.put("entityPlayerClassName", "net.minecraft.entity.player.EntityPlayer");
		obfNames.put("entityPlayerClassName", "qx");

		mcpNames.put("entityPlayerJavaName",  "net/minecraft/entity/player/EntityPlayer");
		obfNames.put("entityPlayerJavaName",  "qx");

		mcpNames.put("worldJavaName", "net/minecraft/world/World");
		obfNames.put("worldJavaName", "yc");

		mcpNames.put("itemStackJavaName", "net/minecraft/item/ItemStack");
		obfNames.put("itemStackJavaName", "ur");

		// item in world manager stuff
		mcpNames.put("itemInWorldManagerClassName", "net.minecraft.item.ItemInWorldManager");
		obfNames.put("itemInWorldManagerClassName", "ir");

		mcpNames.put("itemInWorldManagerJavaName", "net/minecraft/item/ItemInWorldManager");
		obfNames.put("itemInWorldManagerJavaName", "ir");

		mcpNames.put("IIWMTargFunc", "updateBlockRemoving");
		obfNames.put("IIWMTargFunc", "a");

		// blockDispenser stuff

		mcpNames.put("blockDispenserClassName", "net.minecraft.block.BlockDispenser");
		obfNames.put("blockDispenserClassName", "ajw");

		mcpNames.put("blockDispenserJavaName", "net/minecraft/block/BlockDispenser");
		obfNames.put("blockDispenserJavaName", "ajw");

		mcpNames.put("dispenserDispenseFuncName", "dispense");
		obfNames.put("dispenserDispenseFuncName", "n");

		mcpNames.put("dispenserDispenseDesc", "(Lnet/minecraft/world/World;III)V");
		obfNames.put("dispenserDispenseDesc", "(Lyc;III)V");
		
		// entitySheep stuff
		mcpNames.put("entitySheepClassName", "net.minecraft.entity.passive.EntitySheep");
		obfNames.put("entitySheepClassName", "pe");
		
		mcpNames.put("entitySheepJavaName",  "net/minecraft/entity/passive/EntitySheep");
		obfNames.put("entitySheepJavaName",  "pe");
		
		mcpNames.put("entitySheepSetColour", "setFleeceColor");
		obfNames.put("entitySheepSetColour", "s");
		
		//blockFire stuff
		mcpNames.put("blockFireClassName", "net.minecraft.block.BlockFire");
		obfNames.put("blockFireClassName", "akf");
		
		mcpNames.put("blockFireTargDesc", "(Lnet/minecraft/world/World;IIIILjava/util/Random;ILnet/minecraftforge/common/ForgeDirection;)V");
		obfNames.put("blockFireTargDesc", "Lyc;IIIILjava/util/Random;ILnet/minecraftforge/common/ForgeDirection;)V");
		
		mcpNames.put("blockFireTargName", "tryToCatchBlockOnFire");
		obfNames.put("blockFireTargName", "a");
		
		// block stuff
		mcpNames.put("blockClassName", "net.minecraft.block.Block");
		obfNames.put("blockClassName", "amq");
		
		mcpNames.put("blockBreakBlock", "breakBlock");
		obfNames.put("blockBreakBlock", "a");
		
		mcpNames.put("blockBreakBlockDesc", "(Lnet/minecraft/world/World;IIIII)V");
		obfNames.put("blockBreakBlockDesc", "(Lyc;IIIII)V");
		
		// item stuff
		mcpNames.put("itemClassName", "net.minecraft.item.Item");
		obfNames.put("itemClassName", "up");
		
		// netserverhandler stuff
		mcpNames.put("netServerHandler", "net.minecraft.network.NetServerHandler");
		obfNames.put("netServerHandler", "iv");
		

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
