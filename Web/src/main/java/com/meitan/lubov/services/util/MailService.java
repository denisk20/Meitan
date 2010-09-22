package com.meitan.lubov.services.util;

import com.meitan.lubov.services.commerce.ShoppingCart;

import java.security.Principal;
import javax.mail.MessagingException;

/**
 * @author denis_k
 *         Date: 11.07.2010
 *         Time: 20:54:39
 */
public interface MailService {
	void sendBuyingActNotification(ShoppingCart cart, Principal currentUser) throws MessagingException;
}
