package com.meitan.lubov.services.commerce;

import com.meitan.lubov.model.PriceAware;
import com.meitan.lubov.model.persistent.Client;
import com.meitan.lubov.model.persistent.ShoppingCartItem;
import com.meitan.lubov.services.util.XmlResourceBundleControl;

import java.util.ResourceBundle;

/**
 * Date: Sep 22, 2010
 * Time: 4:24:57 PM
 *
 * @author denisk
 */
public abstract class AbstractConfirmOrderMessageBuilder implements ConfirmOrderMessageBuilder {
	private Client client;
	private ShoppingCart shoppingCart;
	protected ResourceBundle rb;
	private static final String MAIL_PROPERTIES = "mail";

	public AbstractConfirmOrderMessageBuilder() {
		rb = ResourceBundle.getBundle(MAIL_PROPERTIES, new XmlResourceBundleControl());
	}

	protected abstract String getGreeting() ;

	protected abstract String getWhatWasOrdered();

	protected abstract String getByPrice();

	protected abstract String getForItemInQuantity();

	protected abstract String getPricesTotal();

	protected abstract String getTotal();

	protected abstract String getBuyerCoordinates();

	protected abstract String getEmail();

	protected abstract String getPhone();

	@Override
	public void setClient(Client client) {
		this.client = client;
	}

	@Override
	public void setShoppingCart(ShoppingCart cart) {
		this.shoppingCart = cart;
	}

	@Override
	public String getMessageText() {
		StringBuilder sb = new StringBuilder();
		sb.append(getGreeting());
		sb.append(client.getName());

		sb.append(client.getName().getFirstName() + " "
				+ client.getName().getPatronymic() + " "
				+ client.getName().getSecondName() + "\n");

		sb.append(getWhatWasOrdered() + "\n");

		for (ShoppingCartItem i : shoppingCart.getItems()) {
			PriceAware item = i.getItem();
			sb.append(item.getName() +
					getByPrice() + item.getPrice().getAmount() +
					getForItemInQuantity() + i.getQuantity() +
					getPricesTotal() + i.getPrice() + "\n");
		}
		sb.append(getTotal() + shoppingCart.getTotalPrice()+ "\n");

		sb.append(getBuyerCoordinates() + "\n");
		sb.append(getEmail() + client.getEmail() + "\n");
		sb.append(getPhone() + client.getPhone() + "\n");

		return sb.toString();
	}
}
