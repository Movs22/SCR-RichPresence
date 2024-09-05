package com.movies22.scr.rpc;

public class ShutdownHook extends Thread {
	public void run() {
		Main.SystemTray.shutdown();
	}
}
