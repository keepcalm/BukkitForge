package org.bukkit.util;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_5_R3.CraftServer;

public class ExceptionHandler implements UncaughtExceptionHandler {

    public void uncaughtException(Thread t, Throwable e) {
        Logger log = ((CraftServer) Bukkit.getServer()).getLogger();
        log.log(Level.SEVERE, "The server has crashed!");
        log.log(Level.SEVERE, "Please report this to md_5!");
        log.log(Level.SEVERE, "Begin Exception Trace:");
        log.log(Level.SEVERE, "");
        StackTraceElement[] stack = e.getStackTrace();
        for (int line = 0; line < stack.length; line++) {
            log.log(Level.SEVERE, "        " + stack[line].toString());
        }
        log.log(Level.SEVERE, "End Exception Trace:");
        log.log(Level.SEVERE, "");
        log.log(Level.SEVERE, "Begin Thread Stack Trace:");
        stack = t.getStackTrace();
        for (int line = 0; line < stack.length; line++) {
            log.log(Level.SEVERE, "        " + stack[line].toString());
        }
        log.log(Level.SEVERE, "End Exception Trace:");
        log.log(Level.SEVERE, "");
    }
}
