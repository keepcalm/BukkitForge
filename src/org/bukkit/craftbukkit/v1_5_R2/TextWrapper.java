package org.bukkit.craftbukkit.v1_5_R2;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;

public class TextWrapper {
    public static List<String> wrapText(final String text) {
        ArrayList<String> output = new ArrayList<String>();
        String[] lines = text.split("\n");
        String lastColor = null;

        for (String line : lines) {
            if (lastColor != null) {
                line = lastColor + line;
            }

            output.add(line);
            lastColor = ChatColor.getLastColors(line);
        }

        return output;
    }
}
