package com.meitan.lubov.services.dao.jpa.impl;

import com.meitan.lubov.model.persistent.ShoppingCartItem;
import com.meitan.lubov.services.dao.ShoppingCartItemDao;
import com.meitan.lubov.services.dao.jpa.JpaDao;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Date: Jul 12, 2010
 * Time: 5:29:50 PM
 *
 * @author denisk
 */
@Repository
@Service("shoppingCartItemDao")
public class JpaShoppingCartItemDao extends JpaDao<ShoppingCartItem, Long> implements ShoppingCartItemDao{
	@Override
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<ShoppingCartItem> getForProduct(Long productId) {
		List<ShoppingCartItem> result = em.createNamedQuery("findByProduct").setParameter("productId", productId).getResultList();
		return result;
	}

	@Override
	public List<ShoppingCartItem> findByExample(ShoppingCartItem exampleInstance, String... excludeProperty) {
		List<ShoppingCartItem> result = super.findByExample(exampleInstance, excludeProperty);
		return getDistinct(result);
	}
}
