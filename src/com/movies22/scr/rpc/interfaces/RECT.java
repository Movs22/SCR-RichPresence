package com.movies22.scr.rpc.interfaces;

import java.util.ArrayList;
import java.util.List;

import com.sun.jna.Structure;

public class RECT extends Structure {
	public int left, top, right, bottom;

	@Override
	protected List<String> getFieldOrder() {
		List<String> order = new ArrayList<>();
		order.add("left");
		order.add("top");
		order.add("right");
		order.add("bottom");
		return order;
	}
}