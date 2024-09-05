package com.movies22.scr.rpc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;

import com.sun.jna.Library;

public class RoVer {

	private static long robloxId = -1L;

	private static String token = "ROVER_TOKEN";
	
	private static String token2 = "ROBLOX_TOKEN";
	
	public static void roverLink(String discordId) throws MalformedURLException {
		if (robloxId == -1L) {
			URL url = new URL(
					"https://registry.rover.link/api/guilds/1277349531645903033/discord-to-roblox/" + discordId);

			HttpURLConnection conn;
			try {
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestProperty("Authorization", "Bearer " + token);
				conn.setRequestProperty("Content-Type", "application/json");
				conn.setRequestMethod("GET");

				BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String output;

				StringBuffer response = new StringBuffer();
				while ((output = in.readLine()) != null) {
					response.append(output);
				}
				in.close();
				// printing result from response
				JSONObject obj = new JSONObject(response.toString());
				RoVer.robloxId = Integer.toUnsignedLong((int) obj.get("robloxId"));
				Main.logger.info("Found a linked roblox account: " + obj.get("cachedUsername") + " (" + obj.get("robloxId") + ")");
				
				RoVer.getRobloxServer();
				
			} catch (IOException e) {
				Main.logger.severe("Failed to setup RoVer integration. API returned a 500 status code");
				Main.logger.warning(e.getMessage());
				return;
			}

		}
	}
	
	public static String getRobloxServer() throws MalformedURLException {
		if(RoVer.robloxId == -1) {
			MainOld.logger.severe("Attempted to fetch Roblox server without a valid roblox ID.");
			return ServerErrors.NoRoVer.toString();
		}
		URL url = new URL("https://presence.roblox.com/v1/presence/users");
		HttpURLConnection conn;
		try {
			String resp = "{ \"userIds\": [ ID ] }";
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Cookie", ".ROBLOSECURITY=" + token2);
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Length", Integer.toString(resp.length()));
			conn.getOutputStream().write(resp.getBytes("UTF8"));

			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String output;

			StringBuffer response = new StringBuffer();
			while ((output = in.readLine()) != null) {
				response.append(output);
			}
			in.close();
			JSONObject obj = new JSONObject(response.toString());
			MainOld.logger.info("Received " + obj);
		} catch (IOException e) {
			MainOld.logger.severe("Failed to setup RoVer integration. API returned a 500 status code");
			MainOld.logger.warning(e.getMessage());
			return ServerErrors.Unknown.toString();
		}
		return "NULL";
	}

	public static long getRobloxId() {
		return RoVer.robloxId;
	}

	public interface RoverHelper extends Library {

		public String get_token();
	}
	
	public enum ServerErrors {
		NoRoVer,
		NoJoins,
		Unknown;
		private ServerErrors() {
		}
	}
}
