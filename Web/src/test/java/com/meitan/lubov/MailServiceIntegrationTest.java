package com.meitan.lubov;

import com.meitan.lubov.model.components.Price;
import com.meitan.lubov.model.persistent.Authority;
import com.meitan.lubov.model.persistent.Client;
import com.meitan.lubov.model.persistent.Product;
import com.meitan.lubov.services.commerce.ShoppingCart;
import com.meitan.lubov.services.commerce.ShoppingCartImpl;
import com.meitan.lubov.services.dao.ClientDao;
import com.meitan.lubov.services.dao.Dao;
import com.meitan.lubov.services.util.MailService;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.GrantedAuthorityImpl;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Set;
import javax.mail.MessagingException;

/**
 * @author denis_k
 *         Date: 11.07.2010
 *         Time: 20:52:05
 */
public class MailServiceIntegrationTest extends GenericIntegrationTest<Client>{
	private final static String TO_EMAIL = "denis.k1985@gmail.com";

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

	@Ignore
	@Test
	public void testSendEmail() throws MessagingException {
		Client client = beansFromDb.get(0);
		client.setEmail(TO_EMAIL);
		testClientDao.makePersistent(client);
		Set<Authority> roles = client.getRoles();
		GrantedAuthorityImpl[] auths = new GrantedAuthorityImpl[roles.size()];
		int i = 0;
		for (Authority a : roles) {
			auths[i] = new GrantedAuthorityImpl(a.getRole());
			i++;
		}
		Principal user = new UsernamePasswordAuthenticationToken(client.getLogin(),
				client.getPassword(), auths);

		ShoppingCart cart = new ShoppingCartImpl();

		Product product = new Product("Плюшка");
		product.setPrice(new Price(new BigDecimal(14)));
		Product product1 = new Product("Бантик");
		product1.setPrice(new Price(new BigDecimal(123)));

		cart.addItem(product);
		cart.addItem(product);
		cart.addItem(product1);
		mailService.sendBuyingActNotification(cart, user);
	}
}
