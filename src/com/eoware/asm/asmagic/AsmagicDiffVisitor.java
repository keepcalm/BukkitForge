package com.eoware.asm.asmagic;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AsmagicDiffVisitor extends ClassVisitor {
    public AsmagicDiffVisitor(int i)
    {
        super(i);
    }

    public List<FieldNode> newFields = new ArrayList<FieldNode>();
    public List<MethodNode> newMethods = new ArrayList<MethodNode>();
    public HashMap<String, Integer> methodsToReplace = new HashMap<String, Integer>();

    @Override
    public FieldVisitor visitField(int i, java.lang.String s, java.lang.String s1, java.lang.String s2, java.lang.Object o)
    {
        return new AsmagicFieldCollector(newFields, i, s, s1, s2, o);
    }

    @Override
    public MethodVisitor visitMethod(int i, java.lang.String s, java.lang.String s1, java.lang.String s2, java.lang.String[] strings)
    {
        return new AsmagicMethodCollector(newMethods, methodsToReplace, i, s, s1, s2, strings);
    }
}
