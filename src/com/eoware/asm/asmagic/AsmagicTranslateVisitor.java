package com.eoware.asm.asmagic;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.MethodNode;

import java.util.HashMap;

public class AsmagicTranslateVisitor extends ClassVisitor {

    HashMap<String,String> reps = null;

    public AsmagicTranslateVisitor(ClassVisitor classVisitor, int i, HashMap<String,String> repl ) {
        super(i, classVisitor);
        reps = repl;
    }

    @Override
    public MethodVisitor visitMethod(int i, java.lang.String s, java.lang.String s1, java.lang.String s2, java.lang.String[] strings)
    {
        return new AsmagicMethodTextReplacementVisitor(i, cv.visitMethod(i, s, s1, s2, strings), reps, s1);
    }
}
