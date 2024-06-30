package com.movies22.scr.rpc;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.Native;
import com.sun.jna.Structure;

import com.movies22.scr.rpc.Screen.Position;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import static javax.swing.JOptionPane.showMessageDialog;

public class Main {
	public static Config config;
	private static Tesseract ts;
	private static Tesseract ts2;
	private static Robot robot;
	private static CurrentWindow status = CurrentWindow.UNKNOWN;
	private static Color LOAD = new Color(51, 51, 51);
	private static Color MAIN_MENU = new Color(154, 154, 154);
	private static Color GREY1 = new Color(104, 104, 104);
	private static Color QD = new Color(216, 67, 64);
	private static Color RED = new Color(255, 61, 61);
	private static Color VIP = new Color(127, 85, 0);
	private static Color SPAWN = new Color(0, 170, 255);
	private static Color WHITE = new Color(255, 255, 255);
	//private static Color ROBLOX = new Color(254, 254, 254);
	private static String version = "Beta-0.2";
	private static Boolean update = false;
	private static Boolean vip = false;

	public static App application;
	public static RPC rpc;
	public static Boolean overrideStatus = null;
	public static CurrentWindow overriden;

	public static long start = System.currentTimeMillis();
	public static Logger logger;
	public static Path dataFolder;
	public static Screen mainWindow;
	public static Boolean debugDraw = false;

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws InterruptedException, TesseractException {

		logger = Logger.getLogger("");
		dataFolder = Paths.get(".SCR-RichPresence").toAbsolutePath();
		overrideStatus = false;

		// Creates robot
		try {
			robot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			logger.log(Level.SEVERE, e.getMessage(), e.getCause());
			showMessageDialog(null, e.getClass() + ": " + e.getCause() + "\n" + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			Toolkit.getDefaultToolkit().beep();
			logger.info("Failed to start robot. Please contact @Movies22");
			Thread.sleep(5000);
			System.exit(1);
			return;
		}

		// Enables fancy buttons.
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			logger.log(Level.SEVERE, e.getMessage(), e.getCause());
		}

		// Handles data folder & engine data.
		Path engdata = Paths.get(".SCR-RichPresence/eng.traineddata").toAbsolutePath();
		Path configfile = Paths.get(".SCR-RichPresence/config.txt").toAbsolutePath();

		if (Files.notExists(configfile)) {
			new File(dataFolder.toString()).mkdirs();
			File file = new File(configfile.toString());
			config = new Config(file);
			config.changeSetting("version", version);
			config.saveAll();
		} else {
			File file = new File(configfile.toString());
			config = new Config(file);
			if (!version.equals(config.getValue("version"))) {
				update = true;
				logger.severe("The data folder is on an older version. Copying engine data...");
				config.changeSetting("version", version);
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

		FileHandler fh;
		logger.setUseParentHandlers(false);
		try {
			fh = new FileHandler(dataFolder.toString() + "/latest.log");
			logger.addHandler(fh);
			fh.setFormatter(new SimpleFormatter());
		} catch (SecurityException | IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e.getCause());
			showMessageDialog(null, e.getClass() + ": " + e.getCause() + "\n" + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			Toolkit.getDefaultToolkit().beep();
			logger.info("Failed to create data folder");
			Thread.sleep(5000);
			System.exit(1);
		}

		// Initialises RPC & application
		rpc = new RPC();
		application = new App(false); //set this to true to toggle dev mode
		application.spawn();

		application.updateStatus("Loading status...", "Playing in a public server");

		logger.info("Starting SCR-RichPresence version " + version);

		ts = new Tesseract();
		ts.setTessVariable("tessedit_char_whitelist",
				"0123456789:+-. _[]ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
		ts.setTessVariable("max_permuter_attempts", "750");
		ts.setDatapath(".SCR-RichPresence");
		
		ts2 = new Tesseract();
		ts2.setTessVariable("tessedit_char_whitelist",
				"0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZlatform ");
		ts2.setTessVariable("max_permuter_attempts", "750");
		ts2.setDatapath(".SCR-RichPresence");

		Rectangle screen;
		if (config.getValue("screen") != null) {
			String[] a = config.getValue("screen").split("/");
			if (a.length != 4) {
				screen = new Rectangle(0, 0, 1920, 1080);
				logger.warning("Attempted to load an invalid screen resolution: " + a.toString());
				config.deleteSetting("screen");
			} else {
				screen = new Rectangle(Integer.parseInt(a[0]), Integer.parseInt(a[1]), Integer.parseInt(a[2]),
						Integer.parseInt(a[3]));
			}
		} else {
			int hWnd = User32.instance.FindWindowA(null, "Roblox");
			if (hWnd == 0) {
				logger.warning("Couldn't find a Roblox.exe window");
				showMessageDialog(null,
						"Couldn't find a Roblox.exe window. Please make sure that you have roblox running (it can be running in the background) before starting the SCR RichPresence.",
						"Error", JOptionPane.ERROR_MESSAGE);
				Toolkit.getDefaultToolkit().beep();
				System.exit(1);
				return;
			}
			WindowInfo w = getWindowInfo(hWnd);
			logger.info("" + hWnd);
			logger.info(w.rect.toString());
			screen = new Rectangle(w.rect.left, w.rect.top, w.rect.right - w.rect.left, w.rect.bottom - w.rect.top);
		}

		logger.info(
				"Loaded a " + screen.width + " by " + screen.height + " screen at " + screen.x + ", " + screen.y + "!");

		logger.info("Fetching menu...");

		BufferedImage img;
		img = robot.createScreenCapture(screen);
		System.out.println(screen);
		mainWindow = new Screen(screen.width, screen.height, screen.x, screen.y);
		logger.info("Main menu has loaded!");

		// Vars & caches
		Graphics graphics = null;
		Roles currentRole = Roles.NONE;

		int menuButtonScale = mainWindow.getScale(0.275, true) - 5;
		int menuButtonGap = mainWindow.getScale(0.01, false) + 5;

		//71280 is a number obtained through trial and error and by comparing a HD with a 4K screen. DM me for more info - movies
		//nerdy stuff begins here
		int stationsOffset = 71280/screen.height; 
		stationsOffset += 66;
		int stationsGap = (int) Math.round(screen.height - (screen.height*0.10555) - 114);
		
		int stationsSize = 30 + 32400/screen.height;
		//nerdy stuff ends here (i hope)
		
		String name;
		String details;
		Operators curOperator = Operators.UNKNOWN;
		Operators curOperator2;
		String lastStop = "";
		String curStop = "";
		String curDSStop = "";
		Boolean loading = false;
		String currentHeadcode = "";
		String curStopA = "";
		String curStop2;
		String rank = "";
		String user = "";
		String zone = "";
		int message;
		
		// Main detection loop
		while (true) {
			img = robot.createScreenCapture(screen);
			message = Math.round(System.currentTimeMillis() - start) % 10000;
			if (debugDraw) {
				graphics = img.getGraphics();
				graphics.setColor(Color.red);

			} else {
				graphics = null;
			}
			if (checkPixel(img, -43, 9, VIP.getRGB(), Anchor.Horizontal.RIGHT, Anchor.Vertical.TOP,
					(debugDraw ? graphics : null))) {
				vip = true;
			}
			if (overrideStatus) {
				status = overriden;
			} else {
				if (checkPixel(img, 0, 0, LOAD.getRGB(), Anchor.Horizontal.LEFT, Anchor.Vertical.TOP,
						(debugDraw ? graphics : null))) {
					status = validate(CurrentWindow.LOADING, status);
				} else if (checkPixel(img, 158, -104, MAIN_MENU.getRGB(), Anchor.Horizontal.LEFT,
						Anchor.Vertical.CENTRE, (debugDraw ? graphics : null))) {
					status = validate(CurrentWindow.MAIN_MENU, status);
					currentRole = Roles.NONE;
				} else if (checkPixel(img, +menuButtonScale / 2 - 20, -menuButtonScale + 20, QD.getRGB(),
						Anchor.Horizontal.CENTRE, Anchor.Vertical.CENTRE, (debugDraw ? graphics : null))) {
					status = validate(CurrentWindow.SPAWN_MENU, status);
					if (status != CurrentWindow.SPAWN_MENU)
						currentRole = Roles.NONE;
				}
				if (status == CurrentWindow.SPAWN_MENU
						&& checkPixel(img, +menuButtonScale / 2 - 20, -menuButtonScale + 20, QD.getRGB(),
								Anchor.Horizontal.CENTRE, Anchor.Vertical.CENTRE, (debugDraw ? graphics : null))) {
					if (checkPixel(img, -menuButtonScale / 2 - menuButtonGap, -menuButtonScale + menuButtonGap / 2,
							SPAWN.getRGB(), Anchor.Horizontal.CENTRE, Anchor.Vertical.CENTRE,
							(debugDraw ? graphics : null))) {
						currentRole = Roles.PASSENGER;
					} else if (checkPixel(img, menuButtonScale / 2, -menuButtonScale + menuButtonGap / 2,
							SPAWN.getRGB(), Anchor.Horizontal.CENTRE, Anchor.Vertical.CENTRE,
							(debugDraw ? graphics : null))) {
						currentRole = Roles.DRIVER;
					} else if (checkPixel(img, 3 * menuButtonScale / 2 + menuButtonGap,
							-menuButtonScale + menuButtonGap / 2, SPAWN.getRGB(), Anchor.Horizontal.CENTRE,
							Anchor.Vertical.CENTRE, (debugDraw ? graphics : null))) {
						currentRole = Roles.DISPATCHER;
					} else if (checkPixel(img, -menuButtonScale / 2 - menuButtonGap, 3 * menuButtonGap / 2,
							SPAWN.getRGB(), Anchor.Horizontal.CENTRE, Anchor.Vertical.CENTRE,
							(debugDraw ? graphics : null))) {
						currentRole = Roles.GUARD;
					} else if (checkPixel(img, menuButtonScale / 2, 3 * menuButtonGap / 2, SPAWN.getRGB(),
							Anchor.Horizontal.CENTRE, Anchor.Vertical.CENTRE, (debugDraw ? graphics : null))) {
						currentRole = Roles.SIGNALLER;
					} else if (checkPixel(img, 3 * menuButtonScale / 2 + menuButtonGap, 3 * menuButtonGap / 2,
							SPAWN.getRGB(), Anchor.Horizontal.CENTRE, Anchor.Vertical.CENTRE,
							(debugDraw ? graphics : null))) {
						currentRole = Roles.STAFF;
					}
				}
				if (currentRole != Roles.NONE && status == CurrentWindow.SPAWN_MENU
						&& !checkPixel(img, +menuButtonScale / 2 - 20, -menuButtonScale + 20, QD.getRGB(),
								Anchor.Horizontal.CENTRE, Anchor.Vertical.CENTRE, (debugDraw ? graphics : null))) {
					switch (currentRole.toString()) {
					case "PASSENGER":
						status = CurrentWindow.EXPLORING;
						break;
					case "DRIVER":
						status = CurrentWindow.DRIVING;
						break;
					case "DISPATCHER":
						status = CurrentWindow.DISPATCHING;
						break;
					case "GUARD":
						status = CurrentWindow.GUARDING;
						break;
					case "SIGNALLER":
						status = CurrentWindow.SIGNALLING;
						break;
					case "STAFF":
						status = CurrentWindow.SUPERVISOR;
						break;
					}
				}
			}

			if (status == CurrentWindow.DRIVING) {
				curOperator2 = getOperator(img, 192, 60, Anchor.Horizontal.LEFT, Anchor.Vertical.TOP, curOperator);
				if (curOperator2 != Operators.UNKNOWN)
					curOperator = curOperator2;
				if (curOperator2 != Operators.UNKNOWN) {
					details = vip ? "In a private server" : "In a public server";
					name = "Selecting a route";
				} else {
					if (checkPixel(img, 118, -63, WHITE.getRGB(), Anchor.Horizontal.CENTRE, Anchor.Vertical.BOTTOM,
							(debugDraw ? graphics : null))) {
						if (lastStop != curStop)
							loading = false;
						Color check = new Color(getPixel(img, 109, -40, Anchor.Horizontal.CENTRE,
								Anchor.Vertical.BOTTOM, (debugDraw ? graphics : null)));
						String h = doOCR(img, -193, -77, 46, 19, Anchor.Horizontal.CENTRE, Anchor.Vertical.BOTTOM,
								(debugDraw ? graphics : null)).replaceAll("[^A-Z\\d]", "");

						if (h.length() < 4)
							h = currentHeadcode;
						if (h == "")
							continue;
						try {
							Integer.parseInt(h.substring(0, 1));
						} catch (Exception e) {
							continue;
						}
						currentHeadcode = Utils.parseHeadcode(h, curOperator);
						if (check.getRed() > 200 && check.getGreen() > 120 && check.getBlue() < 40) {
							loading = true;
							if (message < 5000) {
								curStop = doOCR(img, -295, -59, 245, 30, Anchor.Horizontal.CENTRE,
										Anchor.Vertical.BOTTOM, (debugDraw ? graphics : null));
								details = "Loading at " + Utils.shortify(curStop);
								lastStop = curStop;
							} else {
								curStop = doOCR(img, -108, -25, 30, 18, Anchor.Horizontal.CENTRE,
										Anchor.Vertical.BOTTOM, (debugDraw ? graphics : null));
								if (curStop.contains("00"))
									curStop = "00";
								try {
									int z = Integer.parseInt(curStop.replaceAll("\\+", "").replaceAll("\\n", ""));
									if (z < 1)
										curStop = "on time";
									else
										curStop = z + " min" + (z > 1 ? "s" : "") + " late";
									details = "Service running " + Utils.shortify(curStop);
									lastStop = curStop;
								} catch (Exception e) {
									continue;
								}
							}
						} else {
							if (loading) {
								loading = false;
								curStopA = "";
							}
							if (message < 5000) {
								curStop2 = doOCR(img, -295, -59, 245, 30, Anchor.Horizontal.CENTRE,
										Anchor.Vertical.BOTTOM, (debugDraw ? graphics : null));

								if (curStopA.equals("") || !curStop2.equals(curStop)) {
									curStopA = doOCR(img, -276, -23, 36, 14, Anchor.Horizontal.CENTRE,
											Anchor.Vertical.BOTTOM, (debugDraw ? graphics : null));
								}
								curStop = curStop2;
								details = "NS: " + Utils.shortify(curStop) + " @ " + curStopA;
							} else {
								details = "Service to " + Utils.getDest(currentHeadcode.charAt(1), curOperator);
							}
						}
						name = "Driving a"
								+ ((curOperator == Operators.AIRLINK || curOperator == Operators.EXPRESS) ? "n " : " ")
								+ curOperator.toString().toLowerCase() + " service as " + currentHeadcode.toUpperCase();

					} else {
						details = "Selecting a depot";
						name = "Driving a"
								+ ((curOperator == Operators.AIRLINK || curOperator == Operators.EXPRESS) ? "n " : " ")
								+ curOperator.toString().toLowerCase() + " service.";
					}
				}
				if (curOperator != Operators.UNKNOWN) {
					application.updateStatus(name, details, curOperator.name().toLowerCase(), curOperator.name);
				} else {
					application.updateStatus(name, details);
				}
			} else if (status == CurrentWindow.DISPATCHING) {
				if (checkPixel(img, 27, -29, MAIN_MENU.getRGB(), Anchor.Horizontal.LEFT, Anchor.Vertical.BOTTOM,
						(debugDraw ? graphics : null))) {
					BufferedImage stationimg = img.getSubimage(35, stationsOffset, 340, stationsGap);
					for (int i = 0; i < stationsGap; i++) {
						if (stationimg.getRGB(0, i) == GREY1.getRGB())
							i += stationsSize;
						if (i >= stationsGap)
							break;
						if (stationimg.getRGB(0, i) == LOAD.getRGB()) {
							for (int b = i; b > i - stationsSize+6; b--) {
								if (b > stationsGap || b < 0)
									break;
								if (stationimg.getRGB(0, b) != LOAD.getRGB())
									break;
								i--;
							}
							if (i + 10 + stationsSize-20 > stationsGap || i + 10 < 0)
								break;
							curDSStop = doOCRstatic(stationimg, 10, 10 + i, 320, stationsSize-20, (debugDraw ? graphics : null)).replaceAll("\\n", "");
							i = stationsGap;
						}
					}
					application.updateStatus("Selecting a station", "In a " + (vip ? "private" : "public") + " server");
				} else {
					//getting the pixels off the DS ui is unreliable due to weird scaling done with the UI. Using tesseract instead
					String plat = doOCR2(img, -160, -120, 159, 50, Anchor.Horizontal.RIGHT, Anchor.Vertical.CENTRE, (debugDraw ? graphics : null));
					if(plat.contains("atform")) { //small hack in case the text gets cut out
						application.updateStatus("Dispatching at " + Utils.shortify2(curDSStop), "Standing in platform " + plat.toLowerCase().split(" ")[1]);
					} else {
						application.updateStatus("Dispatching at " + Utils.shortify2(curDSStop), "In a " + (vip ? "private" : "public") + " server");
					}
				}
			} else if(status == CurrentWindow.GUARDING) {
				curStop = "";
				if(checkPixel(img, 174, 106, RED.getRGB(), Anchor.Horizontal.RIGHT, Anchor.Vertical.CENTRE, (debugDraw ? graphics : null))) {
					String t = doOCR(img, -175, 51, 175, 23, Anchor.Horizontal.RIGHT, Anchor.Vertical.CENTRE, (debugDraw ? graphics : null));
					if(rank == "") {
						rank = doOCR(img, -180, -66, 175, 23, Anchor.Horizontal.RIGHT, Anchor.Vertical.CENTRE, (debugDraw ? graphics : null));
						user = doOCR(img, -180, -85, 175, 23, Anchor.Horizontal.RIGHT, Anchor.Vertical.CENTRE, (debugDraw ? graphics : null));
					}
					if(rank.contains("Guard Manager")) rank = "GM";
					if(rank.contains("Senior Guard")) rank = "SGD";
					if(rank.contains("Senior Dispatcher")) rank = "SDS";
					if(rank.contains("Guard")) rank = "GD";
					if(rank.contains("Guest")) rank = "GUEST";
					if(rank.contains("Dispatcher")) rank = "DS";
					else {
						rank = rank.split("\\]")[0];
						rank = rank.split("\\[").length > 1 ? rank.split("\\[")[1] : rank;
						rank.replaceAll("6", "G");
					}
					application.updateStatus("Guarding [" + rank + "] " + user, "NS:" + Utils.shortify(t));
				} else {
					rank = "";
					application.updateStatus("Guarding in a " +(vip ? "private" : "public") + " server", "Selecting a train");
				}
				
			//SGs are a niche in the community so this was kept as 1920x1080
			//TODO: rewrite this in a future version (after 2.0 though)
			} else if(status == CurrentWindow.SIGNALLING) {
				Color col = new Color(img.getRGB(838, 40));
				if(col.getRed() > 220 && col.getGreen() > 210 && col.getBlue() > 210) {
					BufferedImage a = img.getSubimage(254, 17, 230, 40);
					zone = ts.doOCR(a);
					int trains = 0;
					for(int y = 454; y < 1033; y++) {
						Color check = new Color(img.getRGB(1737, y));
						if(((check.getRed() < 50 && check.getGreen() > 50) || (check.getRed() > 50 && check.getGreen() > 60) || (check.getRed() > 50 && check.getGreen() < 50)) && check.getBlue() < 50) {
							trains++;
							y += 39;
						}
					}
					if(zone.contains("Supervisor")) {
						application.updateStatus("Idling in the supervisor desk", trains + " train" + (trains == 1 ? "" : "s") + " in this zone.", "sg", "Signalling");
					} else {
						application.updateStatus("Signalling in " + zone, trains + " train" + (trains == 1 ? "" : "s") + " in this zone.", "sg", "Signalling");
						
					}
				} else {
					Color checkCol = new Color(img.getRGB(818, 84));
					if(checkCol.getBlue() > 160 && checkCol.getGreen() > 120 && checkCol.getRed() < 60) {
						BufferedImage camtxt = img.getSubimage(906, 90, 115, 38);
						String cam = ts2.doOCR(Utils.resize(camtxt, 115*2, 38*2));
						if(zone.contains("Supervisor")) {
							application.updateStatus("Idling in the supervisor desk", "Viewing " + cam, "sg", "Signalling");
						} else {
							application.updateStatus("Signalling in " + zone, "Viewing " + cam, "sg", "Signalling");
						}
					} else {
						application.updateStatus("Picking a signalling desk", "In a " + (vip ? "private" : "public") + " server.", "sg", "Signalling");
					}
				}
			}else {
				application.updateStatus(status.t, "In a " + (vip ? "private" : "public") + " server");
			}
			if (graphics != null) {
				application.updateDebugImg(img);
				graphics.dispose();
			}
			Thread.sleep(125);
		}

		
	}

	public static Boolean checkPixel(BufferedImage img, int x, int y, int col, Anchor.Horizontal ah, Anchor.Vertical av,
			Graphics... debug) {
		Position pos = mainWindow.getPixelAt(x, y, ah, av);
		int checkCol;
		try {
			checkCol = img.getRGB(pos.x, pos.y);
		} catch (ArrayIndexOutOfBoundsException e) {
			logger.log(Level.SEVERE, e.getMessage(), e.getCause());
			showMessageDialog(null, e.getClass() + ": " + e.getCause() + "\n" + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			Toolkit.getDefaultToolkit().beep();
			System.exit(1);
			return false;
		}
		if (debug[0] != null) {
			if (checkCol == col) {
				debug[0].setColor(Color.green);
			} else {
				debug[0].setColor(Color.red);
			}
			debug[0].drawRect(pos.x - 3, pos.y - 3, 9, 9);
		}
		return checkCol == col;
	};

	public static int getPixel(BufferedImage img, int x, int y, Anchor.Horizontal ah, Anchor.Vertical av,
			Graphics... debug) {
		Position pos = mainWindow.getPixelAt(x, y, ah, av);
		int checkCol;
		try {
			checkCol = img.getRGB(pos.x, pos.y);
		} catch (ArrayIndexOutOfBoundsException e) {
			logger.log(Level.SEVERE, e.getMessage(), e.getCause());
			showMessageDialog(null, e.getClass() + ": " + e.getCause() + "\n" + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			Toolkit.getDefaultToolkit().beep();
			System.exit(1);
			return 0;
		}
		if (debug[0] != null) {
			debug[0].setColor(Color.pink);
			debug[0].drawRect(pos.x - 3, pos.y - 3, 9, 9);
		}
		return checkCol;
	};

	public static CurrentWindow validate(CurrentWindow b, CurrentWindow a) {
		if (a == null)
			return b;
		if (b.v > a.v)
			return b;
		if (b.v == (a.v - 1))
			return b;
		if (b.v == 1)
			return b;
		return a;

	}

	public static void displayMessage(String message) throws AWTException {
		SystemTray tray = SystemTray.getSystemTray();

		Image image = Toolkit.getDefaultToolkit().createImage("icon.png");

		TrayIcon trayIcon = new TrayIcon(image, "Information");
		trayIcon.setImageAutoSize(true);
		trayIcon.setToolTip("SCR RichPresence notification");
		tray.add(trayIcon);

		trayIcon.displayMessage("SCR RichPresence", message, MessageType.INFO);
	}

	public static String doOCR(BufferedImage img, int x, int y, int sizeX, int sizeY, Anchor.Horizontal ah,
			Anchor.Vertical av, Graphics... debug) {
		Position pos = mainWindow.getPixelAt(x, y, ah, av);
		BufferedImage img2 = img.getSubimage(pos.x, pos.y, sizeX, sizeY);
		if (debug[0] != null) {
			debug[0].setColor(Color.pink);
			debug[0].drawRect(pos.x, pos.y, sizeX, sizeY);
		}
		try {
			return ts.doOCR(img2);
		} catch (TesseractException e) {
			e.printStackTrace();
			return "TESS_FAIL";
		}
	}
	
	public static String doOCR2(BufferedImage img, int x, int y, int sizeX, int sizeY, Anchor.Horizontal ah,
			Anchor.Vertical av, Graphics... debug) {
		Position pos = mainWindow.getPixelAt(x, y, ah, av);
		BufferedImage img2 = img.getSubimage(pos.x, pos.y, sizeX, sizeY);
		if (debug[0] != null) {
			debug[0].setColor(Color.pink);
			debug[0].drawRect(pos.x, pos.y, sizeX, sizeY);
		}
		try {
			return ts2.doOCR(img2);
		} catch (TesseractException e) {
			e.printStackTrace();
			return "TESS_FAIL";
		}
	}
	
	public static String doOCRstatic(BufferedImage img, int x, int y, int sizeX, int sizeY, Graphics... debug) {
		BufferedImage img2 = img.getSubimage(x, y, sizeX, sizeY);
		if (debug[0] != null) {
			debug[0].setColor(Color.pink);
			debug[0].drawRect(x, y, sizeX, sizeY);
		}
		try {
			return ts.doOCR(img2);
		} catch (TesseractException e) {
			e.printStackTrace();
			return "TESS_FAIL";
		}
	}

	public static Operators getOperator(BufferedImage img, int x, int y, Anchor.Horizontal ah, Anchor.Vertical av,
			Operators curOp) {
		Graphics graphics = img.getGraphics();
		if (checkPixel(img, x, y, Operators.AIRLINK.color.getRGB(), ah, av, (debugDraw ? graphics : null)))
			return Operators.AIRLINK;
		if (checkPixel(img, x, y, Operators.CONNECT.color.getRGB(), ah, av, (debugDraw ? graphics : null)))
			return Operators.CONNECT;
		if (checkPixel(img, x, y, Operators.EXPRESS.color.getRGB(), ah, av, (debugDraw ? graphics : null)))
			return Operators.EXPRESS;
		if (checkPixel(img, x, y, Operators.WATERLINE.color.getRGB(), ah, av, (debugDraw ? graphics : null)))
			return Operators.WATERLINE;
		if (checkPixel(img, x, y, Operators.SELECTING.color.getRGB(), ah, av, (debugDraw ? graphics : null)))
			return curOp;
		return Operators.UNKNOWN;
	}

	// Loads User32.dll to get the location & size of the roblox.exe window
	// TODO: add macOs support (p.s: if anyone can be bothered to add linux support
	// feel free to do a pull request, but i ain't doing that - movies22)
	public static interface WndEnumProc extends StdCallLibrary.StdCallCallback {
		boolean callback(int hWnd, int lParam);
	}

	public static interface User32 extends StdCallLibrary {
		public static final String SHELL_TRAY_WND = "Shell_TrayWnd";
		public static final int WM_COMMAND = 0x111;
		public static final int MIN_ALL = 0x1a3;
		public static final int MIN_ALL_UNDO = 0x1a0;

		final User32 instance = (User32) Native.load("user32", User32.class);

		boolean EnumWindows(WndEnumProc wndenumproc, int lParam);

		boolean IsWindowVisible(int hWnd);

		int GetWindowRect(int hWnd, RECT r);

		void GetWindowTextA(int hWnd, byte[] buffer, int buflen);

		int GetTopWindow(int hWnd);

		int GetWindow(int hWnd, int flag);

		boolean ShowWindow(int hWnd);

		boolean BringWindowToTop(int hWnd);

		int GetActiveWindow();

		boolean SetForegroundWindow(int hWnd);

		int FindWindowA(String winClass, String title);

		long SendMessageA(int hWnd, int msg, int num1, int num2);

		final int GW_HWNDNEXT = 2;
	}

	public static class RECT extends Structure {
		public int left, top, right, bottom;

		@Override
		protected List<String> getFieldOrder() {
			List<String> order = new ArrayList<>();
			order.add("left");
			order.add("top");
			order.add("right");
			order.add("bottom");
			return order;
		}
	}

	public static WindowInfo getWindowInfo(int hWnd) {
		RECT r = new RECT();
		User32.instance.GetWindowRect(hWnd, r);
		byte[] buffer = new byte[1024];
		User32.instance.GetWindowTextA(hWnd, buffer, buffer.length);
		String title = Native.toString(buffer);
		WindowInfo info = new WindowInfo(hWnd, r, title);
		return info;
	}

	public static class WindowInfo {
		int hwnd;
		RECT rect;
		String title;

		public WindowInfo(int hwnd, RECT rect, String title) {
			this.hwnd = hwnd;
			this.rect = rect;
			this.title = title;
		}

		public String toString() {
			return String.format("(%d,%d)-(%d,%d) : \"%s\"", rect.left, rect.top, rect.right, rect.bottom, title);
		}
	}
}
