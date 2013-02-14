package keepcalm.mods.bukkit.asm.asmext;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jtdossett
 * Date: 2/11/13
 * Time: 11:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class AsmExtMethodCollector extends MethodNode {

    public AsmExtMethodCollector(List<MethodNode> nm, HashMap<String, Integer> rm, int i, java.lang.String s, java.lang.String s1, java.lang.String s2, java.lang.String[] strings){
        super(ClassWriter.COMPUTE_MAXS, i, s, s1, s2, strings);
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
            if(desc.contains("AsmExtMethodAdd") )
            {
                newMethods.add(this);
                wasAsmAnnotated = true;
            }

            if(desc.contains("AsmExtMethodReplace"))
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
