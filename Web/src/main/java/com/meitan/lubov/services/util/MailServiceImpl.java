package com.meitan.lubov.services.util; 

import com.meitan.lubov.model.PriceAware;
import com.meitan.lubov.model.persistent.Client;
import com.meitan.lubov.services.commerce.AdminConfirmOrderMessageBuilder;
import com.meitan.lubov.services.commerce.BuyerConfirmOrderMessageBuilder;
import com.meitan.lubov.services.commerce.ConfirmOrderMessageBuilder;
import com.meitan.lubov.services.commerce.ShoppingCart;
import com.meitan.lubov.model.persistent.ShoppingCartItem;
import com.meitan.lubov.services.dao.ClientDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;

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
//	@Autowired
//	@Qualifier(value = "adminEmail")
	private String adminEmail = "denis.k1985@gmail.com";
//	@Autowired
	private ArrayList<String> ccEmails;
	
	@Override
	//todo make this accept only login
	public void sendBuyingActNotification(ShoppingCart cart, Principal currentUser) {
		Client client = clientDao.getByLogin(currentUser.getName());

		ConfirmOrderMessageBuilder adminBuilder = new AdminConfirmOrderMessageBuilder();
		prepareBuilder(cart, client, adminBuilder);

		SimpleMailMessage messageToAdmin = getMessage(adminBuilder);
		messageToAdmin.setTo(adminEmail);


		ConfirmOrderMessageBuilder buyerBuilder = new BuyerConfirmOrderMessageBuilder();
		prepareBuilder(cart, client, buyerBuilder);

		SimpleMailMessage messageToBuyer = getMessage(buyerBuilder);
		messageToBuyer.setTo(client.getEmail());

		ccEmails = new ArrayList<String>();
		ccEmails.add("denis.k1985@gmail.com");
		ccEmails.add("denis_k@nixsolutions.com");
		
		messageToBuyer.setCc(ccEmails.toArray(new String[0]));

		mailSender.send(messageToAdmin);
		mailSender.send(messageToBuyer);
	}

	private void prepareBuilder(ShoppingCart cart, Client client, ConfirmOrderMessageBuilder adminBuilder) {
		adminBuilder.setClient(client);
		adminBuilder.setShoppingCart(cart);
	}

	private SimpleMailMessage getMessage(ConfirmOrderMessageBuilder builder) {
		SimpleMailMessage message = new SimpleMailMessage(templateMessage);
		message.setSubject(builder.getHeader());
		message.setText(builder.getMessageText());

		return message;
	}
}
