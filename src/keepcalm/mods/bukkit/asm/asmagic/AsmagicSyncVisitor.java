package keepcalm.mods.bukkit.asm.asmagic;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jtdossett
 * Date: 2/11/13
 * Time: 3:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class AsmagicSyncVisitor extends ClassVisitor {
    public AsmagicSyncVisitor(ClassVisitor nextVisitor, int i, List<FieldNode> nf, List<MethodNode> nm, ArrayList<String> m2r)
    {
        super(i, nextVisitor);
        newFields = nf;
        newMethods = nm;

        for(String mid : m2r )
        {
            replaceMethods.put(mid, 1);
        }
    }

    public List<FieldNode> newFields = new ArrayList<FieldNode>();
    public List<MethodNode> newMethods = new ArrayList<MethodNode>();
    public HashMap<String, Integer> replaceMethods = new HashMap<String, Integer>();

    @Override
    public MethodVisitor visitMethod(int i, java.lang.String s, java.lang.String s1, java.lang.String s2, java.lang.String[] strings)
    {
        MethodVisitor mv = null;

        MethodNode node = new MethodNode(i,s,s1,s2,strings);

        String methodKey = node.name + node.desc;

        if(replaceMethods.containsKey(methodKey))
        {
            mv = null;
        }
        else
        {
            mv = cv.visitMethod(i,s,s1,s2,strings);
        }

        return mv;
    }

    @Override
    public void visitEnd()
    {
        for( FieldNode fn : newFields )
        {
            fn.accept( cv );
        }

        for( MethodNode mn : newMethods )
        {
            mn.accept( cv );
        }

        cv.visitEnd();
    }
}
