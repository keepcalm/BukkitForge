package keepcalm.mods.bukkit.asm.transformers.events;

import java.util.HashMap;
import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
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
		
		mcpNames.put("entityPlayerJavaName", "net/minecraft/entity/player/EntityPlayer");
		obfNames.put("entityPlayerJavaName", "qx");
		
		mcpNames.put("worldJavaName", "net/minecraft/world/World");
		obfNames.put("worldJavaName", "xv");
		
		mcpNames.put("itemStackJavaName", "net/minecraft/item/ItemStack");
		obfNames.put("itemStackJavaName", "um");
		
	}
	
	@Override
	public byte[] transform(String name, byte[] bytes) {
		if (name.equalsIgnoreCase(mcpNames.get("itemStackClassName"))) {
			itemStackTryPlaceDesc = String.format(itemStackTryPlaceDesc, new Object[] { mcpNames.get("entityPlayerClassName").replace('.', '/'), mcpNames.get("worldClassName").replace('.', '/') });
			return transformItemStack(bytes, mcpNames);
		}
		else if (name.equalsIgnoreCase(obfNames.get("itemStackClassName"))) {
			itemStackTryPlaceDesc = String.format(itemStackTryPlaceDesc, new Object[] { obfNames.get("entityPlayerClassName"), obfNames.get("worldClassName") });
			return transformItemStack(bytes, obfNames);
		}
		
		
		
		return bytes;
	}
	
	private byte[] transformItemStack(byte[] bytes, HashMap<String,String> names) {
		ClassNode cn = new ClassNode();
		ClassReader cr = new ClassReader(bytes);
		cr.accept(cn, 0);
		System.out.println("Using desc for tryPlaceInWorld: " + itemStackTryPlaceDesc);
		int idx = 0;
		Iterator<MethodNode> methods = cn.methods.iterator();
		while (methods.hasNext()) {
			MethodNode m = methods.next();
			//System.out.println("Method: " + m.name + " Desc: " + m.desc + "(wanted desc: " + itemStackTryPlaceDesc);
			if (m.name.equals(names.get("itemStackTryPlace")) && m.desc.equals(itemStackTryPlaceDesc)) {
				System.out.println("Found target method: " + m.name + m.desc + "! Finding the last instruction!");
				
				boolean found = false;
				System.out.println("Processing " + m.instructions.size() + " instructions...");
				for (int index = 0; index < m.instructions.size(); index++) {
					AbstractInsnNode instr = m.instructions.get(index);
					
					//System.out.println("Processing INSN at " + index +
                    // " of type " + m.instructions.get(index).getType() +
                    // ", OpCode " + m.instructions.get(index).getOpcode());
					
					// return integer (or boolean), load integer (or boolean)
					if (instr.getOpcode() == Opcodes.IRETURN && instr.getPrevious().getOpcode() == Opcodes.ILOAD) {
						System.out.println("Found IRETURN after ILOAD, inserting code before...");
						index -= 1;
						InsnList toInject = new InsnList();
						/*toInject.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
						toInject.add(new VarInsnNode(Opcodes.ALOAD, 0));
						toInject.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/Object;)V"));
						toInject.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
						toInject.add(new VarInsnNode(Opcodes.ALOAD, 1));
						toInject.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/Object;)V"));*/
						LabelNode lmmnode = new LabelNode(new Label());
						// this
						toInject.add(new VarInsnNode(Opcodes.ALOAD, 0));
						// entityplayer
						toInject.add(new VarInsnNode(Opcodes.ALOAD, 1));
						// world
						toInject.add(new VarInsnNode(Opcodes.ALOAD, 2));
						// x y z direction
						toInject.add(new VarInsnNode(Opcodes.ILOAD, 3));
						toInject.add(new VarInsnNode(Opcodes.ILOAD, 4));
						toInject.add(new VarInsnNode(Opcodes.ILOAD, 5));
						toInject.add(new VarInsnNode(Opcodes.ILOAD, 6));
						toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "keepcalm/mods/bukkit/ForgeEventHelper", "onItemUse", String.format("(L%s;L%s;L%s;IIII)V", new Object[] {names.get("itemStackJavaName"), names.get("entityPlayerJavaName"), names.get("worldJavaName")})));
						toInject.add(lmmnode);
						m.instructions.insertBefore(m.instructions.get(index), toInject);
						System.out.println("Used desc: " + String.format("(L%s;L%s;L%s;IIII)V", new Object[] {names.get("itemStackJavaName"), names.get("entityPlayerJavaName"), names.get("worldJavaName")}));
						System.out.println("Finished patching ItemStack! - Inserted before " + index + ", toInject: " + toInject);
						break;
					}
				}
				
				idx++;
			}
			
		}
		
		
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cn.accept(writer);
        return writer.toByteArray();
	}

}
