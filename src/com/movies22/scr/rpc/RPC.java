package com.movies22.scr.rpc;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

/*
 * Fetches user info using discord's RPC dll (given the limitations of IPC connections)
 */


public class RPC {
	public static void fetchUser() {
		Main.logger.info("Initialized RPC");
		
		DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler((user) -> {
			Main.logger.info("Welcome " + user.username + "#" + user.discriminator + ".");
			Main.user = user;
			DiscordRichPresence.Builder presence = new DiscordRichPresence.Builder("");	
			presence.setDetails("Loading status...");
			presence.setBigImage("scrlogo", "SCR 2.0.0");
			DiscordRPC.discordUpdatePresence(presence.build());
		}).build();
		DiscordRPC.discordInitialize("1227325093781311663", handlers, false);
		DiscordRPC.discordRunCallbacks();
	}
	
	public static void runCallbacks() {
		DiscordRPC.discordRunCallbacks();
	}
	
	public static void shutdown() {
		DiscordRPC.discordShutdown();
	}
}
