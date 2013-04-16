package com.eoware.asm.asmagic;

/**
 * Created with IntelliJ IDEA.
 * User: jtdossett
 * Date: 2/22/13
 * Time: 9:46 PM
 * To change this template use File | Settings | File Templates.
 */
public @interface AsmagicClassProxy {
    String proxiedClass() default "";
}
