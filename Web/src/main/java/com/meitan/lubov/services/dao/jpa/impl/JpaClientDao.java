package com.meitan.lubov.services.dao.jpa.impl;

import com.meitan.lubov.model.persistent.BuyingAct;
import com.meitan.lubov.model.persistent.Client;
import com.meitan.lubov.services.commerce.ShoppingCart;
import com.meitan.lubov.services.dao.BuyingActDao;
import com.meitan.lubov.services.dao.ClientDao;
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

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public Client getByLogin(String login) {
		List<Client> result = em.createNamedQuery("getClientByLogin")
				.setParameter("login", login)
				.getResultList();
		if (result.size() != 1) {
			throw new IllegalStateException("Multiple users with login " + login);
		}
		return result.get(0);
	}

	@Override
	@Transactional
	public void buyGoods(String login, ShoppingCart cart) {
		Client client = getByLogin(login);
		BuyingAct act = new BuyingAct(new Date(), client);

		//for()
	}

	@Override
	public void setBuyingActDao(BuyingActDao buyingActDao) {
		this.buyingActDao = buyingActDao;
	}

	@Override
	public BuyingActDao getBuyingActDao() {
		return buyingActDao;
	}
}
