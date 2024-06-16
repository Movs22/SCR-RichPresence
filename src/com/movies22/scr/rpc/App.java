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
	private static Color WHITE = new Color(255, 255, 255);
	private static Color SG = new Color(237, 228, 228);
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
		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(new FlowLayout());
		frame.pack();
		JLabel label2 = new JLabel(new ImageIcon());
		frame.getContentPane().add(label2);
		JLabel label = new JLabel(new ImageIcon());
		frame.getContentPane().add(label);
		JLabel label3 = new JLabel(new ImageIcon());
		frame.getContentPane().add(label3);
		ts.setTessVariable("tessedit_char_whitelist", "0123456789:+-. _[]ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
		//ts.setTessVariable("textord_old_xheight ", "true");
		//ts.setTessVariable("textord_min_xheight", "10");
		//ts.setTessVariable("textord_max_noise_size", "10");
		ts.setTessVariable("max_permuter_attempts", "500");
		ts.setDatapath("./tessdata");
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
		frame.setSize(400, 400);
		frame.setVisible(true);
		frame.setAlwaysOnTop(true);
		while(true) {
			img = robot.createScreenCapture(screen);
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
			} else if(img.getRGB(1748, 649) == WHITE.getRGB()) {
				status = validate(CurrentWindow.GUARDING_ONDUTY, status);
			} else if(img.getRGB(838, 40) == SG.getRGB()) {
				status = validate(CurrentWindow.SIGNALLING, status);
			} else if(img.getRGB(862, 303) == QD.getRGB()) {
				status = validate(CurrentWindow.SPAWN_MENU, status);
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
				if(curimg != null) {
				label2.setIcon(new ImageIcon(curimg));
				label.setIcon(new ImageIcon(stationimg));
				}
			}
			label3.setIcon(new ImageIcon(img));
			
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
								BufferedImage a = img.getSubimage(665, 1021, 245, 30);
								curStop = ts.doOCR(a);
								presence = new DiscordRichPresence.Builder("Loading at " + shortify(curStop));
								lastStop = curStop;
							} else {
								if(loading) {
									loading = false;
									curStopA = "";
									Thread.sleep(1000);
								}
								BufferedImage c;
								BufferedImage a = img.getSubimage(665, 1021, 245, 30);
								curStop2 = ts.doOCR(a);
									if(curStopA.equals("") || !curStop2.equals(curStop)) {
										c = img.getSubimage(684, 1057, 36, 14);
										curStopA = ts.doOCR(c);
									}
									curStop = curStop2;
									presence = new DiscordRichPresence.Builder("NS: " + shortify(curStop) + " @ " + curStopA);
							}
							presence.setDetails("Driving a" + ((curOperator == Operators.AIRLINK || curOperator == Operators.EXPRESS) ? "n " : " ") + curOperator.toString().toLowerCase() + " service.");
							
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
					presence = new DiscordRichPresence.Builder(vip ? "In a private server" : "In a public server");
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
						rank = rank.split("\\]")[0];
						rank = rank.split("\\[").length > 1 ? rank.split("\\[")[1] : rank;
						rank.replaceAll("6", "G");
						presence = new DiscordRichPresence.Builder("Next stop: " + shortify(t));
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
					if(img.getRGB(838, 40) == SG.getRGB()) {
						if(zone == "" ) {
							BufferedImage a = img.getSubimage(254, 17, 230, 40);
							zone = ts.doOCR(a);
						}
						presence = new DiscordRichPresence.Builder(vip ? "In a private server" : "In a public server");
						if(zone.contains("Supervisor")) {
							presence.setDetails("Idling in the supervisor desk");
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
		if(s.equals("Leighton Stepford Road")) return "Leighton S. Road";
		if(s.equals("Millcastle Racecourse")) return "Mill. Racecourse";
		if(s.equals("Llyn-by-the-sea")) return "Llyn";
		if(s.equals("Stepford United Football")) return "Stepford UFC";
		if(s.equals("Cambridge Street...")) return "Cambridge Street Pkw";
		return s;
	}
	public static String shortify2(String s) {
		s = s.replaceAll("\\n","");
		if(s.equals("Stepford United Football Club")) return "Stepford UFC";
		return s;
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
