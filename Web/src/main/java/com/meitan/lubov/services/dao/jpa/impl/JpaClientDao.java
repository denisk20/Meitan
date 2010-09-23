package com.meitan.lubov.services.dao.jpa.impl;

import com.meitan.lubov.model.persistent.BuyingAct;
import com.meitan.lubov.model.persistent.Client;
import com.meitan.lubov.model.persistent.ShoppingCartItem;
import com.meitan.lubov.services.commerce.ShoppingCart;
import com.meitan.lubov.services.dao.BuyingActDao;
import com.meitan.lubov.services.dao.ClientDao;
import com.meitan.lubov.services.dao.ShoppingCartItemDao;
import com.meitan.lubov.services.dao.jpa.JpaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

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

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public Client getByLogin(String login) {
		List<Client> result = em.createNamedQuery("getClientByLogin")
				.setParameter("login", login)
				.getResultList();
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
	public void saveOrFetchClientByEmail(Client c) {
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
		} else {
			if (resultList.size() != 1) {
				throw new IllegalStateException("Multiple users with email " + email);
			}
		}

	}

	@Override
	@Transactional
	//todo implement and test it properly
	public void deleteById(Long id) {
		super.deleteById(id);
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
	
}
