package org.bukkit.craftbukkit.scheduler;

import org.bukkit.plugin.Plugin;


class B4VAsyncDebugger {
    private B4VAsyncDebugger next = null;
    private final int expiry;
    private final Plugin plugin;
    private final Class<? extends Runnable> clazz;

    B4VAsyncDebugger(final int expiry, final  Plugin plugin, final Class<? extends Runnable> clazz) {
        this.expiry = expiry;
        this.plugin = plugin;
        this.clazz = clazz;

    }

    final B4VAsyncDebugger getNextHead(final int time) {
        B4VAsyncDebugger next, current = this;
        while (time > current.expiry && (next = current.next) != null) {
            current = next;
        }
        return current;
    }

    final B4VAsyncDebugger setNext(final B4VAsyncDebugger next) {
        return this.next = next;
    }

    StringBuilder debugTo(final StringBuilder string) {
        for (B4VAsyncDebugger next = this; next != null; next = next.next) {
            string.append(next.plugin.getDescription().getName()).append(':').append(next.clazz.getName()).append('@').append(next.expiry).append(',');
        }
        return string;
    }
}
