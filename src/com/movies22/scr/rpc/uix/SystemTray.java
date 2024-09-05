package com.movies22.scr.rpc.uix;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.movies22.scr.rpc.Main;

public class SystemTray {
	
	private Logger logger;
	
	private java.awt.SystemTray tray;
	private TrayIcon trayIcon;
	
	private CheckboxMenuItem status;
	private CheckboxMenuItem joining;
	
	public SystemTray() throws IOException {
		logger = Main.logger;
		if (!java.awt.SystemTray.isSupported()) {
            logger.severe("SystemTray is not supported");
            return;
        }
		
        final PopupMenu popup = new PopupMenu();
        Image trayImg = ImageIO.read(Main.class.getResourceAsStream("resources/tray.png"));
        trayIcon = new TrayIcon(trayImg, "SCR RichPresence", popup);
        tray = java.awt.SystemTray.getSystemTray();
       
        // Create a pop-up menu components
        MenuItem consoleItem = new MenuItem("Console");
        MenuItem creditsItem = new MenuItem("Credits");
        status = new CheckboxMenuItem("Toggle status");
        joining = new CheckboxMenuItem("Toggle joining");
        MenuItem windowItem = new MenuItem("Reveal Window");
        MenuItem exitItem = new MenuItem("Exit");
       
        status.setState(true);
        joining.setState(true);
      
        consoleItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Main.application.showConsole();
			}
        });
        
        creditsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Main.application.showCredits();
			}
        });
        
        
        status.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Main.showStatus = status.getState();
			}
        });
        
        joining.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Main.allowJoining = joining.getState();
			}
        });
        
        //Add components to pop-up menu
        popup.add(consoleItem);
        popup.add(creditsItem);
        popup.addSeparator();
        popup.add(status);
        popup.add(joining);
        popup.addSeparator();
        popup.add(windowItem);
        popup.add(exitItem);
        
        windowItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Main.application.showWindow();
			}
        });
        
        exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(1);
			}
        });
        
        trayIcon.setPopupMenu(popup);
        trayIcon.setImageAutoSize(true);
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
        }
	}
	
	public void shutdown() {
		tray.remove(trayIcon);
	}
	
	public void toggleJoining(Boolean b) {
		joining.setState(b);

	}
	
	public void toggleJoiningAvailable(Boolean b) {
		joining.setEnabled(b);
	}
	
	public void toggleStatus(Boolean b) {
		status.setState(b);
	}
}
