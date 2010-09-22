package com.meitan.lubov.services.commerce;

import com.meitan.lubov.services.util.XmlResourceBundleControl;

import java.util.ResourceBundle;

/**
 * Date: Sep 22, 2010
 * Time: 5:05:48 PM
 *
 * @author denisk
 */
public class AdminConfirmOrderMessageBuilder extends AbstractConfirmOrderMessageBuilder {

	private static final String GREETING = "message.order.header.mama";
	private static final String HEADER = "message.order.header.mama";
	private static final String WHAT_WAS_ORDERED = "message.order.whatwasordered";
	private static final String BUY_PRICE = "message.order.buyprice";
	private static final String FOR_ITEMS_QUANTITY = "message.order.foriteminquantity";
	private static final String PRICE_ITEM_TOTAL = "message.order.piecestotal";
	private static final String TOTAL_PRICE = "message.order.totalgoods";
	private static final String COORDS_HEADER = "message.order.buyercoords.mama";
	private static final String EMAIL = "message.order.email";
	private static final String PHONE = "message.order.phone";

	@Override
	protected String getGreeting() {
		return rb.getString(GREETING);
	}

	@Override
	protected String getWhatWasOrdered() {
		return rb.getString(WHAT_WAS_ORDERED);
	}

	@Override
	protected String getByPrice() {
		return rb.getString(BUY_PRICE);
	}

	@Override
	protected String getForItemInQuantity() {
		return rb.getString(FOR_ITEMS_QUANTITY);
	}

	@Override
	protected String getPricesTotal() {
		return rb.getString(PRICE_ITEM_TOTAL);
	}

	@Override
	protected String getTotal() {
		return rb.getString(TOTAL_PRICE);
	}

	@Override
	protected String getBuyerCoordinates() {
		return rb.getString(COORDS_HEADER);
	}

	@Override
	protected String getEmail() {
		return rb.getString(EMAIL);
	}

	@Override
	protected String getPhone() {
		return rb.getString(PHONE);
	}

	@Override
	public String getHeader() {
		return rb.getString(HEADER);
	}
}
