package com.meitan.lubov.services.dao;

import com.meitan.lubov.model.persistent.ShoppingCartItem;

import java.util.List;

/**
 * Date: Jul 12, 2010
 * Time: 5:29:05 PM
 *
 * @author denisk
 */
public interface ShoppingCartItemDao extends Dao<ShoppingCartItem, Long>{
	List<ShoppingCartItem> getForProduct(Long productId);

	BuyingActDao getBuyingActDao();

	void setBuyingActDao(BuyingActDao buyingActDao);
}
