package com.movies22.scr.rpc.activities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.movies22.scr.rpc.elements.Anchor;
import com.movies22.scr.rpc.elements.Element;

public class Menu extends Activity {
	public static Element VERSION;
	public static Element SERVER_TYPE;
	public static Element TITLE;
	
	@Override
	public void init() {
		VERSION = new Element("GLOBAL.Version", 0, 0, 88, 20, Anchor.Horizontal.RIGHT, Anchor.Vertical.BOTTOM);
		SERVER_TYPE = new Element("GLOBAL.ServerType", 108, 13, 34, 20, Anchor.Horizontal.LEFT, Anchor.Vertical.TOP);
		TITLE = new Element("MAINMENU.Title", 131, -74, 155, 71, Anchor.Horizontal.LEFT, Anchor.Vertical.CENTRE);
	}
	
	@Override
	public String getDetails(BufferedImage img) {
		return "Idling in the main menu";
	}
	
	@Override
	public void _debugDrawActivity(Color c, Graphics g, BufferedImage i) {
		VERSION._debugRenderElement(c, g, i);
		TITLE._debugRenderElement(c, g, i);
		SERVER_TYPE._debugRenderElement(c, g, i);
	}
	
	@Override
	public String getState(BufferedImage img) {
		String a = SERVER_TYPE.doOCR(img);
		if(a.contains("VIP")) return "In a VIP server";
		return "In a public server";
	}
	
	@Override
	public String getLImageLabel(BufferedImage img) {
		return VERSION.doOCR(img);
	}
	
	@Override
	public Boolean checkStatus(BufferedImage img) {
		return TITLE.doOCR(img).contains("SCR");
		
	}
}
