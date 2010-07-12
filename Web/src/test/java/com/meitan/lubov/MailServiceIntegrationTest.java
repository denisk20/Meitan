package com.meitan.lubov;

import com.meitan.lubov.model.persistent.Client;
import com.meitan.lubov.services.commerce.ShoppingCart;
import com.meitan.lubov.services.commerce.ShoppingCartImpl;
import com.meitan.lubov.services.dao.ClientDao;
import com.meitan.lubov.services.dao.Dao;
import com.meitan.lubov.services.util.MailService;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;

import java.security.Principal;

/**
 * @author denis_k
 *         Date: 11.07.2010
 *         Time: 20:52:05
 */
public class MailServiceIntegrationTest extends GenericIntegrationTest<Client>{
	//todo make testClientDao
	@Autowired
	private ClientDao testClientDao;

	@Autowired
	private MailService mailService;
	@Override
	protected void setUpBeanNames() {
		beanNames.add("ent_creamLover");
	}

	@Override
	protected Dao<Client, Long> getDAO() {
		return testClientDao;
	}

	//@Ignore
	@Test
	public void testSendEmail() {
		Client client = beansFromDb.get(0);
		Principal user = new UsernamePasswordAuthenticationToken(client.getLogin(), client.getPassword(),
				new GrantedAuthorityImpl[] {new GrantedAuthorityImpl(client.getRole())});

		ShoppingCart cart = new ShoppingCartImpl();

		mailService.sendBuyingActNotification(cart, user);
	}
}
