package keepcalm.mods.bukkit.asm.transformers.events;

import java.util.HashMap;
import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.avaje.ebean.enhance.asm.Opcodes;
import com.google.common.collect.Maps;

import cpw.mods.fml.relauncher.IClassTransformer;

public class BlockEventHelpers implements IClassTransformer {

	private HashMap<String,String> obfNames;
	private HashMap<String,String> mcpNames;
	
	private String itemStackTryPlaceDesc = "(L%s;L%s;IIIIFFF)Z";
	
	public BlockEventHelpers() {
		
		mcpNames = Maps.newHashMap();
		obfNames = Maps.newHashMap();
		
		mcpNames.put("itemStackClassName", "net.minecraft.item.ItemStack");
		obfNames.put("itemStackClassName", "um");
		
		mcpNames.put("itemStackTryPlace", "tryPlaceItemIntoWorld");
		obfNames.put("itemStackTryPlace", "a");
		
		mcpNames.put("worldClassName", "net.minecraft.world.World");
		obfNames.put("worldClassName", "xv");
		
		mcpNames.put("entityPlayerClassName", "net.minecraft.entity.player.EntityPlayer");
		obfNames.put("entityPlayerClassName", "qx");
		
	}
	
	@Override
	public byte[] transform(String name, byte[] bytes) {
		if (name.equalsIgnoreCase(mcpNames.get("itemStackClassName"))) {
			itemStackTryPlaceDesc = String.format(itemStackTryPlaceDesc, new Object[] { mcpNames.get("entityPlayerClassName").replace('.', '/'), mcpNames.get("worldClassName").replace('.', '/') });
			transformItemStack(bytes, mcpNames);
		}
		else if (name.equalsIgnoreCase(obfNames.get("itemStackClassName"))) {
			itemStackTryPlaceDesc = String.format(itemStackTryPlaceDesc, new Object[] { obfNames.get("entityPlayerClassName"), obfNames.get("worldClassName") });
			transformItemStack(bytes, obfNames);
		}
		
		return bytes;
	}
	
	private byte[] transformItemStack(byte[] bytes, HashMap<String,String> names) {
		ClassNode cn = new ClassNode();
		ClassReader cr = new ClassReader(bytes);
		cr.accept(cn, 0);
		System.out.println("Using desc for tryPlaceInWorld: " + itemStackTryPlaceDesc);
		Iterator<MethodNode> methods = cn.methods.iterator();
		int index = 0;
		while (methods.hasNext()) {
			MethodNode m = methods.next();
			//System.out.println("Method: " + m.name + " Desc: " + m.desc + "(wanted desc: " + itemStackTryPlaceDesc);
			if (m.name.equals(names.get("itemStackTryPlace")) && m.desc.equals(itemStackTryPlaceDesc)) {
				System.out.println("Found target method: " + m.name + m.desc + "! Finding the last instruction!");
				
				Iterator<AbstractInsnNode> insns = m.instructions.iterator();
				
				while (insns.hasNext()) {
					AbstractInsnNode instr = insns.next();
					
					System.out.println("Processing INSN at " + index +
                     " of type " + m.instructions.get(index).getType() +
                     ", OpCode " + m.instructions.get(index).getOpcode());
					index++;
					// return integer (or boolean), load integer (or boolean)
					if (instr.getOpcode() == Opcodes.IRETURN && instr.getPrevious().getOpcode() == Opcodes.ILOAD) {
						System.out.println("Found IRETURN after ILOAD, inserting code before...");
						index--;
						InsnList toInject = new InsnList();
						toInject.add(new VarInsnNode(Opcodes.ALOAD, 0));
						toInject.add(new VarInsnNode(Opcodes.ALOAD, 1));
						toInject.add(new VarInsnNode(Opcodes.ALOAD, 2));
						toInject.add(new VarInsnNode(Opcodes.ILOAD, 3));
						toInject.add(new VarInsnNode(Opcodes.ILOAD, 4));
						toInject.add(new VarInsnNode(Opcodes.ILOAD, 5));
						toInject.add(new VarInsnNode(Opcodes.ILOAD, 6));
						toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "keepcalm.mods.bukkit.ForgeEventHelper", "onItemUse", 
								"(L" + names.get("itemStackClassName").replace('.', '/') + ";L" + names.get("entityPlayerClassName").replace('.', '/') + 
								";L" + names.get("worldClassName").replace('.', '/') + ";IIII)V"));
						m.instructions.insertBefore(m.instructions.get(index), toInject);
						System.out.println("Finished patching ItemStack!");
					}
				}
			}
			
		}
		
		
		return bytes;
	}

}
