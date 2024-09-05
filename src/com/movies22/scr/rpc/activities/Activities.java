package com.movies22.scr.rpc.activities;

public enum Activities {
	MENU("Menu", new Menu()),
	SETTINGS("Settings", new Settings()),
	TRAININGTAB("TrainingTab", new TrainingTab()),
	ROLESELECTION("RoleSelection", new RoleSelection()),
	SIGNALLINGCAMERA("SignallingCamera", new SignallingCamera());
	public String name;
	public Activity activity;
	Activities(String n, Activity a) {
		this.name = n;
		this.activity = a;
		a.init();
	}
}
