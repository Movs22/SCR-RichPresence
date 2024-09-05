package com.movies22.scr.rpc.activities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.movies22.scr.rpc.elements.Anchor;
import com.movies22.scr.rpc.elements.Element;

public class Settings extends Activity {
	public static Element TITLE;
	public static Element SERVER_TYPE;
	
	@Override
	public void init() {
		TITLE = new Element("SETTINGS.Title", 60, -248, 200, 50, Anchor.Horizontal.LEFT, Anchor.Vertical.CENTRE);
		SERVER_TYPE = new Element("GLOBAL.ServerType", 108, 13, 34, 20, Anchor.Horizontal.LEFT, Anchor.Vertical.TOP);
	}
	
	@Override
	public String getDetails(BufferedImage img) {
		return "Editing their settings";
	}
	
	@Override
	public String getState(BufferedImage img) {
		String a = SERVER_TYPE.doOCR(img);
		if(a.contains("VIP")) return "In a VIP server";
		return "In a public server";
	}
	
	@Override
	public void _debugDrawActivity(Color c, Graphics g, BufferedImage i) {
		TITLE._debugRenderElement(c, g, i);
		SERVER_TYPE._debugRenderElement(c, g, i);
	}
	
	@Override
	public String getLImageLabel(BufferedImage img) {
		return "Version 2.0.0";
	}
	
	@Override
	public Boolean checkStatus(BufferedImage img) {
		return TITLE.doOCR(img).contains("Settings") || TITLE.doOCR(img).contains("Display") || TITLE.doOCR(img).contains("Gameplay") || TITLE.doOCR(img).contains("Audio");
		
	}
}