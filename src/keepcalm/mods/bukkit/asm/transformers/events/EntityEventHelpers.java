package keepcalm.mods.bukkit.asm.transformers.events;

import java.util.HashMap;
import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import cpw.mods.fml.relauncher.IClassTransformer;

public class EntityEventHelpers implements IClassTransformer {

	private final HashMap<String,String> names;
	
	
	public EntityEventHelpers() {
		names = ObfuscationHelper.getRelevantMappings();
	}
	
	@Override
	public byte[] transform(String name, byte[] bytes) {
		
		if (name.equalsIgnoreCase(names.get("entitySheep_className"))) {
			transformEntitySheep(bytes);
		}
		
		return bytes;
	}
	
	private byte[] transformNetServerHandler(byte[] bytes) {
		ClassNode cn = new ClassNode();
		ClassReader cr = new ClassReader(bytes);
		cr.accept(cn, 0);
		
		// TODO :P
		
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		cn.accept(cw);
		return cw.toByteArray();
		
	}
	
	private byte[] transformEntitySheep(byte[] bytes) {
		System.out.println("Transforming EntitySheep...");
		ClassNode cn = new ClassNode();
		ClassReader cr = new ClassReader(bytes);
		cr.accept(cn, 0);
		
		Iterator<MethodNode> methods = cn.methods.iterator();
		while (methods.hasNext()) {
			MethodNode m = methods.next();
			
			if (m.name.equals(names.get("entitySheep_setColour_func")) && m.desc.equals(names.get("entitySheep_setColour_desc"))) {
				System.out.println("Found target method: " + m.name + m.desc + "! Inserting call...");
				
				for (int idx = 0; idx < m.instructions.size(); idx++) {
					if (m.instructions.get(idx).getOpcode() == Opcodes.ISTORE) {
						System.out.println("Found ISTORE at index " + idx + ", inserting code afterwards...");
						idx++; // AFTERwards, not before ;)
						
						InsnList toAdd = new InsnList();
						
						// to mark the end of the code
						LabelNode lmmnode = new LabelNode(new Label());
						// load this
						toAdd.add(new VarInsnNode(Opcodes.ALOAD, 0));
						// new fleece colour
						toAdd.add(new VarInsnNode(Opcodes.ILOAD, 1));
						// old fleece colour
						toAdd.add(new VarInsnNode(Opcodes.ILOAD, 2));
						toAdd.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "keepcalm/mods/bukkit/ForgeEventHelper", "onSheepDye", "(L" + names.get("entitySheep_javaName") + ";II)Z"));
						LabelNode endIf = new LabelNode(new Label());
						toAdd.add(new JumpInsnNode(Opcodes.IFEQ, endIf));
						toAdd.add(new InsnNode(Opcodes.RETURN));
						toAdd.add(endIf);
						toAdd.add(lmmnode);
						
						m.instructions.insertBefore(m.instructions.get(idx), toAdd);
						break;
					}
				}
			}
		}
		
		
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		cn.accept(cw);
		return cw.toByteArray();
	}

}
