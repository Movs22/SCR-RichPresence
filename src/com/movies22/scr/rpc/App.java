package com.movies22.scr.rpc;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.sourceforge.tess4j.Tesseract;

public class App {
	private static Tesseract ts;
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
	private static Color SG = new Color(232, 228, 228);
	private static Color ROBLOX = new Color(254, 254, 254);
	private static long start = System.currentTimeMillis();
	public static void main(String[] args) throws Exception {
		DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler((user) -> {
            System.out.println("Welcome " + user.username + "#" + user.discriminator + ".");
            DiscordRichPresence.Builder presence = new DiscordRichPresence.Builder("Score: ");
            presence.setDetails("Running Test");
            presence.setStartTimestamps(start);
            DiscordRPC.discordUpdatePresence(presence.build());
        }).build();
        DiscordRPC.discordInitialize("1227325093781311663", handlers, false);
        DiscordRPC.discordRegister("1227325093781311663", "");
		System.out.println("Fetching menu...");
		ts = new Tesseract();
		ts.setTessVariable("tessedit_char_whitelist", "0123456789:+-. _[]ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
		//ts.setTessVariable("textord_old_xheight ", "true");
		//ts.setTessVariable("textord_min_xheight", "10");
		//ts.setTessVariable("textord_max_noise_size", "10");
		ts.setTessVariable("max_permuter_attempts", "750");
		ts.setDatapath("./");
		Rectangle screen = new Rectangle(0, 0, 1920, 1080);
		robot = new Robot();
		BufferedImage img;
		System.out.println("Main menu has loaded!");
		Operators curOperator = Operators.UNKNOWN;
		Operators curOperator2;
		Boolean driving = false;
		Boolean vip = false;
		Boolean loading = false;
		String lastStop = null;
		String curStop = "";
		String curStop2 = "";
		String curStopA = "";
		String rank = "";
		String user = "";
		String zone = "";
		String currentStop = "";
		String plats = "";
		String currentDest = "";
		String currentHeadcode = "";
		String selRank = "";
		long lastCheck = 0L;
		long message = 0;
		while(true) {
			message = (System.currentTimeMillis() - start) % 10000;
			img = robot.createScreenCapture(screen);
			if(img.getRGB(27, 11) != ROBLOX.getRGB() && img.getRGB(27, 11) != WHITE.getRGB()) {
				if(lastCheck == 0L) lastCheck = System.currentTimeMillis();
				System.out.println("User isn't playing roblox anymore. Quitting in " + (15-Math.ceil((System.currentTimeMillis() - lastCheck)/100)/10) + " seconds.");
				if((System.currentTimeMillis() - lastCheck) > 15000) {
					System.out.println("User isn't playing roblox anymore. Quitting...");
					System.exit(1);
				}
			} else {
				lastCheck = 0L;
			}
			Color sgcol = new Color(img.getRGB(838, 40));
			if(img.getRGB(995, 947) == LOAD.getRGB()) {
				status = validate(CurrentWindow.LOADING, status);
			} else if(img.getRGB(158, 436) == MAIN_MENU.getRGB()) {
				status = validate(CurrentWindow.MAIN_MENU, status);
			} else if(img.getRGB(543, 1023) == WHITE.getRGB()) {
				status = validate(CurrentWindow.DRIVING, status);
			} else if(img.getRGB(1772, 670) == WHITE.getRGB()) {
				status = validate(CurrentWindow.DISPATCHING, status);
			}  else if(img.getRGB(1758, 657) == WHITE.getRGB()) {
				status = validate(CurrentWindow.GUARDING, status);
			} else if(img.getRGB(1240, 1028) == WHITE.getRGB()) {
				status = validate(CurrentWindow.DRIVING, status);
			} else if(sgcol.getRed() > 220 && sgcol.getGreen() > 210 && sgcol.getBlue() > 210) {
				status = validate(CurrentWindow.SIGNALLING, status);
			} else if(img.getRGB(862, 303) == QD.getRGB()) {
				status = validate(CurrentWindow.SPAWN_MENU, status);
				curOperator = null;
			} else if(img.getRGB(396, 1008) == SPAWN.getRGB() && (curOperator == null || curOperator == Operators.UNKNOWN)) {
				status = validate(CurrentWindow.SPAWN_MENU, status);
			}
			if(status == CurrentWindow.SPAWN_MENU && img.getRGB(24, 1008) == MAIN_MENU.getRGB()) {
				if(img.getRGB(789, 248) == SPAWN.getRGB()) {
					selRank = "Passenger";
				} else if(img.getRGB(1108, 248) == SPAWN.getRGB()) {
					selRank = "Driver";
				} else if(img.getRGB(1428, 248) == SPAWN.getRGB()) {
					selRank = "Dispatcher";
				} else if(img.getRGB(789, 567) == SPAWN.getRGB()) {
					selRank = "Guard";
				} else if(img.getRGB(1108, 567) == SPAWN.getRGB()) {
					selRank = "Signaller";
				} else if(img.getRGB(1428, 567) == SPAWN.getRGB()) {
					selRank = "Staff";
				}
			} else if(status == CurrentWindow.SPAWN_MENU) {
				switch(selRank) {
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

			if(img.getRGB(1877, 9) == VIP.getRGB()) {
				vip = true;
			}
			if(status == CurrentWindow.SPAWN_MENU && img.getRGB(743, 1008) == GREY1.getRGB()) {
				BufferedImage stationimg = img.getSubimage(30, 134, 335, 845);
				BufferedImage curimg = null;
				for(int i = 0; i < 845; i++) {
					if(stationimg.getRGB(0, i) == GREY1.getRGB()) i += 65;
					if(i >= 845) break;
					if(stationimg.getRGB(0, i) == LOAD.getRGB()) {
						for(int b = i; b > i-66; b--) {
							if(b > 845 || b < 0) break;
							if(stationimg.getRGB(0, b) != LOAD.getRGB()) break;
							i--;
						}
						if(i+11+35 > 845 || i+11 < 0) break;
						curimg = stationimg.getSubimage(10, 11+i, 315, 35);
						currentStop = ts.doOCR(curimg).replaceAll("\\n", "");
						i = 845;
					}
				}
			}
			
			// TODO:
			// add DS status
			// optimizations
			// rec video
			
			//TODO
			//check for roblox logo
			//if no roblox logo && if not scr, - stop program
			
			//add version check
			
			DiscordRPC.discordRunCallbacks();
			if(status != null) {
				DiscordRichPresence.Builder presence;
				if(status == CurrentWindow.DRIVING) {
					curOperator2 = curOperator.getOperator(img.getRGB(192, 60));
					if(curOperator2 != Operators.UNKNOWN) curOperator = curOperator2;
					if(curOperator2 != Operators.UNKNOWN) {
						presence = new DiscordRichPresence.Builder(vip ? "In a private server" : "In a public server");
						presence.setDetails("Selecting a route");
						presence.setStartTimestamps(start);
						presence.setBigImage("scrlogo", "SCR 1.10.13");
						//presence.setSmallImage("logo", curOperator.name);
						DiscordRPC.discordUpdatePresence(presence.build());
					} else {
						if(img.getRGB(1078, 1017) == WHITE.getRGB()) {
							if(lastStop != curStop) loading = false;
							Color check = new Color(img.getRGB(1069, 1040));
							if(check.getRed() > 200 && check.getGreen() > 120 && check.getBlue() < 40) {
								loading = true;
								BufferedImage y = img.getSubimage(770, 1005, 40, 17);
								String h = ts.doOCR(y);
								if(h == "") continue;
								if(h.length() < 4) h = currentHeadcode;
								currentHeadcode = parseHeadcode(h, curOperator);
								if(message < 5000) {
									BufferedImage a = img.getSubimage(665, 1021, 245, 30);
									curStop = ts.doOCR(a);
									presence = new DiscordRichPresence.Builder("Loading at " + shortify(curStop));
									lastStop = curStop;
								} else {
									BufferedImage a = img.getSubimage(852, 1055, 30, 18);
									curStop = ts.doOCR(a);
									if(curStop.contains("00")) curStop = "00";
									int z = Integer.parseInt(curStop.replaceAll("\\+", "").replaceAll("\\n", ""));
									if(z < 1) curStop = "on time";
									else curStop = z + " min" + (z > 1 ? "s" : "") + " late";
									presence = new DiscordRichPresence.Builder("Service running " + shortify(curStop));
									lastStop = curStop;
								}
							} else {
								if(loading) {
									loading = false;
									curStopA = "";
									Thread.sleep(1000);
								}
								BufferedImage c;
								BufferedImage y = img.getSubimage(770, 1005, 40, 17);
								String h = ts.doOCR(y);
								if(h == "") continue;
								if(h.length() < 4) h = currentHeadcode;
								currentHeadcode = parseHeadcode(h, curOperator);
								if(message < 5000) {
								BufferedImage a = img.getSubimage(665, 1021, 245, 30);
								curStop2 = ts.doOCR(a);
									if(curStopA.equals("") || !curStop2.equals(curStop)) {
										c = img.getSubimage(684, 1057, 36, 14);
										curStopA = ts.doOCR(c);
									}
									curStop = curStop2;
									presence = new DiscordRichPresence.Builder("NS: " + shortify(curStop) + " @ " + curStopA);
								} else {
									if(h.toLowerCase() == currentHeadcode) {
										presence = new DiscordRichPresence.Builder("Service to " + shortify(currentDest));
									} else {
									switch(currentHeadcode.toLowerCase().charAt(1)) {
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
											currentDest = curOperator == Operators.AIRLINK ? "Airport Parkway" : "Port Benton";
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
									presence = new DiscordRichPresence.Builder("Service to " + shortify(currentDest));
								}
								}
							}
							presence.setDetails("Driving a" + ((curOperator == Operators.AIRLINK || curOperator == Operators.EXPRESS) ? "n " : " ") + curOperator.toString().toLowerCase() + " service as " + currentHeadcode.toUpperCase());
							
						} else {
							presence = new DiscordRichPresence.Builder("Selecting a depot");
							presence.setDetails("Driving a" + ((curOperator == Operators.AIRLINK || curOperator == Operators.EXPRESS) ? "n " : " ") + curOperator.toString().toLowerCase() + " service.");
						}
						presence.setStartTimestamps(start);
						presence.setBigImage("scrlogo", "SCR 1.10.13");
						presence.setSmallImage(curOperator.name().toLowerCase(), curOperator.name);
						DiscordRPC.discordUpdatePresence(presence.build());
					}
					rank = "";
				} else if(status == CurrentWindow.DISPATCHING) {
					Color col = new Color(img.getRGB(1810, 497));
					if((col.getRed() < 100) && (col.getGreen() > 160) && (col.getBlue() > 160)) {
						BufferedImage headcode = img.getSubimage(1798, 476, 65, 30);
						BufferedImage plat = img.getSubimage(1755, 434, 144, 30);
						presence = new DiscordRichPresence.Builder("Dispatching " + ts.doOCR(headcode).replaceAll("\\n", "").toUpperCase() + " at " + ts.doOCR(plat).replaceAll("\\n", ""));
					} else {
						presence = new DiscordRichPresence.Builder(vip ? "In a private server" : "In a public server");
					}
					presence.setDetails("Dispatching at " + shortify2(currentStop));
					presence.setStartTimestamps(start);
					presence.setBigImage("scrlogo", "SCR 1.10.13");
					presence.setSmallImage("ds", "Dispatching");
					DiscordRPC.discordUpdatePresence(presence.build());
				}  else if(status == CurrentWindow.GUARDING) {
					curStop = "";
					if(img.getRGB(1746, 646) == RED.getRGB()) {
						BufferedImage a = img.getSubimage(1745, 591, 175, 23);
						String t = ts.doOCR(a);
						if(rank == "") {
							BufferedImage b = img.getSubimage(1740, 474, 175, 23);
							rank = ts.doOCR(b);
							BufferedImage c = img.getSubimage(1745, 455, 175, 23);
							user = ts.doOCR(c);
						}
						if(rank.contains("Guard Manager")) rank = "GM";
						if(rank.contains("Senior Guard")) rank = "SGD";
						if(rank.contains("Senior Dispatcher")) rank = "SDS";
						if(rank.contains("Guard")) rank = "GD";
						if(rank.contains("Dispatcher")) rank = "DS";
						else {
							rank = rank.split("\\]")[0];
							rank = rank.split("\\[").length > 1 ? rank.split("\\[")[1] : rank;
							rank.replaceAll("6", "G");
						}
						presence = new DiscordRichPresence.Builder("NS: " + shortify(t));
						presence.setDetails("Guarding [" + rank + "] " + user);
					} else {
						rank = "";
						presence = new DiscordRichPresence.Builder("Selecting a train");
						presence.setDetails("Guarding in a " + (vip ? "private" : "public") + " server.");
					}
					presence.setStartTimestamps(start);
					presence.setBigImage("scrlogo", "SCR 1.10.13");
					presence.setSmallImage("gd", "Guarding");
					DiscordRPC.discordUpdatePresence(presence.build());
				} else if(status == CurrentWindow.SIGNALLING) {
					Color col = new Color(img.getRGB(838, 40));
					if(col.getRed() > 220 && col.getGreen() > 210 && col.getBlue() > 210) {
						if(zone == "") {
							BufferedImage a = img.getSubimage(254, 17, 230, 40);
							zone = ts.doOCR(a);
						}
						int trains = 0;
						for(int y = 454; y < 1033; y++) {
							Color check = new Color(img.getRGB(1737, y));
							if(((check.getRed() < 50 && check.getGreen() > 50) || (check.getRed() > 50 && check.getRed() < 50)) && check.getBlue() < 50) {
								System.out.println(check.getRed() + "|" + check.getGreen() + "|" + check.getBlue());
								trains++;
								y += 39;
							}
						}
						presence = new DiscordRichPresence.Builder(trains + " train" + (trains == 1 ? "" : "s") + " in this zone.");
						if(zone.contains("Supervisor")) {
							presence.setDetails("Idling in the supervisor desk");
						} else if(!zone.contains("Zone") && !zone.contains("Supervisor")) {
							presence.setDetails("Checking the trains list");
						} else {
							presence.setDetails("Signalling in " + zone);
						}
						presence.setStartTimestamps(start);
						presence.setBigImage("scrlogo", "SCR 1.10.13");
						presence.setSmallImage("sg", "Signalling");
						DiscordRPC.discordUpdatePresence(presence.build());
					} else {
						Color checkCol = new Color(img.getRGB(818, 84));
						if(checkCol.getBlue() > 160 && checkCol.getGreen() > 120 && checkCol.getRed() < 50) {
							BufferedImage camtxt = img.getSubimage(906, 95, 105, 28);
							String cam = ts.doOCR(camtxt);
							presence = new DiscordRichPresence.Builder("Viewing " + cam);
							if(zone.contains("Supervisor")) {
								presence.setDetails("Idling in the supervisor desk");
							} else if(!zone.contains("Zone") && !zone.contains("Supervisor")) {
								presence.setDetails("Checking the trains list");
							} else {
								presence.setDetails("Signalling in " + zone);
							}
							presence.setStartTimestamps(start);
							presence.setBigImage("scrlogo", "SCR 1.10.13");
							presence.setSmallImage("sg", "Signalling");						
							DiscordRPC.discordUpdatePresence(presence.build());
						} else {
						zone = "";
						presence = new DiscordRichPresence.Builder(vip ? "In a private server" : "In a public server");
						presence.setDetails("Idling in the signaller role");
						presence.setStartTimestamps(start);
						presence.setBigImage("scrlogo", "SCR 1.10.13");
						presence.setSmallImage("sg", "Signalling");						
						DiscordRPC.discordUpdatePresence(presence.build());
						}
					}
				} else {
					presence = new DiscordRichPresence.Builder(vip ? "In a private server" : "In a public server");
					presence.setDetails(status.t);
					presence.setStartTimestamps(start);
					presence.setBigImage("scrlogo", "SCR 1.10.13");
					DiscordRPC.discordUpdatePresence(presence.build());
					curOperator = Operators.UNKNOWN;
					driving = false;
				}
			}
			Thread.sleep(250);
		}
	}
	public static String shortify(String s) {
		s = s.replaceAll("\\n","");
		if(s.equals("Leighton Stepford Road")) return "Leighton Step Rd";
		if(s.equals("Millcastle Racecourse")) return "Mill. Racecourse";
		if(s.equals("Stepford United Football")) return "Stepford UFC";
		if(s.equals("Cambridge Street...")) return "Cambridge Street Pkw";
		return s;
	}
	public static String shortify2(String s) {
		s = s.replaceAll("\\n","");
		if(s.equals("Stepford United Football Club")) return "Stepford UFC";
		return s;
	}
	
	public static String parseHeadcode(String h, Operators c) {
		char prefix = h.charAt(0);
		char dest;
		switch(h.charAt(1)) {
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
		switch(number1) {
			case 'o':
				number1 = '0';
				break;
			case 'O':
				number1 = '0';
				break;
		}
		switch(number2) {
		case 'o':
			number2 = '0';
			break;
		case 'O':
			number2 = '0';
			break;
	}
		return ""+prefix + dest + number1 + number2;
	}
	public static CurrentWindow validate(CurrentWindow b, CurrentWindow a) {
		if(a == null) return b;
		if(b.v == a.v) return b;
		if(b.v > a.v) return b;
		if(b.v == (a.v - 1)) return b;
		if(b.v == 1) return b;
		return a;
		
	}
}
