package keepcalm.mods.bukkit.asm;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import java.util.HashMap;
import java.util.Iterator;

import keepcalm.mods.bukkit.forgeHandler.VanishUtils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import cpw.mods.fml.relauncher.IClassTransformer;

public class BukkitVanishTransformer implements IClassTransformer {

	private HashMap<String,String> obfNames = new HashMap<String, String>();
	private HashMap<String,String> devNames = new HashMap<String, String>();
	
	private static final String targMethodDesc = "()V";
	
	private static final String newFuncOwner = "keepcalm.mods.bukkit.forgeHandler.VanishUtils";
	private static final String newFuncName = "isHidden";
	private static       String newFuncDesc;
	
	
	
	public BukkitVanishTransformer() {
		System.out.println("INITIALIZING VANISH TRANSFORMER!");
		// net.minecraft.entity.EntityTracker
		obfNames.put("targClass", "ii");
		// updateTrackedEntities
		obfNames.put("targMethod", "a");
		// net.minecraft.entity.player.EntityPlayerMP
		obfNames.put("entityPlayerMPClass", "iq");
		// net/minecraft/entity/player/EntityPlayerMP
		obfNames.put("entityPlayerMPJavaClass", "iq");
		// net/minecraft/entity/Entity
		obfNames.put("entityJavaClass", "lq");
		
		devNames.put("targClass", "net.minecraft.entity.EntityTracker");
		devNames.put("targMethod", "updateTrackedEntities");
		devNames.put("entityPlayerMPClass", "net.minecraft.entity.player.EntityPlayerMP");
		devNames.put("entityPlayerMPJavaClass", "net/minecraft/entity/player/EntityPlayerMP");
		devNames.put("entityJavaClass", "net/minecraft/entity/Entity");
		
		
	}
	
	@Override
	public byte[] transform(String name, byte[] bytes) {
		//System.out.println("Might transform class: " + name);
		if (name.equalsIgnoreCase(obfNames.get("targClass"))) {
			System.out.println("Ok, I will!");
			return transformEntityTracker(bytes, obfNames);
		}
		else if (name.equalsIgnoreCase(devNames.get("targClass"))) {
			System.out.println("Ok, I will!");
			return transformEntityTracker(bytes, devNames);
		}
		//System.out.println("Nope!");
		return bytes;
	}
	
	private byte[] transformEntityTracker(byte[] clazz, HashMap<String,String> maps) {
		System.out.println("Adding vanishing support...");
		newFuncDesc = String.format("(L%s;L%s;)Z", new Object[] { maps.get("entityPlayerMPJavaClass"), maps.get("entityJavaClass") });
		ClassNode cn = new ClassNode();
		ClassReader cr = new ClassReader(clazz);
		cr.accept(cn, 0);
		
		Iterator<MethodNode> methods = cn.methods.iterator();
		
		while (methods.hasNext()) {
			MethodNode m = methods.next();
			//System.out.println("Method: " + m.name + " Desc: " + m.desc + " targ method: " + maps.get("targMethod") + " tag desc: " + targMethodDesc);
			if (m.name.equalsIgnoreCase(maps.get("targMethod")) && m.desc.equalsIgnoreCase(targMethodDesc)) {
				System.out.println("Found target method: " + m.name + m.desc);
				int lastPlayerPos = -1;
				int entryPos = -1;
				String entryOwner = "";
				String entryName = "";
				String entryDesc = "";
				for (int index = 0; index < m.instructions.size(); index++) {
                    //System.out.println("Processing INSN at " + index +
                    //" of type " + m.instructions.get(index).getType() +
                    //", OpCode " + m.instructions.get(index).getOpcode());
					if (m.instructions.get(index).getOpcode() == Opcodes.ALOAD) {
						VarInsnNode j = (VarInsnNode) m.instructions.get(index);
						lastPlayerPos = j.var;
					}
					if (m.instructions.get(index).getOpcode() == Opcodes.GETFIELD) {
						FieldInsnNode j = (FieldInsnNode) m.instructions.get(index);
						if (j.desc.equalsIgnoreCase(Type.getDescriptor(Entity.class))) {
							entryOwner = j.owner;
							entryName = j.name;
							entryDesc = j.desc;
							// save it!
							entryPos = lastPlayerPos;
						}
					}
					
                    if (m.instructions.get(index).getOpcode() == Opcodes.IF_ACMPEQ) {
                    	if (entryPos == -1 || lastPlayerPos == -1) {
    						throw new RuntimeException("Failed to add vanishing code!");
    					}
                    	System.out.println("Found if statement, preparing to insert additional check...");
                    	
                    	int newIndex = Integer.valueOf(index);
                    	
                    	while (!(m.instructions.get(newIndex) instanceof LabelNode))
                    		newIndex++;
                    	
                    	LabelNode jumpTarg = (LabelNode) m.instructions.get(newIndex);//.getNext();
                    	
                    	  // make a new label node for the end of our code
                        LabelNode lmm1Node = new LabelNode(new Label());
                        // make new instruction list
                        InsnList toInject = new InsnList();
                        Boolean b;
                        toInject.add(new VarInsnNode(Opcodes.ALOAD, lastPlayerPos));
                        toInject.add(new VarInsnNode(Opcodes.ALOAD, entryPos));
                        toInject.add(new FieldInsnNode(GETFIELD, entryOwner, entryName, entryDesc));
                        toInject.add(new MethodInsnNode(Opcodes.GETSTATIC, "keepcalm.mods.bukkit.forgeHandler.VanishUtils", "isHidden", newFuncDesc));
                        toInject.add(new InsnNode(Opcodes.ISTORE));
                        toInject.add(new JumpInsnNode(Opcodes.IF_ACMPEQ, jumpTarg));
                        
                        m.instructions.insertBefore(m.instructions.get(index), toInject);
                        System.out.println("Finished patching class!");
                        break;
                    }
				}
			}
			
		}
		
		return clazz;
	}

}
