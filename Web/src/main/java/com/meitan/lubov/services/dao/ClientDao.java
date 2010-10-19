package com.meitan.lubov.services.dao;

import com.meitan.lubov.model.persistent.Client;
import com.meitan.lubov.services.commerce.ShoppingCart;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.webflow.execution.RequestContext;

import java.security.Principal;

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
	Client saveOrFetchUnregisteredClientByEmail(RequestContext requestContext, Client c) throws IllegalAccessException;

	AuthorityDao getAuthorityDao();

	void setAuthorityDao(AuthorityDao authorityDao);

	void mergeAnonymousClient(RequestContext requestContext, Client c) throws IllegalAccessException;

	@Transactional
	Client findByLoginOrCreateNew(Principal p);
}
