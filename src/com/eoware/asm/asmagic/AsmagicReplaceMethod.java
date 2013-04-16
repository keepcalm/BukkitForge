package com.eoware.asm.asmagic;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created with IntelliJ IDEA.
 * User: jtdossett
 * Date: 2/11/13
 * Time: 10:27 PM
 * To change this template use File | Settings | File Templates.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface AsmagicReplaceMethod {
    String obfuscatedName() default "";
    int replaceType() default 1; // 1 - full, 2 - accessonly
}
