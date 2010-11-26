package com.meitan.lubov.services.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Date: Aug 23, 2010
 * Time: 5:42:08 PM
 *
 * @author denisk
 */
@Service("menuBackgroundService")
public class MenuBackgroundService {
	@Autowired
	private RoundedItemBean blueBigRoundedItem;
	@Autowired
	private RoundedItemBean blueSmallRoundedItem;
	@Autowired
	private RoundedItemBean blueTinyRoundedItem;
	@Autowired
	private RoundedItemBean redSmallRoundedItem;

	private String selectedItem;
	private static final String SELECTED_BACKGROUND_IMAGE = "menu_selected_background.png";
	private static final String PLAIN_BACKGROUND_IMAGE = "menu_background.png";


	public String getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(String selectedItem) {
		this.selectedItem = selectedItem;
	}

	public String getImage(String item) {
		if (item.equals(selectedItem)) {
			return SELECTED_BACKGROUND_IMAGE;
		} else {
			return PLAIN_BACKGROUND_IMAGE;
		}
	}

	public RoundedItemBean getRoundedItem(String item) {
		if (item.equals(selectedItem)) {
			return redSmallRoundedItem;
		} else {
			return blueSmallRoundedItem;
		}
	}
}
