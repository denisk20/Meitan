package com.meitan.lubov.services.dao;

import com.meitan.lubov.model.persistent.Client;
import com.meitan.lubov.services.commerce.ShoppingCart;
import org.springframework.transaction.annotation.Transactional;

/**
 * Date: Mar 5, 2010
 * Time: 10:15:02 PM
 *
 * @author denisk
 */
public interface ClientDao extends Dao<Client, Long>{
	Client getByLogin(String login);

	void buyGoods(ShoppingCart cart, String login);

	void setBuyingActDao(BuyingActDao buyingActDao);

	BuyingActDao getBuyingActDao();

	ShoppingCartItemDao getShoppingCartItemDao();

	void setShoppingCartItemDao(ShoppingCartItemDao shoppingCartItemDao);

	@Transactional
	//todo u-test
	void saveOrFetchClientByEmail(Client c);
}
