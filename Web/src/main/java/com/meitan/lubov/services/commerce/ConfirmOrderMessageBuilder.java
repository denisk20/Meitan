package com.meitan.lubov.services.commerce;

import com.meitan.lubov.model.persistent.Client;

/**
 * Date: Sep 22, 2010
 * Time: 4:23:19 PM
 *
 * @author denisk
 */
public interface ConfirmOrderMessageBuilder {
	void setClient(Client client);

	void setShoppingCart(ShoppingCart cart);

	String getMessageText();

	String getHeader();
}
