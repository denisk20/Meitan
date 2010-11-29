package com.meitan.lubov.services.dao.jpa.impl;

import com.meitan.lubov.model.persistent.Authority;
import com.meitan.lubov.model.persistent.BuyingAct;
import com.meitan.lubov.model.persistent.Client;
import com.meitan.lubov.model.persistent.ShoppingCartItem;
import com.meitan.lubov.services.commerce.ShoppingCart;
import com.meitan.lubov.services.dao.AuthorityDao;
import com.meitan.lubov.services.dao.BuyingActDao;
import com.meitan.lubov.services.dao.ClientDao;
import com.meitan.lubov.services.dao.ShoppingCartItemDao;
import com.meitan.lubov.services.dao.jpa.JpaDao;
import com.meitan.lubov.services.util.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.webflow.execution.RequestContext;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Date: Mar 5, 2010
 * Time: 10:16:28 PM
 *
 * @author denisk
 */
@Repository
@Service("clientDao")
public class JpaClientDao extends JpaDao<Client, Long> implements ClientDao {

	@Autowired
	private BuyingActDao buyingActDao;

	@Autowired
	private ShoppingCartItemDao shoppingCartItemDao;

	@Autowired
	private AuthorityDao authorityDao;

	@Override
	public List<Client> findByExample(Client exampleInstance, String... excludeProperty) {
		final List<Client> result = super.findByExample(exampleInstance, excludeProperty);
		return getDistinct(result);
	}

	@Override
	public List<Client> findAll() {
		final List<Client> clientList = super.findAll();
		return getDistinct(clientList);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public Client getByLogin(String login) {
		List<Client> result = em.createNamedQuery("getClientByLogin").setParameter("login", login).getResultList();
		if (result.size() == 0) {
			throw new IllegalArgumentException("Can't find user with login " + login);
		}
		if (result.size() != 1) {
			throw new IllegalStateException("Multiple users with login " + login);
		}
		return result.get(0);
	}

	@Override
	@Transactional
	public Client findByLoginOrCreateNew(Principal p) {
		Client c;
		if (p == null) {
			c = new Client();

		}
		else {
			c = getByLogin(p.getName());
		}

		return c;
	}
	@Override
	@Transactional
	public void buyGoods(ShoppingCart cart, String login) {
		Client client = getByLogin(login);
		if (client == null) {
			throw new IllegalArgumentException("No user in session: " + login);
		}
		BuyingAct act = new BuyingAct(new Date(), client);

		for (ShoppingCartItem it : cart.getItems()) {
			shoppingCartItemDao.makePersistent(it);
			act.getProducts().add(it);
		}

		client.getPurchases().add(act);
		buyingActDao.makePersistent(act);
	}

	@Override
	@Transactional
	//todo u-test
	public Client saveOrFetchUnregisteredClientByEmail(RequestContext requestContext,
													   Client c) throws IllegalAccessException {
		if (c == null) {
			throw new IllegalArgumentException("Client was null");
		}
		final String email = c.getEmail();
		if (email == null || email.equals("")) {
			throw new IllegalArgumentException("Email is undefined for client " + c);
		}

		final List resultList = em.createNamedQuery("getClientByEmail").setParameter("email", email).getResultList();
		if (resultList.size() == 0) {
			makePersistent(c);
			authorityDao.assignAuthority(c, SecurityService.ROLE_UNREGISTERED);
			return c;
		} else {
			if (resultList.size() != 1) {
				throw new IllegalStateException("Multiple users with email " + email);
			}
			Client existing = (Client) resultList.get(0);
			checkIfUnregistered(existing, requestContext);
			return existing;
		}
	}

	@Override
	@Transactional
	//todo unit test
	public void mergeAnonymousClient(RequestContext requestContext, Client c) throws IllegalAccessException {
		if (c == null) {
			throw new IllegalArgumentException("Client was null");
		}
		final String email = c.getEmail();
		if (email == null || email.equals("")) {
			throw new IllegalArgumentException("Email is undefined for client " + c);
		}

		final long id = c.getId();
		if (id == 0L) {
			throw new IllegalArgumentException("Id is undefined for client " + c);
		}
		final Client result = findById(id);
		if (result == null) {
			throw new IllegalArgumentException("No corresponding DB entity for client " + c);
		}
		checkIfUnregistered(result, requestContext);
		final List sameEmails = em.createNamedQuery("getClientByEmail").setParameter("email", email).getResultList();
		if (sameEmails.size() > 0) {
			//todo I18N
			requestContext.getMessageContext().addMessage(new MessageBuilder().error()
					.defaultText("Пользователь с таким почтовым ящиком уже существует. Выберите другой").build());

			throw new IllegalAccessException("Email already exists: " + email);
		}

		//todo this is not very nice if we will have to deal with different props
		result.setEmail(c.getEmail());
		result.setLogin(c.getEmail());
	}

	private void checkIfUnregistered(Client existing, RequestContext requestContext) throws IllegalAccessException {
		Set<Authority> roles = existing.getRoles();
		for (Authority authority : roles) {
			if (!authority.getRole().equals(SecurityService.ROLE_UNREGISTERED)) {
				requestContext.getMessageContext().addMessage(new MessageBuilder().error()
						.defaultText("Пользователь с таким почтовым ящиком уже существует. Выберите другого").build());

				throw new IllegalAccessException("Atempt to anonymously login for existent client " + existing);
			}
		}
	}

	@Override
	public List<Client> findByEmail(String email) {
		return em.createNamedQuery("getClientByEmail").setParameter("email", email).getResultList();
	}

	@Override
	@Transactional
	//todo implement and test it properly
	public void deleteById(Long id) {
		super.deleteById(id);
	}

	@Override
	public Client newInstance() throws IllegalAccessException, InstantiationException {
		final Client client = super.newInstance();
		client.getName().setFirstName("no name");
		return client;
	}

	@Override
	public void setBuyingActDao(BuyingActDao buyingActDao) {
		this.buyingActDao = buyingActDao;
	}

	@Override
	public BuyingActDao getBuyingActDao() {
		return buyingActDao;
	}

	@Override
	public ShoppingCartItemDao getShoppingCartItemDao() {
		return shoppingCartItemDao;
	}

	@Override
	public void setShoppingCartItemDao(ShoppingCartItemDao shoppingCartItemDao) {
		this.shoppingCartItemDao = shoppingCartItemDao;
	}

	@Override
	public AuthorityDao getAuthorityDao() {
		return authorityDao;
	}

	@Override
	public void setAuthorityDao(AuthorityDao authorityDao) {
		this.authorityDao = authorityDao;
	}
}
