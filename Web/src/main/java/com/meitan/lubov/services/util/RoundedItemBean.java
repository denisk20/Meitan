package com.meitan.lubov.services.util;

/**
 * Date: Nov 23, 2010
 * Time: 4:29:20 PM
 *
 * @author denisk
 */
public class RoundedItemBean {
	private String color;
	private String size;
	private int itemWidth;
	private int itemHeight;

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public int getItemWidth() {
		return itemWidth;
	}

	public void setItemWidth(int itemWidth) {
		this.itemWidth = itemWidth;
	}

	public int getItemHeight() {
		return itemHeight;
	}

	public void setItemHeight(int itemHeight) {
		this.itemHeight = itemHeight;
	}

	@Override
	public String toString() {
		return "RoundedItemBean{" +
				"color='" + color + '\'' +
				", size='" + size + '\'' +
				", itemWidth=" + itemWidth +
				", itemHeight=" + itemHeight +
				'}';
	}
}
