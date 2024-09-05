package com.movies22.scr.rpc;

import com.movies22.scr.rpc.uix.SystemTray;
import com.movies22.scr.rpc.utils.CaptureUtils;
import com.movies22.scr.rpc.activities.Activities;
import com.movies22.scr.rpc.activities.Activity;
import com.movies22.scr.rpc.uix.App;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.WinDef.HWND;

import net.arikia.dev.drpc.DiscordUser;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class Main {
	
	public static SystemTray SystemTray;
	public static App application;
	public static IRPC rpc;
	
	public static String version = "Release-1.0";

	public static Tesseract ts;
	public static Tesseract sg_ts;
	
	public static long start;
	public static Logger logger;
	public static Path dataFolder;
	public static Screen mainWindow;
	public static Boolean debugDraw = false;
	
	public static DiscordUser user;
	
	public static Config config;
	
	public static Boolean allowJoining;
	public static Boolean showStatus;
	public static Boolean isJoinAvailable;
	public static HWND hWnd;
	
	public static Activity currentActivity;
	
	public static boolean DEVELOPER_MODE = false;
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws InterruptedException, TesseractException, IOException {
		// Initializes logging & data folder
		logger = Logger.getGlobal();
		dataFolder = Paths.get(".SCR-RichPresence").toAbsolutePath();
		DataFolder.init();
		config = DataFolder.config;

		FileHandler fh;
		logger.setUseParentHandlers(true);
		try {
			fh = new FileHandler(dataFolder.toString() + "/latest.log");
			logger.addHandler(fh);
			fh.setFormatter(new SimpleFormatter());
		} catch (SecurityException | IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e.getCause());
			Toolkit.getDefaultToolkit().beep();
			logger.info("Failed to create data folder");
			Thread.sleep(5000);
			System.exit(1);
		}
		
		allowJoining = false;
		showStatus = true;
		
		if(config.exists("allowJoining")) {
			if(config.getValue("allowJoining").equals("true")) allowJoining = true;
		};
		
		if(config.exists("showStatus")) {
			if(config.getValue("showStatus").equals("false")) showStatus = false;
		};
		
		SystemTray = new SystemTray();
		application = new App();
		
		//app & tray have been initialized. Sets up a shutdown thread to handle clean up once the program stops
		Runtime.getRuntime().addShutdownHook(new ShutdownHook());
		
		toggleJoiningAvailable(false);
		
		toggleJoining(allowJoining);
		toggleStatus(showStatus);
		
		// Enables fancy buttons.
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			logger.log(Level.SEVERE, e.getMessage(), e.getCause());
		}
		
		RPC.fetchUser();
		
		while(Main.user == null) {
			RPC.runCallbacks();
			Thread.sleep(16);
		}
		
		Thread.sleep(100);
		
		start = System.currentTimeMillis();
		
		logger.info("Initializing IPCClient");
		rpc = new IRPC();
		
		logger.info("Looking for a Roblox.exe window");

		WindowUtils.getAllWindows(true).forEach(desktopWindow -> {
		    if (desktopWindow.getTitle().equals("Roblox")) {
		    	hWnd = desktopWindow.getHWND();
		    }
		});
		logger.info("Found a Roblox.exe window with the following handle: " + hWnd);
		
		ts = new Tesseract();
		ts.setTessVariable("tessedit_char_whitelist",
				"0123456789:+-. _[]ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
		ts.setTessVariable("max_permuter_attempts", "750");
		ts.setDatapath(".SCR-RichPresence");

		
		sg_ts = new Tesseract();
		sg_ts.setTessVariable("tessedit_char_whitelist","0123456789CAMDSWLR ");
		sg_ts.setTessVariable("max_permuter_attempts", "750");
		sg_ts.setDatapath(".SCR-RichPresence");
		logger.info("Initialized Character Recognition thread");
		
		
		BufferedImage img;
		while(true) {
			if(hWnd == null) {
				WindowUtils.getAllWindows(true).forEach(desktopWindow -> {
				    if (desktopWindow.getTitle().equals("Roblox")) {
				    	hWnd = desktopWindow.getHWND();
				    	if(hWnd != null) logger.info("Found a Roblox.exe window with the following handle: " + hWnd);
				    }
				});
				Thread.sleep(1000);
				continue;
			}
			img = CaptureUtils.Capture(hWnd);
			if(img == null) {
				hWnd = null;
				continue;
			}
			Graphics c = img.getGraphics();
			currentActivity = null;
			for(Activities a : Activities.values()) {
				if(a.activity.checkStatus(img)) {
					currentActivity = a.activity;
				}
				//a.activity._debugDrawActivity(Color.red, c, img);
			}
			c.dispose();
			Main.logger.info("" + currentActivity);
			if(currentActivity != null) {
				rpc.updateStatus(currentActivity, img);
			}
			
			Thread.sleep(250);
			application.updateDebugImg(img);
		}
	}
	
	public static void toggleJoining(Boolean b) {
		Main.allowJoining = b;
		Main.application.toggleJoining(b);
		Main.SystemTray.toggleJoining(b);

	}
	
	public static void toggleJoiningAvailable(Boolean b) {
		Main.isJoinAvailable = b;
		Main.application.toggleJoiningAvailable(b);
		Main.SystemTray.toggleJoiningAvailable(b);
	}
	
	public static void toggleStatus(Boolean b) {
		Main.showStatus = b;
		Main.application.toggleStatus(b);
		Main.SystemTray.toggleStatus(b);
	}
}
