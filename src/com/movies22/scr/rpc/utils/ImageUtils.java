package com.movies22.scr.rpc.utils;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class ImageUtils {
	public static BufferedImage resize(BufferedImage img, double d, double e) {
		BufferedImage dimg = new BufferedImage((int) d, (int) e, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g = dimg.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(img, 0, 0, (int) d, (int) e, 0, 0, img.getWidth(), img.getHeight(), null);

		return dimg;
	}
}
