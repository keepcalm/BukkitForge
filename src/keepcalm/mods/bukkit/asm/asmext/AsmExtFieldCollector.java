package keepcalm.mods.bukkit.asm.asmext;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.tree.FieldNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jtdossett
 * Date: 2/11/13
 * Time: 11:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class AsmExtFieldCollector extends FieldNode {
    public AsmExtFieldCollector( List<FieldNode> nf, int i, java.lang.String s, java.lang.String s1, java.lang.String s2, java.lang.Object o ) {
        super(i, s, s1, s2, o);
        newFields = nf;
    }

    public List<FieldNode> newFields = null;

    boolean isWriting = false;

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible)
    {
        if( !isWriting )
        {
            if(desc.contains("AsmExtFieldAdd"))
            {
                newFields.add(this);
            }

            isWriting = true;
        }

        return super.visitAnnotation(desc, visible);
    }
}
