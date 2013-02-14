package keepcalm.mods.bukkit.asm.asmext;

import cpw.mods.fml.relauncher.IClassTransformer;
import keepcalm.mods.bukkit.asm.replacements.CommandHandler_BukkitForge;
import keepcalm.mods.bukkit.asm.replacements.MinecraftServer_BukkitForge;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import keepcalm.mods.bukkit.asm.replacements.DimensionManager_BukkitForge;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: jtdossett
 * Date: 2/12/13
 * Time: 8:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class AsmExtClassTransformer implements IClassTransformer {

    static protected HashMap<String,String> classesToTransform = createClassesToTransform();

    static protected HashMap<String,String> createClassesToTransform() {
        HashMap<String,String> map = new HashMap<String, String>();

//        map.put( "net.minecraft.command.CommandHandler", CommandHandler_BukkitForge.class.getName());
        //map.put( "net.minecraft.server.dedicated.DedicatedServer", MinecraftServer_BukkitForge.class.getName() );
        //map.put( "net.minecraftforge.common.DimensionManager", DimensionManager_BukkitForge.class.getName() );

        return map;
    }

    public AsmExtClassTransformer()
    {
        System.out.println( "Instantiating AsmExtClassTransformer ...");
    }


    @Override
    public byte[] transform(String s, byte[] bytes) {

        String nameToMatch = s.replace('/', '.');

        if( classesToTransform.containsKey(nameToMatch) )
        {
            System.out.println( "Transforming " + nameToMatch + " with " +  classesToTransform.get(nameToMatch) + " ...");
            return transformClass(bytes, classesToTransform.get(nameToMatch));
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

            AsmExtDiffVisitor dv = new AsmExtDiffVisitor(Opcodes.ASM4);
            repl.accept(dv, ClassReader.SKIP_FRAMES);

            AsmExtSyncVisitor sv = new AsmExtSyncVisitor(cw, Opcodes.ASM4, dv.newFields, dv.newMethods, new ArrayList<String>( dv.methodsToReplace.keySet() ) );
            orig.accept(sv, ClassReader.SKIP_FRAMES);

            return cw.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return bytes;
        }
    }
}
