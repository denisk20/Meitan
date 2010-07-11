package com.meitan.lubov.services.dao;

import com.meitan.lubov.model.persistent.Client;
import com.meitan.lubov.services.commerce.ShoppingCart;

/**
 * Date: Mar 5, 2010
 * Time: 10:15:02 PM
 *
 * @author denisk
 */
public interface ClientDao extends Dao<Client, Long>{
	Client getByLogin(String login);

	void buyGoods(String login, ShoppingCart cart);

	void setBuyingActDao(BuyingActDao buyingActDao);

	BuyingActDao getBuyingActDao();
}
