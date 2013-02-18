package com.eoware.asm.asmagic;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldNode;

import java.util.List;

public class AsmagicFieldCollector extends FieldNode {
    public AsmagicFieldCollector(List<FieldNode> nf, int i, java.lang.String s, java.lang.String s1, java.lang.String s2, java.lang.Object o) {
        super(Opcodes.ASM4, i, s, s1, s2, o);
        newFields = nf;
    }

    public List<FieldNode> newFields = null;

    boolean isWriting = false;

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible)
    {
        if(desc.contains("AsmagicFieldAdd"))
        {
            newFields.add(this);
        }

        return super.visitAnnotation(desc, visible);
    }

}
