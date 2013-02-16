package keepcalm.mods.bukkit.asm.asmagic;

/**
 * Created with IntelliJ IDEA.
 * User: jtdossett
 * Date: 2/11/13
 * Time: 10:27 PM
 * To change this template use File | Settings | File Templates.
 */
public @interface AsmagicMethodReplace {
    String obfuscatedName() default "";
}
