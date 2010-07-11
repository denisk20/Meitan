package com.meitan.lubov.services.util;

import com.meitan.lubov.model.persistent.Client;
import com.meitan.lubov.services.commerce.ShoppingCart;
import com.meitan.lubov.services.dao.ClientDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.security.Principal;

/**
 * @author denis_k
 *         Date: 11.07.2010
 *         Time: 19:14:19
 */
@Service("mailService")
public class MailServiceImpl implements MailService {
	@Autowired
	private ClientDao clientDao;
	@Autowired
	private JavaMailSenderImpl mailSender;
	@Autowired
	private SimpleMailMessage templateMessage;
	
	@Override
	public void sendBuyingActNotification(ShoppingCart cart, Principal currentUser) {
		Client client = clientDao.getByLogin(currentUser.getName());

		SimpleMailMessage message = new SimpleMailMessage(templateMessage);
		message.setTo(client.getEmail());

		message.setText("This is test message!");

		mailSender.send(message);
	}
}
