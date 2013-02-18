package com.eoware.asm.asmagic;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AsmagicClassTransformer {

    public AsmagicClassTransformer(HashMap<String,String> classesToTransform)
    {
        c2t = classesToTransform;
    }

    HashMap<String,String> c2t = null;

    public byte[] transform(String s, byte[] bytes) {
        String nameToMatch = s.replace('/', '.');

        if( c2t.containsKey(nameToMatch) )
        {
            return transformClass(bytes, c2t.get(nameToMatch));
        }

        return bytes;
    }

    protected byte[] transformClass(byte[] bytes, String clssname )
    {
        InputStream is = getClass().getResourceAsStream( "/" + clssname.replace('.', '/') + ".class" );

        ClassReader orig = null;
        try {
            orig = new ClassReader(bytes);
            ClassReader repl = new ClassReader(is);

            ClassWriter cw = new ClassWriter(orig, ClassWriter.COMPUTE_MAXS);

            AsmagicDiffVisitor dv = new AsmagicDiffVisitor(262144);
            repl.accept(dv, ClassReader.SKIP_FRAMES);

            replaceObfuscatedMethodNames(dv);

            AsmagicSyncVisitor sv = new AsmagicSyncVisitor(cw, 262144, dv.newFields, dv.newMethods, new ArrayList<String>( dv.methodsToReplace.keySet() ) );
            orig.accept(sv, ClassReader.SKIP_FRAMES);

            return cw.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return bytes;
        }
    }

    private void replaceObfuscatedMethodNames(AsmagicDiffVisitor dv) {
        for (MethodNode mn : dv.newMethods) {
            if (mn.visibleAnnotations != null) {
                for (Object oan : mn.visibleAnnotations) {
                    AnnotationNode an = (AnnotationNode) oan;
                    if (an.desc.contains("AsmagicMethodReplace")) {
                        List<Object> vals = an.values;

                        String alias = null;
                        if (vals != null || vals.size() > 1) {
                            alias = (String) vals.get(1);
                            dv.methodsToReplace.put(alias, 1);
                            mn.name = alias;
                        }
                    }
                }
            }
        }
    }
}
