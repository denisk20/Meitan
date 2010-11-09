package com.meitan.lubov.services.util; 

import com.meitan.lubov.model.persistent.Client;
import com.meitan.lubov.services.commerce.AdminConfirmOrderMessageBuilder;
import com.meitan.lubov.services.commerce.BuyerConfirmOrderMessageBuilder;
import com.meitan.lubov.services.commerce.ConfirmOrderMessageBuilder;
import com.meitan.lubov.services.commerce.ShoppingCart;
import com.meitan.lubov.services.dao.ClientDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import javax.mail.MessagingException;

/**
 * @author denis_k
 *         Date: 11.07.2010
 *         Time: 19:14:19
 */
@Service("mailService")
public class MailServiceImpl implements MailService {
	private final Log log = LogFactory.getLog(getClass());
	@Autowired
	private ClientDao clientDao;
	@Autowired
	private JavaMailSenderImpl mailSender;
	@Autowired
	private String adminEmail;
	@Autowired
	private ArrayList<String> ccEmails;
	@Autowired
	private String from;
	
	@Override
	//todo make this accept only login
	public void sendBuyingActNotification(ShoppingCart cart, Principal currentUser) throws MessagingException {
		Client client = clientDao.getByLogin(currentUser.getName());

		ConfirmOrderMessageBuilder adminBuilder = new AdminConfirmOrderMessageBuilder();
		ConfirmOrderMessageBuilder buyerBuilder = new BuyerConfirmOrderMessageBuilder();

		prepareBuilder(cart, client, adminBuilder);
		prepareBuilder(cart, client, buyerBuilder);

		MimeMailMessage messageToAdmin = getMessage(adminBuilder);
		MimeMailMessage messageToBuyer = getMessage(buyerBuilder);



		messageToAdmin.setTo(adminEmail);
		messageToBuyer.setTo(client.getEmail());
		messageToBuyer.setCc(ccEmails.toArray(new String[0]));

		messageToAdmin.setFrom(from);
		messageToBuyer.setFrom(from);

		try {
			mailSender.send(messageToAdmin.getMimeMessage());
			mailSender.send(messageToBuyer.getMimeMessage());
		} catch (MailException e) {
			log.error("Can't send message", e);
		}
	}

	private void prepareBuilder(ShoppingCart cart, Client client, ConfirmOrderMessageBuilder adminBuilder) {
		adminBuilder.setClient(client);
		adminBuilder.setShoppingCart(cart);
	}

	private MimeMailMessage getMessage(ConfirmOrderMessageBuilder builder) throws MessagingException {
		MimeMessageHelper helper = new MimeMessageHelper(mailSender.createMimeMessage(), "UTF-8");
		MimeMailMessage message = new MimeMailMessage(helper);
		message.setSubject(builder.getHeader());
		helper.setText(builder.getMessageText(), true);

		return message;
	}
}
