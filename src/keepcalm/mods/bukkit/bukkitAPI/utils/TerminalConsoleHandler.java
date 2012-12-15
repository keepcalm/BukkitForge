package keepcalm.mods.bukkit.bukkitAPI.utils;

import java.util.logging.ConsoleHandler;
//import jline.console.ConsoleReader;
//import org.bukkit.craftbukkit.Main;

/**
 * @deprecated
 *
 */
public class TerminalConsoleHandler extends ConsoleHandler {
    private final Object reader;

    public TerminalConsoleHandler(Object reader) {
        super();
        this.reader = reader;
    }

    @Override
    public synchronized void flush() {
        //try {
            /*if (Main.useJline) {
                reader.print(ConsoleReader.RESET_LINE + "");
                reader.flush();
                super.flush();
                try {
                    reader.drawLine();
                } catch (Throwable ex) {
                    reader.getCursorBuffer().clear();
                }
                reader.flush();
            } else {*/
                super.flush();
            //}
        /*} catch (IOException ex) {
            Logger.getLogger(TerminalConsoleHandler.class.getName()).log(Level.SEVERE, null, ex);*/
    }
}
