/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bukkit.util;

public class TimedThread extends Thread {

    final Runnable runnable;
    final long time;

    public TimedThread(Runnable runnable, long time) {
        super("Spigot Metrics Gathering Thread");
        setDaemon(true);
        this.runnable = runnable;
        this.time = time;
    }

    @Override
    public void run() {
        try {
            sleep(60000);
        } catch (InterruptedException ie) {
        }

        while (!isInterrupted()) {
            try {
                runnable.run();
                sleep(time);
            } catch (InterruptedException ie) {
            } catch (Exception ex) {
                ex.printStackTrace();
                interrupt();
            }
        }
    }
}
