package com.movies22.scr.rpc.elements;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.movies22.scr.rpc.Main;

import net.sourceforge.tess4j.TesseractException;

public class Element {
	private Rect area;
	private String name;
	public Element(String n, int x, int y, int sizeX, int sizeY, Anchor.Horizontal h, Anchor.Vertical v) {
		this.area = new Rect(x, y, sizeX, sizeY, h, v);
		this.name = n;
	}
	
	public int[] getBounds(int w, int h) {
		return this.area.getBounds(w, h);
	}
	
	public String getName() {
		return this.name;
	}
	
	public void _debugRenderElement(Color col, Graphics g, BufferedImage img) {
		g.setColor(col);
		int[] b = this.getBounds(img.getWidth(), img.getHeight());
		g.fillRect(b[0], b[1], b[2], b[3]);
		g.drawString(name, b[0], b[1]);
	}
	
	public Boolean isPixel(BufferedImage img, int rgb) {
		int[] bounds = this.getBounds(img.getWidth(), img.getHeight());
		return img.getSubimage(bounds[0], bounds[1], bounds[2], bounds[3]).getRGB(0, 0) == rgb;
	}
	
	public BufferedImage getSubImage(BufferedImage img) {
		int[] bounds = this.getBounds(img.getWidth(), img.getHeight());
		return img.getSubimage(bounds[0], bounds[1], bounds[2], bounds[3]);
	}
	
	public String doOCR(BufferedImage img) {
		return doOCR(img, "default");
	}
	
	public String doOCR(BufferedImage img, String engine) {
		BufferedImage img2 = this.getSubImage(img);
		
		String result;
		try {
			switch(engine) {
			case "default":
				result = Main.ts.doOCR(img2);
				break;
			case "sg":
				result = Main.sg_ts.doOCR(img2);
				break;
			default:
				result = "tesseract://NO_ENG";
				break;
			}
		} catch (TesseractException e) {
			Main.logger.severe(e.getMessage());
			result = "tesseract://FAIL";
		}
		//DEBUG DRAW
		if(Main.DEVELOPER_MODE) {
			Graphics graphics = img.getGraphics();
			graphics.setColor(new Color(0, 255, 0, 127));
			int[] b = this.getBounds(img.getWidth(), img.getHeight());
			graphics.fillRect(b[0], b[1], b[2], b[3]);
			
			graphics.dispose();
		}
		return result;
	}
	
	
	private class Rect {
		private Rectangle area;
		private int posX;
		private int posY;
		private Anchor.Horizontal anchorX;
		private Anchor.Vertical anchorY;
		
		public Rect(int x, int y, int sizeX, int sizeY, Anchor.Horizontal h, Anchor.Vertical v) {
			area = new Rectangle(sizeX, sizeY);
			this.posX = x;
			this.posY = y;
			this.anchorX = h;
			this.anchorY = v;
		}
		
		public int[] getBounds(int width, int height) {
			int[] coords = new int[4];
			coords[2] = this.area.width;
			coords[3] = this.area.height;
			switch(this.anchorX) {
				case LEFT:
					coords[0] = this.posX;
					break;
				case CENTRE:
					coords[0] = (width - this.area.width)/2 + this.posX;
					break;
				case RIGHT:
					coords[0] = (width - this.area.width) + this.posX;
					break;
			}
			switch(this.anchorY) {
				case TOP:
					coords[1] = this.posY;
					break;
				case CENTRE:
					coords[1] = (height - this.area.height)/2 + this.posY;
					break;
				case BOTTOM:
					coords[1] = (height - this.area.height) + this.posY;
					break;
			}
			return coords;
		}
	}
}
