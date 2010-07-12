package com.meitan.lubov.model.persistent;

import com.meitan.lubov.model.components.Name;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Date: Jan 28, 2010
 * Time: 9:21:13 PM
 *
 * @author denisk
 */
public class ShoppingCartItemTest {
	private ShoppingCartItem testable;

	@Before
	public void setUp() throws Exception {
		testable = new ShoppingCartItem();
	}

	@Test
	public void testId() {
		Long id = new Long(1);
		testable.setId(id);

		assertEquals(id, testable.getId());
	}

	@Test
	public void testQuantity() {
		Integer quantity = 5;
		testable.setQuantity(quantity);

		assertEquals(quantity, testable.getQuantity());
	}

	@Test
	public void testProduct() {
		Product p = new Product("p1");

		testable.setItem(p);

		assertEquals(p, testable.getItem());
	}
}