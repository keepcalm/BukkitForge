package org.bukkit.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Class containing file utilities
 */

public class FileUtil {

    /**
     * This method copies one file to another location
     *
     * @param inFile the source filename
     * @param outFile the target filename
     * @return true on success
     */

    public static boolean copy(File inFile, File outFile) {
        if (!inFile.exists()) {
            return false;
        }

        FileChannel in = null;
        FileChannel out = null;
        FileInputStream tin = null;
        FileOutputStream tout = null;

        try {
        	tin = new FileInputStream(inFile);
        	tout = new FileOutputStream(outFile);
        	
            in = tin.getChannel();
            out = tout.getChannel();

     
            long pos = 0;
            long size = in.size();

            while (pos < size) {
                pos += in.transferTo(pos, 10 * 1024 * 1024, out);
            }
        } catch (IOException ioe) {
            return false;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                
                if(tin != null)
                	tin.close();
                
                if(tout != null)
                	tout.close();
            } catch (IOException ioe) {
                return false;
            }
        }

        return true;

    }
}
