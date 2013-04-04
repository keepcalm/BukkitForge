package keepcalm.mods.events.asm;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.IFNULL;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.ISTORE;

import java.util.HashMap;
import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import cpw.mods.fml.relauncher.IClassTransformer;

/**
 * 
 * Taken from bspkrs's treecapitator mod, adds a more generic event-style system to it.
 * 
 * @author bspkrs
 *
 */
public class BlockBreakEventAdder implements IClassTransformer
{
    /* Obfuscated Names for ItemInWorldManager Transformation */
    
    /* removeBlock */
    private final String  targetMethodDesc = "(III)Z";
    
    private final HashMap obfStrings;
    private final HashMap mcpStrings;
    
    public BlockBreakEventAdder()
    {
        /*
         * create a HashMap to store the obfuscated names of classes, methods, and fields used in the transformation
         */
        obfStrings = new HashMap();
        /* net.minecraft.src.ItemInWorldManager */
        obfStrings.put("className", "jd");
        /* net/minecraft/src/ItemInWorldManager */
        obfStrings.put("javaClassName", "jd");
        /* removeBlock */
        obfStrings.put("targetMethodName", "d");
        /* theWorld */
        obfStrings.put("worldFieldName", "a");
        /* thisPlayerMP */
        obfStrings.put("entityPlayerFieldName", "b");
        /* net/minecraft/src/World */
        obfStrings.put("worldJavaClassName", "aab");
        /* net/minecraft/src/World.getBlockMetadata() */
        obfStrings.put("getBlockMetadataMethodName", "h");
        /* net/minecraft/src/Block */
        obfStrings.put("blockJavaClassName", "apa");
        /* net/minecraft/src/Block.blocksList[] */
        obfStrings.put("blocksListFieldName", "r");
        /* net/minecraft/src/EntityPlayer */
        obfStrings.put("entityPlayerJavaClassName", "sq");
        /* net/minecraft/src/EntityPlayerMP */
        obfStrings.put("entityPlayerMPJavaClassName", "jc");
        
        /*
         * create a HashMap to store the MCP names of classes, methods, and fields used in the transformation
         */
        mcpStrings = new HashMap();
        mcpStrings.put("className", "net.minecraft.item.ItemInWorldManager");
        mcpStrings.put("javaClassName", "net/minecraft/item/ItemInWorldManager");
        mcpStrings.put("targetMethodName", "removeBlock");
        mcpStrings.put("worldFieldName", "theWorld");
        mcpStrings.put("entityPlayerFieldName", "thisPlayerMP");
        mcpStrings.put("worldJavaClassName", "net/minecraft/world/World");
        mcpStrings.put("getBlockMetadataMethodName", "getBlockMetadata");
        mcpStrings.put("blockJavaClassName", "net/minecraft/block/Block");
        mcpStrings.put("blocksListFieldName", "blocksList");
        mcpStrings.put("entityPlayerJavaClassName", "net/minecraft/entity/player/EntityPlayer");
        mcpStrings.put("entityPlayerMPJavaClassName", "net/minecraft/entity/player/EntityPlayerMP");
    }
    
    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes)
    {
        // System.out.println("transforming: "+name);
        if (name.equals(obfStrings.get("className")))
        {
            return transformItemInWorldManager(bytes, obfStrings);
        }
        else if (name.equals(mcpStrings.get("className")))
        {
            return transformItemInWorldManager(bytes, mcpStrings);
        }
        
        return bytes;
    }
    
    private byte[] transformItemInWorldManager(byte[] bytes, HashMap hm)
    {
        System.out.println("Class Transformation running on ItemInWorldManager...");
        
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);
        
        // find method to inject into
        Iterator<MethodNode> methods = classNode.methods.iterator();
        while (methods.hasNext())
        {
            MethodNode m = methods.next();
            if (m.name.equals(hm.get("targetMethodName")) && m.desc.equals(targetMethodDesc))
            {
                System.out.println("Found target method " + m.name + m.desc + "! Searching for landmarks...");
                int blockIndex = 4;
                int mdIndex = 5;
                
                // find injection point in method (use IFNULL inst)
                for (int index = 0; index < m.instructions.size(); index++)
                {
                    // System.out.println("Processing INSN at " + index +
                    // " of type " + m.instructions.get(index).getType() +
                    // ", OpCode " + m.instructions.get(index).getOpcode());
                    // find local Block object node and from that, local object
                    // index
                    if (m.instructions.get(index).getType() == AbstractInsnNode.FIELD_INSN)
                    {
                        FieldInsnNode blocksListNode = (FieldInsnNode) m.instructions.get(index);
                        if (blocksListNode.owner.equals(hm.get("blockJavaClassName")) && blocksListNode.name.equals(hm.get("blocksListFieldName")))
                        {
                            int offset = 1;
                            while (m.instructions.get(index + offset).getOpcode() != ASTORE)
                                offset++;
                            System.out.println("Found Block object ASTORE Node at " + (index + offset));
                            VarInsnNode blockNode = (VarInsnNode) m.instructions.get(index + offset);
                            blockIndex = blockNode.var;
                            System.out.println("Block object is in local object " + blockIndex);
                        }
                    }
                    
                    // find local metadata variable node and from that, local
                    // variable index
                    if (m.instructions.get(index).getType() == AbstractInsnNode.METHOD_INSN)
                    {
                        MethodInsnNode mdNode = (MethodInsnNode) m.instructions.get(index);
                        if (mdNode.owner.equals(hm.get("worldJavaClassName")) && mdNode.name.equals(hm.get("getBlockMetadataMethodName")))
                        {
                            int offset = 1;
                            while (m.instructions.get(index + offset).getOpcode() != ISTORE)
                                offset++;
                            System.out.println("Found metadata local variable ISTORE Node at " + (index + offset));
                            VarInsnNode mdFieldNode = (VarInsnNode) m.instructions.get(index + offset);
                            mdIndex = mdFieldNode.var;
                            System.out.println("Metadata is in local variable " + mdIndex);
                        }
                    }
                    
                    if (m.instructions.get(index).getOpcode() == IFNULL)
                    {
                        System.out.println("Found IFNULL Node at " + index);
                        
                        int offset = 1;
                        while (m.instructions.get(index + offset).getOpcode() != ALOAD)
                            offset++;
                        
                        System.out.println("Found ALOAD Node at offset " + offset + " from IFNULL Node");
                        System.out.println("Patching method " + (String) hm.get("javaClassName") + "/" + m.name + m.desc + "...");
                        
                        // make a new label node for the end of our code
                        LabelNode lmm1Node = new LabelNode(new Label());
                        
                        // make new instruction list
                        InsnList toInject = new InsnList();
                        
                        // construct instruction nodes for list
                        toInject.add(new VarInsnNode(ALOAD, 0));
                        toInject.add(new FieldInsnNode(GETFIELD, (String) hm.get("javaClassName"), (String) hm.get("worldFieldName"), "L" + (String) hm.get("worldJavaClassName") + ";"));
                        toInject.add(new VarInsnNode(ILOAD, 1));
                        toInject.add(new VarInsnNode(ILOAD, 2));
                        toInject.add(new VarInsnNode(ILOAD, 3));
                        toInject.add(new VarInsnNode(ALOAD, blockIndex));
                        toInject.add(new VarInsnNode(ILOAD, mdIndex));
                        toInject.add(new VarInsnNode(ALOAD, 0));
                        toInject.add(new FieldInsnNode(GETFIELD, (String) hm.get("javaClassName"), (String) hm.get("entityPlayerFieldName"), "L" + (String) hm.get("entityPlayerMPJavaClassName") + ";"));
                        toInject.add(new MethodInsnNode(INVOKESTATIC, "keepcalm/mods/events/EventFactory", "onBlockHarvested", "(L" + (String) hm.get("worldJavaClassName") + ";IIIL" + (String) hm.get("blockJavaClassName") + ";IL" + (String) hm.get("entityPlayerJavaClassName") + ";)Z"));
                        LabelNode endIf = new LabelNode(new Label());
                        toInject.add(new JumpInsnNode(Opcodes.IFEQ, endIf));
                        toInject.add(new InsnNode(Opcodes.ICONST_0));
                        toInject.add(new InsnNode(Opcodes.IRETURN));
                        toInject.add(endIf);
                        toInject.add(lmm1Node);
                        
                        m.instructions.insertBefore(m.instructions.get(index + offset), toInject);
                        
                        System.out.println("Method " + (String) hm.get("javaClassName") + "/" + m.name + m.desc + " at index " + (index + offset - 1));
                        System.out.println("Patching Complete!");
                        break;
                    }
                }
            }
        }
        
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }
}
