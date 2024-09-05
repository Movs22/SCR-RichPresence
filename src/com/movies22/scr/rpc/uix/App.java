package com.movies22.scr.rpc.uix;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.FocusEvent.Cause;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import com.movies22.scr.rpc.Main;

public class App {
	
	private Logger logger;
	
	private JFrame window;
	
	private JFrame creditsWindow;

	private JFrame consoleWindow;
	
	private Thread consoleThread;
	
	private JLabel debugImage = null;
	
	private JFrame devWindow;
	
	public App() throws IOException {
		logger = Main.logger;
		
		window = new JFrame();
		window.setSize(300, 350);
		window.setResizable(false);
		window.setLocationRelativeTo(null);
		
		creditsWindow = new JFrame();
		creditsWindow.setSize(250, 125);
		creditsWindow.setLocationRelativeTo(null);
		creditsWindow.setResizable(false);
		creditsWindow.setBackground(Color.white);
		creditsWindow.setTitle("Credits");
		creditsWindow.setLayout(new GridLayout(3, 1, 0, 0));
		creditsWindow.add(new JLabel("Author: Movies22", SwingConstants.CENTER));
		creditsWindow.add(new JLabel("Licensed under the MIT License.", SwingConstants.CENTER));
		creditsWindow.add(new JLabel("© 2024 Movies22", SwingConstants.CENTER));
		InputStream r = Main.class.getResourceAsStream("resources/tray.png");
		try {
			creditsWindow.setIconImage(ImageIO.read(r));
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e.getCause());
		}
		creditsWindow.setTitle("Credits");
		
		consoleWindow = new JFrame();
		consoleWindow.setSize(1000, 400);
		JTextArea logs = new JTextArea();
		try {
			logs.read(new FileReader(Main.dataFolder.toString() + "/latest.log"), "Loading...");
		} catch (FileNotFoundException e1) {
			logger.log(Level.SEVERE, e1.getMessage(), e1.getCause());
		} catch (IOException e1) {
			logger.log(Level.SEVERE, e1.getMessage(), e1.getCause());
		}
		
		consoleThread = new Thread() {
			public void run() {
				while (true) {
					try {
						logs.read(new FileReader(Main.dataFolder.toString() + "/latest.log"), "Loading...");
					} catch (FileNotFoundException e1) {
						logger.log(Level.SEVERE, e1.getMessage(), e1.getCause());
					} catch (IOException e1) {
						logger.log(Level.SEVERE, e1.getMessage(), e1.getCause());
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						
					}
				}
			}
		};
		consoleThread.start();
		
		logs.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				logs.setEditable(true);
			}

			@Override
			public void focusGained(FocusEvent e) {
				logs.setEditable(false);

			}
		});
		r = Main.class.getResourceAsStream("resources/tray.png");
		try {
			consoleWindow.setIconImage(ImageIO.read(r));
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e.getCause());
		}
		consoleWindow.setTitle("Console");
		consoleWindow.add(new JScrollPane(logs));
		
		r = Main.class.getResourceAsStream("resources/tray.png");
		try {
			window.setIconImage(ImageIO.read(r));
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e.getCause());
		}
		window.setTitle("SCR RPC");
		
		/* TEMP */
		JButton button4 = new JButton();
		button4.setText("DEV VIEW");

		//buttonPanel2.add(button2);
		window.add(button4);
		debugImage = new JLabel();
		button4.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (devWindow != null) {
					devWindow.requestFocus(Cause.MOUSE_EVENT);
				} else {
					devWindow = new JFrame();
					devWindow.setSize(1920/2+100, 1080/2+100);
					devWindow.setResizable(true);
					devWindow.setVisible(true);
					devWindow.add(debugImage);
					devWindow.setSize(1920/2+100, 1080/2+100);
					devWindow.addWindowListener(new WindowAdapter() {

						@Override
						public void windowClosing(WindowEvent e) {
							devWindow = null;
						}

						@Override
						public void windowClosed(WindowEvent e) {
							devWindow = null;
						}

					});
				}
			}

		});
		
		
		window.setVisible(true);
		
	}
	
	public void updateDebugImg(BufferedImage img) {
		if(this.debugImage.getWidth() == 0) return;
		BufferedImage resizedImg = new BufferedImage(this.debugImage.getWidth(), this.debugImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = resizedImg.createGraphics();

	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.drawImage(img, 0, 0, resizedImg.getWidth(), resizedImg.getHeight(), null);
	    g2.dispose();
		
		this.debugImage.setIcon(new ImageIcon(resizedImg));
	}
	
	public void showCredits() {
		creditsWindow.setVisible(true);
	}
	
	public void showConsole() {
		consoleWindow.setVisible(true);
	}
	
	public void showWindow() {
		window.setVisible(true);
	}
	
	public void toggleJoining(Boolean b) {
		//joining.setState(b);
	}
	
	public void toggleJoiningAvailable(Boolean b) {
		//joining.setEnabled(b);
	}
	
	public void toggleStatus(Boolean b) {
		//status.setState(b);
	}
}
