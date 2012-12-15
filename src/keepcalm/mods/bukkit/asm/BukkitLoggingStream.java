package keepcalm.mods.bukkit.asm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import cpw.mods.fml.relauncher.FMLRelaunchLog;
//import cpw.mods.fml.relauncher.FMLLogFormatter;

public class BukkitLoggingStream extends ByteArrayOutputStream {
        private Logger log;
        private StringBuilder currentMessage;

        public BukkitLoggingStream(Logger log)
        {
            this.log = log;
            this.currentMessage = new StringBuilder();
        }

        @Override
        public void flush() throws IOException
        {
            String record;
            synchronized(FMLRelaunchLog.class)
            {
                super.flush();
                record = this.toString();
                super.reset();

                currentMessage.append(record.replace(System.getProperty("line.separator"), "\n"));
                if (currentMessage.lastIndexOf("\n")>=0)
                {
                    // Are we longer than just the line separator?
                    if (currentMessage.length()>1)
                    {
                        // Trim the line separator
                        currentMessage.setLength(currentMessage.length()-1);
                        log.log(Level.INFO, currentMessage.toString());
                    }
                    currentMessage.setLength(0);
                }
            }
        }
    }