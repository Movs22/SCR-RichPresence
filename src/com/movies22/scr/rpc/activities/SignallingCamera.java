package com.movies22.scr.rpc.activities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.movies22.scr.rpc.Main;
import com.movies22.scr.rpc.elements.Anchor;
import com.movies22.scr.rpc.elements.Element;

public class SignallingCamera extends Activity {
	public static Element CAM;
	public static Element SERVER_TYPE;
	
	@Override
	public void init() {
		CAM = new Element("SIGNALLING.CameraLabel", 0, 91, 100, 34, Anchor.Horizontal.CENTRE, Anchor.Vertical.TOP);
		SERVER_TYPE = new Element("GLOBAL.ServerType", 108, 13, 34, 20, Anchor.Horizontal.LEFT, Anchor.Vertical.TOP);
	}
	
	@Override
	public String getDetails(BufferedImage img) {
		String cam = CAM.doOCR(img, "sg").split("\n")[0].split("CAM ")[1];
		if(cam.length() < 3) {
			cam = cam.charAt(0) + "00" + cam.charAt(1);
		}
		if(cam.length() < 4) {
			cam = cam.charAt(0) + "0" + cam.charAt(1) + cam.charAt(2);
		}
		return "Viewing CAM " + cam;
	}
	
	@Override
	public String getState(BufferedImage img) {
		String a = SERVER_TYPE.doOCR(img);
		if(a.contains("VIP")) return "Signalling in a VIP server";
		return "Signalling in a public server";
	}
	
	@Override
	public void _debugDrawActivity(Color c, Graphics g, BufferedImage i) {
		CAM._debugRenderElement(c, g, i);
		SERVER_TYPE._debugRenderElement(c, g, i);
	}
	
	@Override
	public String getLImageLabel(BufferedImage img) {
		return "Version 2.0.0";
	}
	
	@Override
	public String getSImage(BufferedImage img) {
		return "sg";
	}
	
	@Override
	public String getSImageLabel(BufferedImage img) {
		return "Signalling";
	}
	
	@Override
	public Boolean checkStatus(BufferedImage img) {
		return CAM.doOCR(img).contains("CAM");
	}
}