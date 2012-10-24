package keepcalm.mods.bukkit.asm;

import java.security.ProtectionDomain;
import java.util.ArrayList;

import org.apache.commons.lang.IllegalClassException;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import cpw.mods.fml.relauncher.IClassTransformer;

public class ASMTransformers implements IClassTransformer {
	public byte[] transform(String name, byte[] b)  {
		ClassWriter cw = new ClassWriter(0);
		ClassVisitor cv = new ClassVisitor(Opcodes.ASM4, cw) { };
		CraftingClassTransformer ct = new CraftingClassTransformer(cv);
		
		ClassReader cr = new ClassReader(b);
		cr.accept(ct, 0);
		cw.visitField(Opcodes.ACC_PRIVATE, "recipes", "java/lang/List", null, new ArrayList()).visitEnd();
		return cw.toByteArray();
	}
}
