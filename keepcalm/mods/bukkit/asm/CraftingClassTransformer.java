package keepcalm.mods.bukkit.asm;

import org.objectweb.asm.*;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@TransformerExclusions
public class CraftingClassTransformer extends ClassVisitor {

	public CraftingClassTransformer( ClassVisitor cv) {
		super(Opcodes.ASM4, cv);
		// TODO Auto-generated constructor stub
	}
	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		if (name == "recipes") {
			boolean isPrivate = (access & Opcodes.ACC_PRIVATE) == 0;
			if (isPrivate) {
				return cv.visitField(Opcodes.ACC_PUBLIC, name, desc, signature,  value);
			}
		}
		return cv.visitField(access, name, desc, signature, value);
	}
	
	
}
