package com.movies22.scr.rpc;

import java.awt.image.BufferedImage;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.logging.Logger;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;
import com.movies22.scr.rpc.activities.Activity;

public class IRPC {
	
	private Logger logger;
	
	private IPCClient client;
	
	private RichPresence.Builder previousPresence;

	public IRPC() {
		logger = Main.logger;
		client = new IPCClient(1227325093781311663L); // your client id
		client.setListener(new IPCListener() {
		    @Override
		    public void onReady(IPCClient client) {
		        logger.info("IRPC has been connected to Discord");
		        RPC.shutdown();
		    }
		});
		try {
			client.connect();
		} catch (NoDiscordClientException e) {
			logger.severe(e.getMessage());
		}
	}
	
	public void updateStatus(Activity a, BufferedImage img) {
		RichPresence.Builder builder = new RichPresence.Builder();
        builder.setState(a.getState(img))
            .setDetails(a.getDetails(img))
            .setLargeImage("scrlogo", a.getLImageLabel(img))
            .setSmallImage(a.getSImage(img), a.getSImageLabel(img))
            .setStartTimestamp(OffsetDateTime.ofInstant(Instant.ofEpochMilli(Main.start), ZoneOffset.systemDefault()));
        
        if(a.getMaxPlayers() > 0) {
        	builder.setParty("scr-rpc-party", a.getMinPlayers(), a.getMaxPlayers());
        }
        
        client.sendRichPresence(builder.build());
	}
	
	
	
	public void shutdown() {
		client.close();
	}
}
