package com.meitan.lubov.services.util;

import com.meitan.lubov.model.PriceAware;
import com.meitan.lubov.model.persistent.Client;
import com.meitan.lubov.services.commerce.ShoppingCart;
import com.meitan.lubov.services.commerce.ShoppingCartItem;
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

		String mailText = getMailText(cart, client);
		message.setText(mailText);

		mailSender.send(message);
	}

	private String getMailText(ShoppingCart cart, Client client) {
		StringBuilder sb = new StringBuilder("������� ������ !\n\n");
		sb.append("���� ������ ����� �����. ������� ��� ");
		sb.append(client.getName().getFirstName() + " "
				+ client.getName().getPatronymic() + " "
				+ client.getName().getSecondName() + "\n");

		sb.append("��� ��� ���� ��������:\n");

		for (ShoppingCartItem i : cart.getItems()) {
			PriceAware item = i.getItem();
			sb.append(item.getName() +
					" �� ���� " + item.getPrice().getAmount() +
					" �� ����� � ��������� " + i.getQuantity() +
					" ���� �� ����� " + i.getPrice() + "\n");
		}
		sb.append("����� ������ �� ����� " + cart.getTotalPrice()+ "\n");

		sb.append("��� ���������� ����������:\n");
		sb.append("�����: " + client.getEmail() + "\n");
		return sb.toString();
	}
}
