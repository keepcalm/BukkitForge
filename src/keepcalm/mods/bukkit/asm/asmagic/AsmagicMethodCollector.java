package keepcalm.mods.bukkit.asm.asmagic;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;

import java.util.HashMap;
import java.util.List;

public class AsmagicMethodCollector extends MethodNode {

    public AsmagicMethodCollector(List<MethodNode> nm, HashMap<String, Integer> rm, int i, java.lang.String s, java.lang.String s1, java.lang.String s2, java.lang.String[] strings){

        super(Opcodes.ASM4, i, s, s1, s2, strings);
        newMethods = nm;
        replaceMethods = rm;
    }

    public List<MethodNode> newMethods = null;

    public HashMap<String, Integer> replaceMethods = null;

    public boolean isWriting = false;

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible)
    {
        boolean wasAsmAnnotated = false;

        if( !isWriting )
        {
            if(desc.contains("AsmagicMethodAdd") )
            {
                newMethods.add(this);
                wasAsmAnnotated = true;
            }

            if(desc.contains("AsmagicMethodReplace"))
            {
                replaceMethods.put(this.name + this.desc, 0);

                newMethods.add(this);
                wasAsmAnnotated = true;
            }

            isWriting = true;
        }
        if( !wasAsmAnnotated )
            return super.visitAnnotation(desc, visible);
        else
            return null;
    }


}
