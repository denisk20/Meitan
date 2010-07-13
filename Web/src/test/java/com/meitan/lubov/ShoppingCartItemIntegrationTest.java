package com.meitan.lubov;

import com.meitan.lubov.model.persistent.BuyingAct;
import com.meitan.lubov.model.persistent.Product;
import com.meitan.lubov.model.persistent.ShoppingCartItem;
import com.meitan.lubov.services.dao.BuyingActDao;
import com.meitan.lubov.services.dao.Dao;
import com.meitan.lubov.services.dao.ProductDao;
import com.meitan.lubov.services.dao.ShoppingCartItemDao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

import java.util.List;

/**
 * @author denis_k
 *         Date: 12.07.2010
 *         Time: 23:21:07
 */
public class ShoppingCartItemIntegrationTest extends GenericIntegrationTest<ShoppingCartItem> {
	@Autowired
	//todo make test dao
	private ShoppingCartItemDao testShoppingCartItemDao;
	@Autowired
	private ProductDao testProductDao;

	@Autowired
	private BuyingActDao testBuyingActDao;

	private static final int EXPECTED_ITEMS_COUNT = 2;

	@Override
	protected void setUpBeanNames() {
		beanNames.add("ent_it1");
		beanNames.add("ent_it2");
		beanNames.add("ent_it3");
		beanNames.add("ent_it4");
	}

	@Override
	protected Dao<ShoppingCartItem, Long> getDAO() {
		return testShoppingCartItemDao;
	}

	@Test
	public void testGetByProduct() {
		Product productFromSpring = (Product) applicationContext.getBean("ent_megaCream");
		List<Product> byExample = testProductDao.findByExample(productFromSpring);
		assertEquals(1, byExample.size());
		Product productFromDb = byExample.get(0);

		List<ShoppingCartItem> result = testShoppingCartItemDao.getForProduct(productFromDb.getId());
		assertEquals(EXPECTED_ITEMS_COUNT, result.size());

		assertTrue(result.contains(applicationContext.getBean("ent_it1")));
		assertTrue(result.contains(applicationContext.getBean("ent_it3")));
	}

	@Test
	public void testDeleteById() {
		ShoppingCartItem item = beansFromDb.get(0);
		List<BuyingAct> acts = testBuyingActDao.findForCartItem(item.getId());
		assertTrue(acts.size() > 0);

		testShoppingCartItemDao.deleteById(item.getId());
		testShoppingCartItemDao.flush();

		for (BuyingAct act : acts) {
			assertFalse(act.getProducts().contains(item));
		}
	}
}
