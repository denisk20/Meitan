package com.meitan.lubov.services.commerce;

import org.springframework.binding.message.MessageBuilder;
import org.springframework.mail.SimpleMailMessage;

/**
 * Date: Sep 19, 2010
 * Time: 7:11:43 PM
 *
 * @author denisk
 */
public class ConfirmOrderMailMessage extends SimpleMailMessage {
	private String greeting;
	private String whatWasOrdered;
	private String byPrice;
	private String forItemInQuantity;
	private String pricesTotal;
	private String buyerCoordinates;
	private String email;
	private String phone;

	public ConfirmOrderMailMessage() {
		MessageBuilder mb = new MessageBuilder();
	}
}
