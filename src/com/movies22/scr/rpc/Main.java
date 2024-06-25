package com.movies22.scr.rpc;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusEvent.Cause;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.movies22.scr.rpc.Screen.Position;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.sourceforge.tess4j.Tesseract;

import static javax.swing.JOptionPane.showMessageDialog;

public class Main {
	public static Config config;
	private static Tesseract ts;
	private static Tesseract ts2;
	private static Robot robot;
	private static CurrentWindow status;
	private static Color LOAD = new Color(51, 51, 51);
	private static Color MAIN_MENU = new Color(154, 154, 154);
	private static Color GREY1 = new Color(104, 104, 104);
	private static Color QD = new Color(216, 67, 64);
	private static Color RED = new Color(255, 61, 61);
	private static Color VIP = new Color(127, 85, 0);
	private static Color SPAWN = new Color(0, 170, 255);
	private static Color WHITE = new Color(255, 255, 255);
	private static Color ROBLOX = new Color(254, 254, 254);
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
	public static void main(String[] args) throws InterruptedException {
		
		logger = Logger.getLogger("");
		dataFolder = Paths.get(".SCR-RichPresence").toAbsolutePath();
		overrideStatus = false;
		
		//Creates robot
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
		
		//Enables fancy buttonns.
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			logger.log(Level.SEVERE, e.getMessage(), e.getCause());
		}
		
		//Handles data folder & engine data.
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
		
		//Initialises RPC & application
		rpc = new RPC();
		application = new App(true);
		application.spawn();
		
		application.updateStatus("Loading status...", "Playing in a public server");
		

		logger.info("Starting SCR-RichPresence version " + version);
		
		ts = new Tesseract();
		ts.setTessVariable("tessedit_char_whitelist",
				"0123456789:+-. _[]ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
		ts.setTessVariable("max_permuter_attempts", "750");
		ts.setDatapath(".SCR-RichPresence");
		
		Rectangle screen;
		if(config.getValue("screen") != null) {
			String[] a = config.getValue("screen").split("/");
			if(a.length != 4) {
				screen = new Rectangle(0, 0, 1920, 1080);
				logger.warning("Attempted to load an invalid screen resolution: " + a.toString());
				config.deleteSetting("screen");
			} else {
				screen = new Rectangle(Integer.parseInt(a[0]), Integer.parseInt(a[1]), Integer.parseInt(a[2]), Integer.parseInt(a[3]));
			}
		} else {
			screen = new Rectangle(0, 0, 1920, 1080);
		}
		
		logger.info("Loaded a " + screen.width + " by " + screen.height + " screen at " + screen.x + ", " + screen.y + "!");
		
		logger.info("Fetching menu...");
		
		BufferedImage img;
		img = robot.createScreenCapture(screen);
		mainWindow = new Screen(screen.width, screen.height, screen.x, screen.y);
		logger.info("Main menu has loaded!");
		
		//Vars & caches
		Graphics graphics = null;
		
		//Main detection loop
		while(true) {
			if(debugDraw) {
				graphics = img.getGraphics();
				graphics.setColor(Color.red);
				
			} else {
				graphics = null;
			}
			if (checkPixel(img, -43, 0, VIP.getRGB(), Anchor.Horizontal.RIGHT, Anchor.Vertical.TOP, (debugDraw ? graphics : null))) {
				vip = true;
			}
			if(overrideStatus) {
				status = overriden;
			} else {
				if (checkPixel(img, 0, 0, LOAD.getRGB(), Anchor.Horizontal.LEFT, Anchor.Vertical.TOP, (debugDraw ? graphics : null))) {
					status = validate(CurrentWindow.LOADING, status);
				} else if (checkPixel(img, 158, -104, MAIN_MENU.getRGB(), Anchor.Horizontal.LEFT, Anchor.Vertical.CENTRE, (debugDraw ? graphics : null))) {
					status = validate(CurrentWindow.MAIN_MENU, status);
				} else if (img.getRGB(1772, 670) == WHITE.getRGB()) {
					status = validate(CurrentWindow.DISPATCHING, status);
				} else if (img.getRGB(1758, 657) == WHITE.getRGB()) {
					status = validate(CurrentWindow.GUARDING, status);
				} else if (img.getRGB(1240, 1028) == WHITE.getRGB()) {
					status = validate(CurrentWindow.DRIVING, status);
				} else {
					status = CurrentWindow.UNKNOWN;
				}
				//status = CurrentWindow.UNKNOWN;
			}
			application.updateStatus(status.t, "In a " + (vip ? "private" : "public") + " server");
			if(graphics != null) {
				application.updateDebugImg(img);
				graphics.dispose();
			}
			img = robot.createScreenCapture(screen);
			Thread.sleep(500);
		}
		
		/*
				Operators curOperator = Operators.UNKNOWN;
				Operators curOperator2 = Operators.UNKNOWN;
				Boolean vip = false;
				Boolean warned = false;
				Boolean loading = false;
				String lastStop = null;
				String curStop = "";
				String curStop2 = "";
				String curStopA = "";
				String rank = "";
				String user = "";
				String zone = "";
				String currentStop = "";
				String currentDest = "";
				String currentHeadcode = "";
				String selRank = "";
				long lastCheck = 0L;
				long message = 0;
				while (true) {
					message = (System.currentTimeMillis() - start) % 10000;
					img = robot.createScreenCapture(screen);
					if(DEBUG_DRAW) debugGraphics = img.getGraphics();
					if(DEBUG_DRAW) debugGraphics.setColor(Color.RED);
					if (img.getRGB(27, 11) != ROBLOX.getRGB() && img.getRGB(27, 11) != WHITE.getRGB()) {
						if (lastCheck == 0L)
							lastCheck = System.currentTimeMillis();
						logger.info("User isn't playing roblox anymore.");
						DiscordRPC.discordClearPresence();
						Thread.sleep(2000);
						continue;
					} else {
						lastCheck = 0L;
					}
					if (img.getRGB(995, 947) == LOAD.getRGB()) {
						status = validate(CurrentWindow.LOADING, status);
					} else if (img.getRGB(158, 436) == MAIN_MENU.getRGB()) {
						selRank = null;
						status = validate(CurrentWindow.MAIN_MENU, status);
					} else if (img.getRGB(543, 1023) == WHITE.getRGB()) {
						status = validate(CurrentWindow.DRIVING, status);
					} else if (img.getRGB(1772, 670) == WHITE.getRGB()) {
						status = validate(CurrentWindow.DISPATCHING, status);
					} else if (img.getRGB(1758, 657) == WHITE.getRGB()) {
						status = validate(CurrentWindow.GUARDING, status);
					} else if (img.getRGB(1240, 1028) == WHITE.getRGB()) {
						status = validate(CurrentWindow.DRIVING, status);
					} /*
						 * else if(sgcol.getRed() > 220 && sgcol.getGreen() > 210 && sgcol.getBlue() >
						 * 210 && status == CurrentWindow.SPAWN_MENU) { status =
						 * validate(CurrentWindow.SIGNALLING, status); }
						 */ /* else if (img.getRGB(862, 303) == QD.getRGB()) {
						status = validate(CurrentWindow.SPAWN_MENU, status);
						curOperator = Operators.UNKNOWN;
					} else if (img.getRGB(396, 1008) == SPAWN.getRGB()
							&& (curOperator == null || curOperator == Operators.UNKNOWN) && selRank == null) {
						status = validate(CurrentWindow.SPAWN_MENU, status);
					}
					if (status == CurrentWindow.SPAWN_MENU && img.getRGB(24, 1008) == MAIN_MENU.getRGB()) {
						if (img.getRGB(789, 248) == SPAWN.getRGB()) {
							selRank = "Passenger";
						} else if (img.getRGB(1108, 248) == SPAWN.getRGB()) {
							selRank = "Driver";
						} else if (img.getRGB(1428, 248) == SPAWN.getRGB()) {
							selRank = "Dispatcher";
						} else if (img.getRGB(789, 567) == SPAWN.getRGB()) {
							selRank = "Guard";
						} else if (img.getRGB(1108, 567) == SPAWN.getRGB()) {
							selRank = "Signaller";
						} else if (img.getRGB(1428, 567) == SPAWN.getRGB()) {
							selRank = "Staff";
						}
					} else if (status == CurrentWindow.SPAWN_MENU && selRank != null) {
						switch (selRank) {
						case "Passenger":
							status = validate(CurrentWindow.EXPLORING, status);
							break;
						case "Signaller":
							status = validate(CurrentWindow.SIGNALLING, status);
							break;
						case "Staff":
							status = validate(CurrentWindow.SUPERVISOR, status);
							break;
						}
					}

					if (img.getRGB(1877, 9) == VIP.getRGB()) {
						vip = true;
					}
					try {
						if (status == CurrentWindow.SPAWN_MENU && img.getRGB(743, 1008) == GREY1.getRGB()) {
							BufferedImage stationimg = img.getSubimage(30, 134, 335, 845);
							BufferedImage curimg = null;
							for (int i = 0; i < 845; i++) {
								if (stationimg.getRGB(0, i) == GREY1.getRGB())
									i += 65;
								if (i >= 845)
									break;
								if (stationimg.getRGB(0, i) == LOAD.getRGB()) {
									for (int b = i; b > i - 66; b--) {
										if (b > 845 || b < 0)
											break;
										if (stationimg.getRGB(0, b) != LOAD.getRGB())
											break;
										i--;
									}
									if (i + 11 + 35 > 845 || i + 11 < 0)
										break;
									curimg = stationimg.getSubimage(10, 11 + i, 315, 35);
									currentStop = ts.doOCR(curimg).replaceAll("\\n", "");
									if(DEBUG_DRAW) debugGraphics.drawRect(10, 11 + i, 315, 35);
									i = 845;
								}
							}
						}
						DiscordRPC.discordRunCallbacks();
						if (status != null) {
							DiscordRichPresence.Builder presence;
							if (status == CurrentWindow.DRIVING) {
								curOperator2 = curOperator.getOperator(img.getRGB(192, 60), curOperator);
								if (curOperator2 != Operators.UNKNOWN)
									curOperator = curOperator2;
								if (curOperator2 != Operators.UNKNOWN) {
									presence = new DiscordRichPresence.Builder(
											vip ? "In a private server" : "In a public server");
									presence.setDetails("Selecting a route");
									presence.setStartTimestamps(start);
									currentHeadcode = "";
									currentDest = "";
									presence.setBigImage("scrlogo", "SCR 1.10.13");
									// presence.setSmallImage("logo", curOperator.name);
									status1.setText("Selecting a route");
									status2.setText(vip ? "In a private server" : "In a public server");
									DiscordRPC.discordUpdatePresence(presence.build());
								} else {
									if (img.getRGB(1078, 1017) == WHITE.getRGB()) {
										if (lastStop != curStop)
											loading = false;
										Color check = new Color(img.getRGB(1069, 1040));
										BufferedImage y = img.getSubimage(767, 1003, 46, 19);
										String h = ts2.doOCR(Utils.resize(y, 46 * 2, 19 * 2)).replaceAll("[^A-Z\\d]",
												"");
										if(DEBUG_DRAW) debugGraphics.drawRect(767, 1003, 46, 19);
										
										if (h.length() < 4)
											h = currentHeadcode;
										if (h == "")
											continue;
										try {
											Integer.parseInt(h.substring(0, 1));
										} catch (Exception e) {
											continue;
										}
										currentHeadcode = parseHeadcode(h, curOperator);
										if (check.getRed() > 200 && check.getGreen() > 120 && check.getBlue() < 40) {
											loading = true;
											if (message < 5000) {
												BufferedImage a = img.getSubimage(665, 1021, 245, 30);
												if(DEBUG_DRAW) debugGraphics.drawRect(665, 1021, 245, 30);
												curStop = ts.doOCR(a);
												presence = new DiscordRichPresence.Builder(
														"Loading at " + shortify(curStop));
												lastStop = curStop;
											} else {
												BufferedImage a = img.getSubimage(852, 1055, 30, 18);
												if(DEBUG_DRAW) debugGraphics.drawRect(852, 1055, 30, 18);
												curStop = ts.doOCR(a);
												if (curStop.contains("00"))
													curStop = "00";
												try {
													int z = Integer.parseInt(
															curStop.replaceAll("\\+", "").replaceAll("\\n", ""));
													if (z < 1)
														curStop = "on time";
													else
														curStop = z + " min" + (z > 1 ? "s" : "") + " late";
													presence = new DiscordRichPresence.Builder(
															"Service running " + shortify(curStop));
													lastStop = curStop;
												} catch (Exception e) {
													continue;
												}
											}
										} else {
											if (loading) {
												loading = false;
												curStopA = "";
												Thread.sleep(1000);
											}
											BufferedImage c;
											if (message < 5000) {
												BufferedImage a = img.getSubimage(665, 1021, 245, 30);
												if(DEBUG_DRAW) debugGraphics.drawRect(665, 1021, 245, 30);
												curStop2 = ts.doOCR(a);
												if (curStopA.equals("") || !curStop2.equals(curStop)) {
													c = img.getSubimage(684, 1057, 36, 14);
													curStopA = ts.doOCR(c);
												}
												curStop = curStop2;
												presence = new DiscordRichPresence.Builder(
														"NS: " + shortify(curStop) + " @ " + curStopA);
											} else {
												switch (currentHeadcode.toLowerCase().charAt(1)) {
												case 'a':
													currentDest = "Airport Central";
													break;
												case 'b':
													currentDest = "Benton";
													break;
												case 'c':
													currentDest = "Beechley";
													break;
												case 'd':
													currentDest = "Willowfield";
													break;
												case 'e':
													currentDest = "Edgemead";
													break;
												case 'f':
													currentDest = "Whitefield";
													break;
												case 'g':
													currentDest = "Greenfield";
													break;
												case 'h':
													currentDest = "Newry Harbour";
													break;
												case 'i':
													currentDest = "St Helens Bridge";
													break;
												case 'j':
													currentDest = "Farleigh";
													break;
												case 'k':
													currentDest = "Leighton West";
													break;
												case 'l':
													currentDest = "Llyn-by-the-Sea";
													break;
												case 'm':
													currentDest = "Morganstown";
													break;
												case 'n':
													currentDest = "Newry";
													break;
												case 'o':
													currentDest = "Connolly";
													break;
												case 'p':
													currentDest = curOperator == Operators.AIRLINK ? "Airport Parkway"
															: "Port Benton";
													break;
												case 'q':
													currentDest = "Esterfield";
													break;
												case 'r':
													currentDest = "Leighton Stepford Road";
													break;
												case 's':
													currentDest = "Stepford Central";
													break;
												case 't':
													currentDest = "Leighton City";
													break;
												case 'u':
													currentDest = "Stepford UFC";
													break;
												case 'v':
													currentDest = "Stepford Victoria";
													break;
												case 'w':
													currentDest = "Westwyvern";
													break;
												case 'x':
													currentDest = "Terminal 2";
													break;
												case 'y':
													currentDest = "Berrily";
													break;
												case 'z':
													currentDest = "Terminal 3";
													break;
												default:
													currentDest = "Unknown";
													break;
												}
												presence = new DiscordRichPresence.Builder(
														"Service to " + shortify(currentDest));
											}
										}
										presence.setDetails("Driving a"
												+ ((curOperator == Operators.AIRLINK
														|| curOperator == Operators.EXPRESS) ? "n " : " ")
												+ curOperator.toString().toLowerCase() + " service as "
												+ currentHeadcode.toUpperCase());

									} else {
										presence = new DiscordRichPresence.Builder("Selecting a depot");
										presence.setDetails("Driving a"
												+ ((curOperator == Operators.AIRLINK
														|| curOperator == Operators.EXPRESS) ? "n " : " ")
												+ curOperator.toString().toLowerCase() + " service.");
									}
									presence.setStartTimestamps(start);
									presence.setBigImage("scrlogo", "SCR 1.10.13");
									presence.setSmallImage(curOperator.name().toLowerCase(), curOperator.name);
									status1.setText(presence.build().details);
									status2.setText(presence.build().state);

									DiscordRPC.discordUpdatePresence(presence.build());
								}
								rank = "";
							} else if (status == CurrentWindow.DISPATCHING) {
								Color col = new Color(img.getRGB(1810, 497));
								if ((col.getRed() < 100) && (col.getGreen() > 160) && (col.getBlue() > 160)) {
									BufferedImage headcode = img.getSubimage(1798, 476, 65, 30);
									BufferedImage plat = img.getSubimage(1755, 434, 144, 30);
									if(DEBUG_DRAW) debugGraphics.drawRect(1798, 476, 65, 30);
									if(DEBUG_DRAW) debugGraphics.drawRect(1755, 434, 144, 30);
									presence = new DiscordRichPresence.Builder(
											"Dispatching " + ts2.doOCR(headcode).replaceAll("\\n", "").toUpperCase()
													+ " at " + ts.doOCR(plat).replaceAll("\\n", ""));
									status2.setText(
											"Dispatching " + ts2.doOCR(headcode).replaceAll("\\n", "").toUpperCase()
													+ " at " + ts.doOCR(plat).replaceAll("\\n", ""));
								} else {
									presence = new DiscordRichPresence.Builder(
											vip ? "In a private server" : "In a public server");
									status2.setText(vip ? "In a private server" : "In a public server");
								}
								presence.setDetails("Dispatching at " + shortify2(currentStop));
								presence.setStartTimestamps(start);
								presence.setBigImage("scrlogo", "SCR 1.10.13");
								presence.setSmallImage("ds", "Dispatching");
								status1.setText("Dispatching at " + shortify2(currentStop));
								DiscordRPC.discordUpdatePresence(presence.build());
							} else if (status == CurrentWindow.GUARDING) {
								curStop = "";
								if (img.getRGB(1746, 646) == RED.getRGB()) {
									BufferedImage a = img.getSubimage(1745, 591, 175, 23);
									if(DEBUG_DRAW) debugGraphics.drawRect(1745, 591, 175, 23);
									String t = ts.doOCR(a);
									if(DEBUG_DRAW) debugGraphics.drawRect(1740, 474, 175, 23);
									if(DEBUG_DRAW) debugGraphics.drawRect(1745, 455, 175, 31);
									if (rank == "") {
										BufferedImage b = img.getSubimage(1740, 476, 175, 20);
										rank = ts.doOCR(b);
										BufferedImage c = img.getSubimage(1745, 455, 175, 25);
										user = ts.doOCR(c);
									}
									if (rank.contains("Guard Manager"))
										rank = "GM";
									if (rank.contains("Senior Guard"))
										rank = "SGD";
									if (rank.contains("Senior Dispatcher"))
										rank = "SDS";
									if (rank.contains("Guard"))
										rank = "GD";
									if (rank.contains("Guest"))
										rank = "GUEST";
									if (rank.contains("Dispatcher"))
										rank = "DS";
									else {
										rank = rank.split("\\]")[0];
										rank = rank.split("\\[").length > 1 ? rank.split("\\[")[1] : rank;
										rank.replaceAll("6", "G");
									}
									presence = new DiscordRichPresence.Builder("NS: " + shortify(t));
									presence.setDetails("Guarding [" + rank + "] " + user);
									status1.setText("Guarding [" + rank + "] " + user);
									status2.setText("NS: " + shortify(t));
								} else {
									rank = "";
									presence = new DiscordRichPresence.Builder("Selecting a train");
									presence.setDetails("Guarding in a " + (vip ? "private" : "public") + " server.");
									status1.setText("Selecting a train");
									status2.setText("Guarding in a " + (vip ? "private" : "public") + " server.");
								}
								presence.setStartTimestamps(start);
								presence.setBigImage("scrlogo", "SCR 1.10.13");
								presence.setSmallImage("gd", "Guarding");
								DiscordRPC.discordUpdatePresence(presence.build());
							} else if (status == CurrentWindow.SIGNALLING) {
								Color col = new Color(img.getRGB(838, 40));
								if (col.getRed() > 220 && col.getGreen() > 210 && col.getBlue() > 210) {
									BufferedImage a = img.getSubimage(254, 17, 230, 40);
									if(DEBUG_DRAW) debugGraphics.drawRect(254, 17, 230, 40);
									zone = ts.doOCR(a);
									int trains = 0;
									for (int y = 454; y < 1033; y++) {
										Color check = new Color(img.getRGB(1737, y));
										if (((check.getRed() < 50 && check.getGreen() > 50)
												|| (check.getRed() > 50 && check.getGreen() > 60)
												|| (check.getRed() > 50 && check.getGreen() < 50))
												&& check.getBlue() < 50) {
											trains++;
											y += 39;
										}
									}
									presence = new DiscordRichPresence.Builder(
											trains + " train" + (trains == 1 ? "" : "s") + " in this zone.");
									if (zone.contains("Supervisor")) {
										presence.setDetails("Idling in the supervisor desk");
									} else if (!zone.contains("Zone") && !zone.contains("Supervisor")) {
										presence.setDetails("Checking the trains list");
									} else {
										presence.setDetails("Signalling in " + zone);
									}
									status1.setText(presence.build().details);
									status2.setText(presence.build().state);
									presence.setStartTimestamps(start);
									presence.setBigImage("scrlogo", "SCR 1.10.13");
									presence.setSmallImage("sg", "Signalling");
									DiscordRPC.discordUpdatePresence(presence.build());
								} else {
									Color checkCol = new Color(img.getRGB(818, 84));
									if (checkCol.getBlue() > 160 && checkCol.getGreen() > 120
											&& checkCol.getRed() < 60) {
										BufferedImage camtxt = img.getSubimage(906, 90, 115, 38);
										if(DEBUG_DRAW) debugGraphics.drawRect(906, 90, 115, 38);
										String cam = ts2.doOCR(Utils.resize(camtxt, 115 * 2, 38 * 2));
										presence = new DiscordRichPresence.Builder("Viewing " + cam);
										if (zone.contains("Supervisor")) {
											presence.setDetails("Idling in the supervisor desk");
										} else if (!zone.contains("Zone") && !zone.contains("Supervisor")) {
											presence.setDetails("Checking the trains list");
										} else {
											presence.setDetails("Signalling in " + zone);
										}
										presence.setStartTimestamps(start);
										presence.setBigImage("scrlogo", "SCR 1.10.13");
										presence.setSmallImage("sg", "Signalling");
										status1.setText(presence.build().details);
										status2.setText(presence.build().state);
										DiscordRPC.discordUpdatePresence(presence.build());
									} else {
										zone = "";
										presence = new DiscordRichPresence.Builder(
												vip ? "In a private server" : "In a public server");
										presence.setDetails("Idling in the signaller role");
										presence.setStartTimestamps(start);
										presence.setBigImage("scrlogo", "SCR 1.10.13");
										presence.setSmallImage("sg", "Signalling");
										status1.setText(presence.build().details);
										status2.setText(presence.build().state);
										DiscordRPC.discordUpdatePresence(presence.build());
									}
								}
							} else {
								presence = new DiscordRichPresence.Builder(
										vip ? "In a private server" : "In a public server");
								presence.setDetails(status.t);
								presence.setStartTimestamps(start);
								presence.setBigImage("scrlogo", "SCR 1.10.13");
								status1.setText(presence.build().details);
								status2.setText(presence.build().state);
								DiscordRPC.discordUpdatePresence(presence.build());
								curOperator = Operators.UNKNOWN;
							}
						} else {
							if (!warned) {
								displayMessage(
										"Failed to detect your current activity. Whenever possible, please go back to the main/role selection menu.");
								Toolkit.getDefaultToolkit().beep();
								logger.warning(
										"Failed to detect your current activity. Please go back to the main menu.");
								warned = true;
							}
							DiscordRichPresence.Builder presence = new DiscordRichPresence.Builder(
									"Playing in a " + (vip ? "private" : "public") + " server");
							presence.setDetails("Status couldn't be loaded");
							status1.setText(presence.build().details);
							status2.setText(presence.build().state);
							presence.setStartTimestamps(start);
							DiscordRPC.discordUpdatePresence(presence.build());
						}
						if(DEBUG_DRAW) debugimg.setIcon(new ImageIcon(Utils.resize(img, 1920/2, 1080/2)));
						Thread.sleep(250);
					} catch (Exception e) {
						logger.log(Level.SEVERE, e.getMessage(), e.getCause());
						logger.log(Level.SEVERE, e.getMessage(), e.getCause());
						showMessageDialog(null, e.getClass() + ": " + e.getCause() + "\n" + e.getMessage(), "Error",
								JOptionPane.ERROR_MESSAGE);
						Toolkit.getDefaultToolkit().beep();
						Thread.sleep(5000);
						System.exit(1);
						return;
					}
				}
			} catch (Exception e) {
				logger.log(Level.SEVERE, e.getMessage(), e.getCause());
				showMessageDialog(null, e.getClass() + ": " + e.getCause() + "\n" + e.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
				Toolkit.getDefaultToolkit().beep();
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					logger.log(Level.SEVERE, e1.getMessage(), e1.getCause());
					showMessageDialog(null, e1.getClass() + ": " + e1.getCause() + "\n" + e1.getMessage(), "Error",
							JOptionPane.ERROR_MESSAGE);
					Toolkit.getDefaultToolkit().beep();

				}
				System.exit(1);
				return;
			}
		} finally {
			DiscordRPC.discordShutdown();
		}*/
	}

	
	public static Boolean checkPixel(BufferedImage img, int x, int y, int col, Anchor.Horizontal ah, Anchor.Vertical av, Graphics... debug) {
		Position pos = mainWindow.getPixelAt(x, y, ah, av);
		int checkCol;
		try {
			checkCol = img.getRGB(pos.x, pos.y);
		} catch(ArrayIndexOutOfBoundsException e) {
			logger.log(Level.SEVERE, e.getMessage(), e.getCause());
			showMessageDialog(null, e.getClass() + ": " + e.getCause() + "\n" + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			Toolkit.getDefaultToolkit().beep();
			System.exit(1);
			return false;
		}
		if(debug[0] != null) {
			if(checkCol == col) {
				debug[0].setColor(Color.green);
			} else {
				debug[0].setColor(Color.red);
			}
			debug[0].drawRect(pos.x-3, pos.y-3, 9, 9);
		}
		return checkCol == col;
	};
	
	public static String shortify(String s) {
		s = s.replaceAll("\\n", "");
		if (s.equals("Leighton Stepford Road"))
			return "Leighton Step Rd";
		if (s.equals("Millcastle Racecourse"))
			return "Mill. Racecourse";
		if (s.equals("Stepford United Football"))
			return "Stepford UFC";
		if (s.equals("Cambridge Street..."))
			return "Cambridge Street Pkw";
		return s;
	}

	public static String shortify2(String s) {
		s = s.replaceAll("\\n", "");
		if (s.equals("Stepford United Football Club"))
			return "Stepford UFC";
		return s;
	}

	public static String parseHeadcode(String h, Operators c) {
		char prefix = h.charAt(0);
		char dest;
		switch (h.charAt(1)) {
		case '1':
			dest = 'I';
			break;
		case '4':
			dest = c == Operators.WATERLINE ? 'J' : 'A';
			break;
		case '5':
			dest = 'S';
			break;
		case '8':
			dest = 'S';
			break;
		case '0':
			dest = 'O';
			break;
		case '6':
			dest = 'G';
			break;
		case '7':
			dest = 'T';
			break;
		default:
			dest = h.charAt(1);
			break;
		}
		char number1 = h.charAt(2);
		char number2 = h.length() > 3 ? h.charAt(3) : h.charAt(2);
		switch (number1) {
		case 'o':
			number1 = '0';
			break;
		case 'O':
			number1 = '0';
			break;
		}
		switch (number2) {
		case 'o':
			number2 = '0';
			break;
		case 'O':
			number2 = '0';
			break;
		}
		return "" + prefix + dest + number1 + number2;
	}

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

}
