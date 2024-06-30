package com.movies22.scr.rpc;

import java.awt.Color;


public enum Operators {
	AIRLINK((new Color(226, 122, 20)), "Airlink"),
	CONNECT((new Color(0, 158, 225)), "Connect"),
	EXPRESS((new Color(255, 0, 128)), "Express"),
	WATERLINE((new Color(73, 95, 124)), "Waterline"),
	SELECTING((new Color(154, 154, 154)), "%OPERATOR%"),
	UNKNOWN((new Color(0, 0, 0)), "unknown");
	public String operator;
	public Color color;
	public String name;
	Operators(Color color, String n) {
		this.color = color;
		this.name = n;
	}
	
}
