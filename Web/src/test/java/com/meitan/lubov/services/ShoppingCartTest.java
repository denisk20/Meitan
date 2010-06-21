package com.meitan.lubov.services;

import com.meitan.lubov.model.PriceAware;
import com.meitan.lubov.model.components.Price;
import com.meitan.lubov.model.persistent.Product;
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

		ArrayList<PriceAware> products = testable.getItems();

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
}
