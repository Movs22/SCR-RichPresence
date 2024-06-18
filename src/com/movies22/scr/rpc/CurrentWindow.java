package com.movies22.scr.rpc;

public enum CurrentWindow {
	LOADING(0, "Loading the game"),
	MAIN_MENU(1, "Idling in the main menu"),
	SETTINGS(3, "Editing their settings"),
	SPAWN_MENU(2, "Picking a role"),
	DRIVING(3, "Driving "),
	DISPATCHING(3, "Dispatching at "),
	GUARDING(3, "Guarding "),
	SIGNALLING(3, "Signalling "),
	EXPLORING(3, "Exploring the map"),
	SUPERVISOR(3, "Idling in a staff role"),
	UNKNOWN(-1, "Unknown");
	public int v;
	public String t;
	CurrentWindow(int value, String title) {
		this.v = value;
		this.t = title;
	}
	
	@Override
	public String toString() {
		return v + "|" + t;
	}
}
