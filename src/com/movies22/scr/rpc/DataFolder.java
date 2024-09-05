package com.movies22.scr.rpc;

import static javax.swing.JOptionPane.showMessageDialog;

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

public class DataFolder {
	private static Path dataFolder = Main.dataFolder;
	
	public static Config config;
	
	private static Logger logger = Main.logger;
	
	
	public static void init() throws InterruptedException {
		Boolean update = false; 
		
		Path engdata = Paths.get(".SCR-RichPresence/eng.traineddata").toAbsolutePath();
		Path configfile = Paths.get(".SCR-RichPresence/config.txt").toAbsolutePath();

		if (Files.notExists(configfile)) {
			new File(dataFolder.toString()).mkdirs();
			File file = new File(configfile.toString());
			config = new Config(file);
			config.changeSetting("version", Main.version);
			config.saveAll();
		} else {
			File file = new File(configfile.toString());
			config = new Config(file);
			if (!Main.version.equals(config.getValue("version"))) {
				update = true;
				logger.severe("The data folder is on an older version. Copying engine data...");
				config.changeSetting("version", Main.version);
				config.saveAll();
			}
		}
		if (Files.notExists(engdata) || update) {
			InputStream tessdata = Main.class.getResourceAsStream("tessdata/eng.traineddata");
			try {
				Files.copy(tessdata, engdata, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.log(Level.SEVERE, e.getMessage(), e.getCause());
				showMessageDialog(null, e.getClass() + ": " + e.getCause() + "\n" + e.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
				Toolkit.getDefaultToolkit().beep();
				logger.info("Failed to copy OCR data. Please contact @Movies22");
				Thread.sleep(5000);
				System.exit(1);
				return;
			}
		}
	}
}
