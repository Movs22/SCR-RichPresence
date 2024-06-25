package com.movies22.scr.rpc;

import java.util.logging.Logger;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

public class RPC {
	private Logger logger = Main.logger;
	private DiscordRichPresence.Builder lastPresence;
	public RPC() {
		DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler((user) -> {
			logger.info("Welcome " + user.username + "#" + user.discriminator + ".");
			updateStatus("Loading status...","Playing in a public server");
		}).build();
		DiscordRPC.discordInitialize("1227325093781311663", handlers, false);
		DiscordRPC.discordRegister("1227325093781311663", "");
	}
	
	public void updateStatus(String title, String desc, String... img) {
		DiscordRichPresence.Builder presence = new DiscordRichPresence.Builder(desc);	
		presence.setDetails(title);
		if(img.length > 1) presence.setSmallImage(img[0], img[1]);
		presence.setStartTimestamps(Main.start);
		presence.setBigImage("scrlogo", "SCR 1.10.13");
		if(presence.equals(lastPresence)) return;
		lastPresence = presence;
		DiscordRPC.discordUpdatePresence(presence.build());
		presence = null;
	}
}
