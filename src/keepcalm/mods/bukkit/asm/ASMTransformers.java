package keepcalm.mods.bukkit.asm;

import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.logging.LogManager;
import java.util.logging.Logger;

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
		cw.visitField(Opcodes.ACC_PRIVATE, "logger", "java/util/logging/Logger", null, BukkitContainer.bukkitLogger).visitEnd();
		return cw.toByteArray();
	}
}
