package keepcalm.mods.events.asm.transformers.events;

import java.util.HashMap;
import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
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
			return transformEntitySheep(bytes);
		}
		else if (name.equalsIgnoreCase(names.get("netServerHandler_className"))) {
			return transformNetServerHandler(bytes);
		}
		else if (name.equalsIgnoreCase(names.get("entityCreeper_className"))) {
			System.out.println("Class name matched creeper...");
			return transformCreeper(bytes);
		}
		
		return bytes;
	}
	
	private byte[] transformNetServerHandler(byte[] bytes) {
		ClassNode cn = new ClassNode();
		ClassReader cr = new ClassReader(bytes);
		cr.accept(cn, 0);
		
		Iterator<MethodNode> methods = cn.methods.iterator();
		while (methods.hasNext()) {
			MethodNode m = methods.next();
			
			if (m.name.equals(names.get("netServerHandler_handleFlying_func")) && m.desc.equals(names.get("netServerHandler_handleFlying_desc"))) {
				System.out.println("Found target method: " + m.name + m.desc + "! Inserting code...");
				
				InsnList toInsert = new InsnList();
				toInsert.add(new VarInsnNode(Opcodes.ALOAD, 1));
				toInsert.add(new VarInsnNode(Opcodes.ALOAD, 0));
				toInsert.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "keepcalm/mods/events/ForgeEventHelper", "onPlayerMove",
						"(L" + names.get("packet10Flying_javaName") + ";L" + names.get("netServerHandler_javaName") + ";)Z"));
				LabelNode endIf = new LabelNode(new Label());
				toInsert.add(new JumpInsnNode(Opcodes.IFEQ, endIf));
				toInsert.add(new InsnNode(Opcodes.RETURN));
				toInsert.add(endIf);
				toInsert.add(new LabelNode(new Label()));
				m.instructions.insert(toInsert);
				
				
			}
		}
		
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		cn.accept(cw);
		return cw.toByteArray();
		
	}
	
	private byte[] transformLightningBolt(byte[] bytes) {
		ClassNode cn = new ClassNode();
		ClassReader cr = new ClassReader(bytes);
		cr.accept(cn, 0);
		
		Iterator<MethodNode> methods = cn.methods.iterator();
		while (methods.hasNext()) {
			MethodNode m = methods.next();
			if (m.name.equals("<init>")) {
				System.out.println("Found constructor in EntityLightningBolt, searching for landmarks...");
				AbstractInsnNode mark1 = null;
				AbstractInsnNode mark2 = null;
				AbstractInsnNode firstEndIf = null;
				AbstractInsnNode secondEndIf = null;
				
				InsnList insns1 = new InsnList();
				insns1.add(new VarInsnNode(Opcodes.ALOAD, 0));
				insns1.add(new VarInsnNode(Opcodes.ALOAD, 1));
				insns1.add(new VarInsnNode(Opcodes.ILOAD, 8));
				insns1.add(new VarInsnNode(Opcodes.ILOAD, 9));
				insns1.add(new VarInsnNode(Opcodes.ILOAD, 10));
				insns1.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "keepcalm/mods/events/ForgeEventHelper", "onLightningStrike", "(L" + names.get("entityLightningBolt_javaName") + ";L" + names.get("world_javaName") + ";III)Z"));
				LabelNode endIf1 = new LabelNode(new Label());
				insns1.add(new JumpInsnNode(Opcodes.IFNE, endIf1));
				
				
				InsnList insns2 = new InsnList();
				insns1.add(new InsnNode(Opcodes.ACONST_NULL));
				insns2.add(new VarInsnNode(Opcodes.ALOAD, 1));
				insns2.add(new VarInsnNode(Opcodes.ILOAD, 9));
				insns2.add(new VarInsnNode(Opcodes.ILOAD, 10));
				insns2.add(new VarInsnNode(Opcodes.ILOAD, 11));
				insns2.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "keepcalm/mods/events/ForgeEventHelper", "onLightningStrike", "(L" + names.get("entityLightningBolt_javaName") + ";L" + names.get("world_javaName") + ";III)Z"));
				LabelNode endIf2 = new LabelNode(new Label());
				insns2.add(new JumpInsnNode(Opcodes.IFNE, endIf2));
				boolean firstInvokeV = false;
				
				for (int i = 0; i < m.instructions.size(); i++) {
					
					if (m.instructions.get(i).getOpcode() == Opcodes.IFEQ) {
						if (mark1 != null) mark2 = m.instructions.get(i).getNext();
						else			   mark1 = m.instructions.get(i).getNext();
					}
					
					if (m.instructions.get(i).getOpcode() == Opcodes.INVOKEVIRTUAL && !firstInvokeV && mark1 != null) {
						firstEndIf = m.instructions.get(i).getNext();
					}
					else if (m.instructions.get(i).getOpcode() == Opcodes.INVOKEVIRTUAL && mark2 != null && firstInvokeV) {
						secondEndIf = m.instructions.get(i).getNext();
					}
				}
				m.instructions.insertBefore(mark1, insns1);
				m.instructions.insertBefore(firstEndIf, endIf1);
				m.instructions.insertBefore(mark2, insns2);
				m.instructions.insertBefore(secondEndIf, endIf2);
				
			}
			
		}
		
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
						toAdd.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "keepcalm/mods/events/ForgeEventHelper", "onSheepDye", "(L" + names.get("entitySheep_javaName") + ";II)Z"));
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

	public byte[] transformCreeper(byte[] bytes) {
		ClassNode cn = new ClassNode();
		ClassReader cr = new ClassReader(bytes);
		cr.accept(cn,  0);
		
		//System.out.println("Transforming EntityCreeper...");
		
		Iterator<MethodNode> methods = cn.methods.iterator();
		
		while (methods.hasNext()) {
			MethodNode m = methods.next();
			
			//System.out.println(m.name + m.desc + " vs " + names.get("entityCreeper_onUpdate_func") + names.get("entityCreeper_onUpdate_desc"));
			if (m.name.equals(names.get("entityCreeper_onUpdate_func")) && m.desc.equals(names.get("entityCreeper_onUpdate_desc"))) {
				for (int i = m.instructions.size() - 1; i >= 0; i--) {
					if (m.instructions.get(i).getOpcode() == Opcodes.IFNE) {
						System.out.println("Found insertion point! inserting code!");
						i++;
						
						InsnList insns = new InsnList();
						
						insns.add(new VarInsnNode(Opcodes.ALOAD, 0));
						insns.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "keepcalm/mods/events/ForgeEventHelper", "onCreeperExplode", "(L" + names.get("entityCreeper_javaName") + ";)Z"));
						LabelNode endIf = new LabelNode(new Label());
						insns.add(new JumpInsnNode(Opcodes.IFEQ, endIf));
						//insns.add(new VarInsnNode(Opcodes.ALOAD, 0));
						//insns.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, names.get("entityCreeper_javaName"), names.get("entityCreeper_setDead_func"), names.get("entityCreeper_setDead_desc")));
						insns.add(new InsnNode(Opcodes.RETURN));
						insns.add(endIf);
						insns.add(new LabelNode(new Label()));
						
						m.instructions.insertBefore(m.instructions.get(i), insns);
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
