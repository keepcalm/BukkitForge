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
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.avaje.ebean.enhance.asm.Opcodes;
import com.google.common.collect.Maps;

import cpw.mods.fml.relauncher.IClassTransformer;

public class BlockEventHelpers implements IClassTransformer {

	private HashMap<String,String> names;

	private String itemStackTryPlaceDesc = "(L%s;L%s;IIIIFFF)Z";

	private static final String itemInWorldUpdateDamageDesc = "()V";

	public BlockEventHelpers() {
		names = ObfuscationHelper.getRelevantMappings();

	}

	@Override
	public byte[] transform(String name, byte[] bytes) {
		if (name.equalsIgnoreCase(names.get("itemStackClassName"))) {
			itemStackTryPlaceDesc = String.format(itemStackTryPlaceDesc, new Object[] { names.get("entityPlayerClassName").replace('.', '/'), names.get("worldClassName").replace('.', '/') });
			return transformItemStack(bytes, names);
		}
		else if (name.equalsIgnoreCase(names.get("itemInWorldManagerClassName"))) {
			return transformItemInWorldManager(bytes, names);
		}
		else if (name.equalsIgnoreCase(names.get("blockDispenserClassName"))) {
			return transformDispenser(bytes, names);
		}


		return bytes;
	}

	private byte[] transformDispenser(byte[] bytes, HashMap<String, String> names) {
		ClassNode cn = new ClassNode();
		ClassReader cr = new ClassReader(bytes);
		cr.accept(cn, 0);


		Iterator<MethodNode> methods = cn.methods.iterator();
		while (methods.hasNext()) {
			MethodNode m = methods.next();
			if (m.name.equals(names.get("dispenserDispenseFuncName")) && m.desc.equals(names.get("dispenserDispenseDesc"))) {
				System.out.println("Found target method: " + m.name + m.desc + "! Looking for landmark...");

				Iterator<AbstractInsnNode> insns = m.instructions.iterator();

				while (insns.hasNext()) {
					AbstractInsnNode i = insns.next();

					if (i.getOpcode() == Opcodes.GETSTATIC && i.getNext().getOpcode() == Opcodes.IF_ACMPEQ) {
						System.out.println("Found insertion point - GETSTATIC followed by IF_ACMPEQ!");
						FieldInsnNode f = (FieldInsnNode) i;
						System.out.println("Does " + f.owner + " equal " + names.get("blockDispenserJavaName") + "?");



						System.out.println("Found landmark! Inserting code...");
						InsnList toAdd = new InsnList();

						// mark end of our code
						LabelNode lmmnode = new LabelNode(new Label());
						// we want 1, 2, 3, 4, 8 - world, x, y, z, ItemStack
						toAdd.add(new VarInsnNode(Opcodes.ALOAD, 1));
						toAdd.add(new VarInsnNode(Opcodes.ILOAD, 2));
						toAdd.add(new VarInsnNode(Opcodes.ILOAD, 3));
						toAdd.add(new VarInsnNode(Opcodes.ILOAD, 4));
						toAdd.add(new VarInsnNode(Opcodes.ALOAD, 8));
						toAdd.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "keepcalm/mods/bukkit/ForgeEventHelper", "onDispenseItem", 
								"(L" + names.get("worldJavaName") + ";IIIL" + names.get("itemStackJavaName") + ";)Z"));

						LabelNode endLabel = new LabelNode(new Label());
						toAdd.add(new JumpInsnNode(Opcodes.IFEQ, endLabel)); // if the return value of ^ is true
						// then return - it was cancelled - and so will not be run
						toAdd.add(new InsnNode(Opcodes.RETURN));
						toAdd.add(endLabel); // otherwise, continue on
						toAdd.add(lmmnode);

						System.out.println("Instructions have been compiled, adding to bytecode...");

						m.instructions.insertBefore(i.getNext(), toAdd);

						System.out.println("Finished patching BlockDispenser!");

						break;
					}
				}
			}
		}



		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		cn.accept(cw);
		return cw.toByteArray();
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
					if (instr.getOpcode() == Opcodes.ILOAD && instr.getPrevious().getOpcode() == Opcodes.IRETURN) {
						System.out.println("Found IRETURN after ILOAD, inserting code before...");
						index++;
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
						//System.out.println("Used desc: " + String.format("(L%s;L%s;L%s;IIII)V", new Object[] {names.get("itemStackJavaName"), names.get("entityPlayerJavaName"), names.get("worldJavaName")}));
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

	private byte[] transformItemInWorldManager(byte[] bytes, HashMap<String,String> names) {
		ClassNode cn = new ClassNode();
		ClassReader cr = new ClassReader(bytes);
		cr.accept(cn, 0);

		String targ = names.get("IIWMTargFunc");

		Iterator<MethodNode> methods = cn.methods.iterator();

		while (methods.hasNext()) {
			MethodNode m = methods.next();

			if (m.name.equals(targ) && m.desc.equals(itemInWorldUpdateDamageDesc)) {
				System.out.println("Found target for ItemInWorldManager transformation: " + m.name + m.desc + "! Searching for landmarks...");

				boolean seen = true;
				int occ = 0;
				for (int index = 0; index < m.instructions.size(); index++) {
					AbstractInsnNode instr = m.instructions.get(index);

					if (instr.getOpcode() == Opcodes.RETURN) {
						System.out.println("Found landmark: RETURN, searching for previous landmark...");
						int ifloc = index;
						while (m.instructions.get(index).getOpcode() != Opcodes.IF_ICMPEQ) index--;
						System.out.println("Found IF_ICMPEQ at index " + index + " to R, which is at " + ifloc + ". Inserting function call after this...");
						int loc = index + 1;
						System.out.println("Will insert code at: " + loc);
						// after, not at the same location
						LabelNode lmmnode = new LabelNode(new Label());

						InsnList toInject = new InsnList();

						// load 'this'
						toInject.add(new VarInsnNode(Opcodes.ALOAD, 0));
						// call the helper method
						System.out.println("Using desc: " + "(L" + names.get("itemInWorldManagerJavaName") + ";)V");
						toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "keepcalm/mods/bukkit/ForgeEventHelper",
								"onBlockDamage", "(L" + names.get("itemInWorldManagerJavaName") + ";)Z"));
						LabelNode endIf = new LabelNode(new Label());
						toInject.add(new JumpInsnNode(Opcodes.IFEQ, endIf));
						toInject.add(new InsnNode(Opcodes.RETURN));
						toInject.add(endIf);
						toInject.add(lmmnode);
						System.out.println("Finished compiling instruction nodes, inserting new instructions... at " + loc);

						m.instructions.insertBefore(m.instructions.get(index + 1), toInject);
						/*for (int i1 = 0; i1 < m.instructions.size(); i1++) {
							System.out.println("Location " + i1 + ": " + m.instructions.get(i1).getClass().getName());
							if (m.instructions.get(i1) instanceof MethodInsnNode) {
								MethodInsnNode x = (MethodInsnNode) m.instructions.get(i1);
								System.out.println("Location " + i1 + ": " + x.owner + "/" + x.name + x.desc);
							}
							else if (m.instructions.get(i1) instanceof LineNumberNode) {
								LineNumberNode x = (LineNumberNode) m.instructions.get(i1);
								System.out.println("Location " + i1 + ": Line " + x.line);
							}
						}*/
						//MethodInsnNode x = (MethodInsnNode) m.instructions.get(249);
						System.out.println("Finished patching ItemInWorldManager! The game will now continue!");
						break;
					}
					else if (instr.getOpcode() == Opcodes.PUTFIELD && instr.getPrevious().getOpcode() == Opcodes.ILOAD && ((VarInsnNode)instr.getPrevious()).var == 3 && instr.getNext() instanceof LabelNode && !seen) {
						occ++;
						if (occ > 0) seen = true;

						System.out.println("Found first occurance of misbehaving segment, ignoring...");
					}

				}


			}
		}


		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		cn.accept(cw);
		return cw.toByteArray();
	}

}
