package com.meitan.lubov.services.util.sync;

import com.meitan.lubov.model.persistent.Category;

import java.net.URL;

/**
 * Date: Jan 8, 2011
 * Time: 4:39:14 PM
 *
 * @author denisk
 */
public class ParsedCategory {
	private Category category;
	private URL imageUrl;
	private URL itemsUrl;

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category c) {
		this.category = c;
	}

	public URL getItemsUrl() {
		return itemsUrl;
	}

	public void setItemsUrl(URL itemsUrl) {
		this.itemsUrl = itemsUrl;
	}

	public URL getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(URL imageUrl) {
		this.imageUrl = imageUrl;
	}
}
