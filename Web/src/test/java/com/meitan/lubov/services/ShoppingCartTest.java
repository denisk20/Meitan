package com.meitan.lubov.services;

import com.meitan.lubov.model.PriceAware;
import com.meitan.lubov.model.components.Price;
import com.meitan.lubov.model.persistent.Product;
import com.meitan.lubov.model.persistent.ShoppingCartItem;
import com.meitan.lubov.services.commerce.ShoppingCartImpl;
import org.junit.Test;
import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * @author denis_k
 *         Date: 21.06.2010
 *         Time: 13:56:45
 */
public class ShoppingCartTest {
	ShoppingCartImpl testable = new ShoppingCartImpl();

	@Test
	public void testGetItems() {
		PriceAware prod1 = new Product("p1");
		PriceAware prod2 = new Product("p2");

		testable.addItem(prod1);
		testable.addItem(prod2);

		ArrayList<PriceAware> products = testable.getPriceAwares();

		assertTrue(products.contains(prod1));
		assertTrue(products.contains(prod2));
	}

	@Test
	public void testGetTotalPriceItems() {
		BigDecimal p1Price = new BigDecimal(24.5);
		BigDecimal p2Price = new BigDecimal(123.123);

		Product prod1 = new Product("p1");
		Product prod2 = new Product("p2");

		prod1.setPrice(new Price(p1Price));
		prod2.setPrice(new Price(p2Price));

		testable.addItem(prod1);
		testable.addItem(prod2);

		BigDecimal total = testable.getTotalPrice();

		assertEquals(p1Price.add(p2Price), total);
	}

	@Test
	public void testDuplicateShoppingCartItems() {
		String name = "p1";
		String description = "desc";
		Product p1 = new Product(name);
		p1.setDescription(description);

		Product p2 = new Product(name);
		p2.setDescription(description);

		testable.addItem(p1);
		testable.addItem(p2);

		ArrayList<ShoppingCartItem> items = testable.getItems();
		assertEquals("Wrong number of items", 1, items.size());

		ShoppingCartItem item = items.get(0);
		assertEquals(p1, item.getItem());
		assertEquals(new Integer(2), item.getQuantity());
	}

	@Test(expected = IllegalStateException.class)
	public void testDuplicateItemsState() {
		String name = "p1";
		String description = "desc";
		Product p1 = new Product(name);
		p1.setDescription(description);

		testable.getItems().add(new ShoppingCartItem(p1, 1));
		testable.getItems().add(new ShoppingCartItem(p1, 1));

		testable.getQuantity(p1);
	}
}
