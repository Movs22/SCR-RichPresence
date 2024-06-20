package com.movies22.scr.rpc;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.tess4j.Word;


//taken from com.movies22.scr.autodrive.Utils @ 20/06/2024

public class Utils {
	public static BufferedImage resize(BufferedImage img, int newW, int newH) {
		BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g = dimg.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(img, 0, 0, newW, newH, 0, 0, img.getWidth(), img.getHeight(), null);

		return dimg;
	}
	
	public static BufferedImage resize2(BufferedImage img, int newW, int newH) {
		BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g = dimg.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		g.drawImage(img, 0, 0, newW, newH, 0, 0, img.getWidth(), img.getHeight(), null);

		return dimg;
	}

	public static int getSpeed(String s, int fallback) {
		if(s.contains("125")) return 125;
		if(s.contains("110")) return 110;
		if(s.contains("100")) return 100;
		if(s.contains("90")) return 90;
		if(s.contains("80")) return 80;
		if(s.contains("75")) return 75;
		if(s.contains("70")) return 70;
		if(s.contains("65")) return 65;
		if(s.contains("60")) return 60;
		if(s.contains("55")) return 55;
		if(s.contains("50")) return 50;
		if(s.contains("45")) return 45;
		if(s.contains("30")) return 30;
		if(s.contains("15")) return 15;
		if(s.contains("10")) return 10;
		if(s.contains("5")) return 5;
		return fallback;
	}
	
	public static Boolean isStopMarker(Color col, Color n, Color s, Color e, Color w) {
		return (isBlack(n) || isBlack(s)|| isBlack(e) || isBlack(w)) && col.getRed() > 80 && Math.abs(col.getRed() - col.getGreen()) < 15 && Math.abs(col.getGreen() - col.getBlue()) < 15;
	}
	
	public static Boolean isBlack(Color n) {
		return isWhite(n) || (n.getRed() < 5 && n.getGreen() < 15 && n.getBlue() < 30);
	}
	
	public static Boolean isWhite(Color col) {
		return col.getRed() > 80 && Math.abs(col.getRed() - col.getGreen()) < 15 && Math.abs(col.getGreen() - col.getBlue()) < 15;
	}
	
	public static String checkCarmarker(List<Word> words, BufferedImage ctx) {
		List<Word> results = new ArrayList<Word>();
		Graphics2D g2 = ctx.createGraphics();
		words.forEach(word -> {
			if(word.getConfidence() < 50) {
				//System.out.println("Ignored " + word.getText() + " | conf: " + word.getConfidence());
				g2.setColor(Color.PINK);
				Rectangle bounds = word.getBoundingBox();
				g2.draw(bounds);
				return;
			}
			if(word.getText().startsWith(".") || word.getText().startsWith("-") || word.getText().startsWith("1") || word.getText().startsWith("9") || word.getText().startsWith("7") || word.getText().isBlank()) {
				//System.out.println("Ignored " + word.getText());
				g2.setColor(Color.RED);
				Rectangle bounds = word.getBoundingBox();
				g2.draw(bounds);
				return;
			}
			results.add(word);
			g2.setColor(Color.GREEN);
			Rectangle bounds = word.getBoundingBox();
			g2.draw(bounds);
		});
		results.sort((a, b) -> Math.round(b.getConfidence() - a.getConfidence()));
		if(results.size() == 0) return "";
		//System.out.println(results);
		return results.get(0).getText();
	}
}
