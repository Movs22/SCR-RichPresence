package com.movies22.scr.rpc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.TreeMap;

public class Config {
	private Map<String, String> config;
	private File file;
	public Config(File loc) {
		file = loc;
		config = new TreeMap<String, String>();
		String settings;
		try {
			settings = Files.readString(loc.toPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Main.logger.severe("Failed to load the RPC's settings");
			return;
		}
		String[] a = settings.split("\n");
		Main.logger.info(a[0]);
		for(String setting : a) {
			String[] keyVal = setting.split("=");
			if(keyVal.length > 2) {
				Main.logger.severe("Attempted to load an invalid key,value setting from config.txt. Expected 1 value for " + keyVal[0] + " but received " + (keyVal.length - 1));
				saveAll();
				System.exit(1);
			} else if(keyVal.length == 2) {
				config.put(keyVal[0], keyVal[1]);
			} else if(keyVal[0] != "") {
				Main.logger.warning("Config key \"" + keyVal[0] + "\" is empty.");
				config.put(keyVal[0], null);
			}
		}
	}
	
	public void deleteSetting(String key) {
		config.remove(key);
	}
	
	public void changeSetting(String key, String value) {
		config.put(key, value);
	}
	
	public String getValue(String key) {
		return config.get(key);
	}
	
	public void saveAll() {
		try {
			FileWriter w = new FileWriter(file);
			String toSave = "";
			for(Map.Entry<String, String> entry : config.entrySet()) {
				toSave = toSave + entry.getKey() + "=" + entry.getValue() + "\n";
			}
			w.write(toSave);
			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
