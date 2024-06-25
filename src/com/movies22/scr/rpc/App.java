package com.movies22.scr.rpc;

import static javax.swing.JOptionPane.showMessageDialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.event.FocusEvent.Cause;
import java.awt.event.FocusListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class App {

	private Boolean dev;
	
	private JFrame mainWindow;
	private JFrame consoleWindow;
	private JFrame creditsWindow;
	private JFrame devWindow;
	private Logger logger;
	
	private JLabel status1;
	private JLabel status2;
	
	private Thread consoleThread;
	
	private JLabel debugImage = null;
	private RPC rpc;
	
	public App(Boolean d) {
		this.dev = d;
		this.logger = Main.logger;
		this.rpc = Main.rpc;
	}
	
	public void updateStatus(String title, String desc, String... imgKey) {
		status1.setText(title);
		status2.setText(desc);
		if(Main.overrideStatus) {
			rpc.updateStatus("Testing SCR-RichPresence", "in a dev environment", imgKey);
		} else {
			rpc.updateStatus(title, desc, imgKey);
		}
	}
	
	public void updateDebugImg(BufferedImage img) {
		this.debugImage.setIcon(new ImageIcon(Utils.resize(img, img.getWidth()/2, img.getHeight()/2)));
	}
	
	public void spawn() {
		mainWindow = new JFrame();
		if(dev) {
			mainWindow.setSize(350, 450);
		} else {
			mainWindow.setSize(300, 400);
		}
		mainWindow.setResizable(false);
		mainWindow.setLocationRelativeTo(null);
		mainWindow.getContentPane().setLayout(new BoxLayout(mainWindow.getContentPane(), BoxLayout.Y_AXIS));
		
		// Status frame
		JPanel statusFrame = new JPanel();
		statusFrame.setLayout(new BoxLayout(statusFrame, BoxLayout.Y_AXIS));
		JLabel statusT = new JLabel("Current status");
		status1 = new JLabel("Loading");
		status2 = new JLabel("status...");
		
		statusT.setFont(new Font("Arial", Font.BOLD, 25));
		statusT.setAlignmentX(Component.CENTER_ALIGNMENT);
		status1.setFont(new Font("Arial", Font.PLAIN, 16));
		status1.setAlignmentX(Component.CENTER_ALIGNMENT);
		status2.setFont(new Font("Arial", Font.PLAIN, 16));
		status2.setAlignmentX(Component.CENTER_ALIGNMENT);

		statusFrame.add(statusT);
		statusFrame.add(status1);
		statusFrame.add(status2);
		statusFrame.setPreferredSize(new Dimension(300, 100));
		
		// UNUSED so far
		JPanel textPanel = new JPanel();
		textPanel.setPreferredSize(new Dimension(300, 200));
		
		mainWindow.add(textPanel);
		
		JPanel buttonPanel = new JPanel();
		JButton button = new JButton();
		JButton button1 = new JButton();
		JButton button2 = new JButton();
		JButton button3 = new JButton();
		textPanel.setBackground(Color.BLACK);
		button.setText("Resize window");
		button1.setText("Credits");
		
		buttonPanel.add(button);
		buttonPanel.add(button1);
		
		if (dev) {
			buttonPanel.setLayout(new GridLayout(1, 2, 3, 3));
			buttonPanel.setPreferredSize(new Dimension(300, 50));
			JPanel buttonPanel2 = new JPanel();
			buttonPanel2.setLayout(new GridLayout(1, 3, 3, 3));
			buttonPanel2.setPreferredSize(new Dimension(300, 50));
			JButton button4 = new JButton();
			button2.setText("DEBUG");
			button3.setText("CONSOLE");
			button4.setText("DEV VIEW");

			buttonPanel2.add(button2);
			buttonPanel2.add(button3);
			buttonPanel2.add(button4);
			buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 1, 10));
			buttonPanel2.setBorder(BorderFactory.createEmptyBorder(1, 10, 10, 10));
			mainWindow.add(buttonPanel);
			mainWindow.add(buttonPanel2);
			debugImage = new JLabel();
			button4.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (devWindow != null) {
						devWindow.requestFocus(Cause.MOUSE_EVENT);
					} else {
						devWindow = new JFrame();
						devWindow.setSize(1920/2+100, 1080/2+100);
						devWindow.setResizable(false);
						devWindow.setVisible(true);
						devWindow.add(debugImage);
						devWindow.setSize(1920/2+100, 1080/2+100);
						Main.debugDraw = true;
						devWindow.addWindowListener(new WindowAdapter() {

							@Override
							public void windowClosing(WindowEvent e) {
								Main.debugDraw = false;
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
		} else {

			buttonPanel.setLayout(new GridLayout(2, 2, 3, 3));
			buttonPanel.setPreferredSize(new Dimension(300, 100));
			button2.setText("Debug mode");
			button3.setText("Open console");

			buttonPanel.add(button);
			buttonPanel.add(button1);
			buttonPanel.add(button2);
			buttonPanel.add(button3);
			buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			mainWindow.add(buttonPanel);
		}

		if(dev) {
			JPanel devPanel = new JPanel();
			devPanel.setLayout(new GridLayout(2, 1, 3, 3));
			devPanel.setPreferredSize(new Dimension(300, 50));
			JLabel title = new JLabel("DEVELOPER | Override activity");
			devPanel.add(title);
			JPanel devRowPanel = new JPanel();
			devRowPanel.setLayout(new GridLayout(1, 2, 3, 3));
			JComboBox<Object> statusMenu = new JComboBox<Object>(CurrentWindow.values());
			JButton overrideButton = new JButton();
			overrideButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					CurrentWindow sel = (CurrentWindow) statusMenu.getSelectedItem();
					if(sel == CurrentWindow.UNKNOWN) {
						Main.overrideStatus = false;
					} else {
						Main.overrideStatus = true;
						Main.overriden = sel;
					}
				}
			});
			
			overrideButton.setText("Override");
			devRowPanel.add(statusMenu);
			devRowPanel.add(overrideButton);
			devPanel.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
			devPanel.add(devRowPanel);
			
			mainWindow.add(devPanel);
		}
		
		mainWindow.add(statusFrame);
		// statusFrame.setBackground(Color.RED);
		// window.getContentPane().add(button1);
		// window.getContentPane().add(button2);
		
		button1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (creditsWindow != null) {
					creditsWindow.requestFocus(Cause.MOUSE_EVENT);
				} else {
					creditsWindow = new JFrame();
					creditsWindow.setSize(200, 100);
					creditsWindow.setLocationRelativeTo(null);
					creditsWindow.setResizable(false);
					creditsWindow.setBackground(Color.white);
					creditsWindow.setTitle("Credits");
					creditsWindow.setLayout(new GridLayout(3, 1, 0, 0));
					creditsWindow.add(new JLabel("Author: Movies22", SwingConstants.CENTER));
					creditsWindow.add(new JLabel("Licensed under the MIT License.", SwingConstants.CENTER));
					creditsWindow.add(new JLabel("© 2024 Movies22", SwingConstants.CENTER));
					creditsWindow.setVisible(true);

				}
			}

		});
		
		button3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (consoleWindow != null) {
					consoleWindow.requestFocus(Cause.MOUSE_EVENT);
				} else {
					consoleWindow = new JFrame();
					consoleWindow.setSize(1000, 400);
					JTextArea logs = new JTextArea();
					try {
						logs.read(new FileReader(Main.dataFolder.toString() + "latest.log"), "Loading...");
					} catch (FileNotFoundException e1) {
						logger.log(Level.SEVERE, e1.getMessage(), e1.getCause());
					} catch (IOException e1) {
						logger.log(Level.SEVERE, e1.getMessage(), e1.getCause());
					}
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
					consoleWindow.setTitle("SCR RichPresence - Console");
					consoleWindow.add(new JScrollPane(logs));
					consoleWindow.addWindowListener(new WindowAdapter() {

						@Override
						public void windowClosing(WindowEvent e) {
							consoleThread.interrupt();
							consoleWindow = null;
						}

						@Override
						public void windowClosed(WindowEvent e) {
							consoleWindow = null;
						}

					});
					consoleWindow.setVisible(true);
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

				}
			}

		});
		
		mainWindow.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				showMessageDialog(null, "Closing this window will stop the rich presence.", "Alert",
						JOptionPane.ERROR_MESSAGE);
				Toolkit.getDefaultToolkit().beep();
				System.exit(1);
			}

			@Override
			public void windowClosed(WindowEvent e) {
				System.exit(1);
			}

		});
		InputStream r = Main.class.getResourceAsStream("resources/icon.png");
		try {
			mainWindow.setIconImage(ImageIO.read(r));
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e.getCause());
		}
		mainWindow.setTitle("SCR RichPresence");
		mainWindow.setVisible(true);
	}
}
