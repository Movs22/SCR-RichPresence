package com.movies22.scr.rpc;

public class Screen {
	private int width;
	private int height;
	private int offX;
	private int offY;

	public Screen(int x, int y, int offX, int offY) {
		this.width = x;
		this.height = y;
		this.offX = offX;
		this.offY = offY;
	}

	public Position getPixelAt(int x, int y, Anchor.Horizontal ah, Anchor.Vertical av) {
		int newX = 0;
		int newY = 0;
		switch (ah) {
		case CENTRE:
			newX = x + this.width / 2;
			break;
		case LEFT:
			newX = x;
			break;
		case RIGHT:
			newX = x + this.width;
			break;
		}
		switch (av) {
		case CENTRE:
			newY = y + this.height / 2;
			break;
		case TOP:
			newY = y;
			break;
		case BOTTOM:
			newY = y + this.height;
			break;
		}
		return new Position(newX, newY);
	}
	
	public int getScale(Double pixel, Boolean vertical) {
		return (int) (vertical ? Math.round(pixel*this.height) : Math.round(pixel*this.width));
	}

	public class Position {
		public int x;
		public int y;

		public Position(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
}
