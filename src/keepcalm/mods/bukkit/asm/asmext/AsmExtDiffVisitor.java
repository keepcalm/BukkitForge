package keepcalm.mods.bukkit.asm.asmext;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
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
public class AsmExtDiffVisitor extends ClassVisitor {
    public AsmExtDiffVisitor(int i)
    {
        super(i);
    }

    public List<FieldNode> newFields = new ArrayList<FieldNode>();
    public List<MethodNode> newMethods = new ArrayList<MethodNode>();
    public HashMap<String, Integer> methodsToReplace = new HashMap<String, Integer>();

    @Override
    public FieldVisitor visitField(int i, java.lang.String s, java.lang.String s1, java.lang.String s2, java.lang.Object o)
    {
        return new AsmExtFieldCollector(newFields, i, s, s1, s2, o);
    }

    @Override
    public MethodVisitor visitMethod(int i, java.lang.String s, java.lang.String s1, java.lang.String s2, java.lang.String[] strings)
    {
        return new AsmExtMethodCollector(newMethods, methodsToReplace, i, s, s1, s2, strings);
    }
}
