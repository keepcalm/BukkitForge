package org.bukkit.craftbukkit.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;

public class Versioning {
	public final static String getBFVersion() {
		String result = "Unknown-Version";

		InputStream stream = Bukkit.class.getClassLoader().getResourceAsStream("META-INF/MANIFEST.MF");
		BufferedReader buf = new BufferedReader(new InputStreamReader(stream));
		String line;
		HashMap<String,String> map = Maps.newHashMap();
		try {
			while ((line = buf.readLine()) != null) {
				if (stream != null) {
					String[] str = line.split(":");
					if (str.length <  2) {
						continue; // invalid
					}
					//if (str.length > 2) {
						//str[1] = Joiner.on(':').join(Arrays.copyOfRange(str, 1, str.length));
					//}
					System.out.println(str[0] + "=>" + str[1]);
					map.put(str[0], str[1]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		result = "@VERSION@";
		return result;
	}
}
