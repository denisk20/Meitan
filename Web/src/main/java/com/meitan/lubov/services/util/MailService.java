package com.meitan.lubov.services.util;

import com.meitan.lubov.services.commerce.ShoppingCart;
import com.meitan.lubov.services.dao.ClientDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;

/**
 * @author denis_k
 *         Date: 11.07.2010
 *         Time: 19:14:19
 */
@Service("mailService")
public class MailService {
	@Autowired
	private ClientDao clientDao;
	public void sendBuyingActNotification(ShoppingCart cart, Principal currentUser) {
		int i=0;
	}
}
