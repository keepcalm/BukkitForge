package keepcalm.mods.bukkit.asm.transformers;

import java.util.HashMap;
import java.util.Iterator;

import keepcalm.mods.events.asm.transformers.events.ObfuscationHelper;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import cpw.mods.fml.relauncher.IClassTransformer;

/**
 * 
 * Class of transformers to add calls to {@link keepcalm.mods.bukkit.BukkitHelpers}
 * for example, add mod materials, etc
 * 
 * @author keepcalm
 *
 */
public class BukkitAPIHelperTransformer implements IClassTransformer, Opcodes {

	private static HashMap<String,String> names = ObfuscationHelper.getRelevantMappings();
	
	private static final String itemInitDesc = "(I)V";
	
	@Override
	public byte[] transform(String name, byte[] bytes) {
		
		if (name.equals(names.get("item_className"))) {
			System.out.println("Transforming " + name + "...");
			return transformItem(bytes);
		}
		
		return bytes;
	}
	
	private byte[] transformItem(byte[] bytes) {
		ClassReader cr = new ClassReader(bytes);
		ClassNode cn = new ClassNode();
		cr.accept(cn, 0);
		System.out.println("Inspecting " + cn.methods.size() + " methods...");
		Iterator<MethodNode> methods = cn.methods.iterator();
		while (methods.hasNext()) {
			MethodNode m = methods.next();
			//System.out.println("Method: " + m.name + " Desc: " + m.desc);
			if (m.name.equals("<init>") && m.desc.equals(itemInitDesc)) {
				System.out.println("Found matching constructor method in Item: " + m.name + m.desc + "!");
				
				InsnList insn = new InsnList();
				
				insn.add(new VarInsnNode(ILOAD, 1));
				insn.add(new MethodInsnNode(INVOKESTATIC, "keepcalm/mods/bukkit/BukkitHelpers", "addMaterial", "(I)V"));
				insn.add(new LabelNode(new Label()));
				
				m.instructions.insert(insn);
				
				System.out.println("Finished patching item-register method into BukkitForge!");
			}
			
		}
		
		
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		cn.accept(cw);
		return cw.toByteArray();
	}

	
	
}
