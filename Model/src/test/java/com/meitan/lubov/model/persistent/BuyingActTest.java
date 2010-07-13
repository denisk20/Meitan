package com.meitan.lubov.model.persistent;

import com.meitan.lubov.model.components.Name;
import com.meitan.lubov.model.components.Price;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Date: Jan 28, 2010
 * Time: 11:27:10 AM
 *
 * @author denisk
 */
public class BuyingActTest {
	private BuyingAct testable;
	
	@Before
	public void setUp() throws Exception {
		testable = new BuyingAct();
	}

	@Test
	public void testGetDate() throws Exception {
		final Date date = new Date();
		testable.setDate(date);

		assertEquals(date, testable.getDate());
	}

	@Test
	public void testGetClient() throws Exception {
		Client client = new Client();
		final Name name = new Name();
		name.setFirstName("John");
		name.setPatronymic("Van");
		name.setSecondName("Hagen");

		client.setName(name);

		testable.setClient(client);

		assertEquals(client, testable.getClient());
	}

	@Test
	public void testGetProducts() throws Exception {
		Product p1 = new Product();
		p1.setName("p1");
		ShoppingCartItem it1 = new ShoppingCartItem(p1, 3);
		p1.setPrice(new Price(new BigDecimal(1)));
		Product p2 = new Product();
		p2.setName("p1");
		p2.setPrice(new Price(new BigDecimal(1)));
		ShoppingCartItem it2 = new ShoppingCartItem(p2, 3);
		Product p3 = new Product();
		p3.setName("p1");
		p3.setPrice(new Price(new BigDecimal(1)));
		ShoppingCartItem it3 = new ShoppingCartItem(p3, 3);

		Set<ShoppingCartItem> products = new HashSet<ShoppingCartItem>();
		products.add(it1);
		products.add(it2);
		products.add(it3);

		testable.getProducts().addAll(products);

		assertEquals(products, testable.getProducts());

	}

	@Test
	public void testGetTotalPrice() throws Exception {
		final BigDecimal price1 = new BigDecimal(12.4);
		final BigDecimal price2 = new BigDecimal(8.7);
		final BigDecimal price3 = new BigDecimal(13.9);

		final int quantity1 = 4;
		final int quantity2 = 3;
		final int quantity3 = 7;

		Product p1 = new Product();
		p1.setName("p1");
		p1.setPrice(new Price(price1));
		ShoppingCartItem it1 = new ShoppingCartItem(p1, quantity1);

		Product p2 = new Product();
		p2.setName("p1");
		p2.setPrice(new Price(price2));
		ShoppingCartItem it2 = new ShoppingCartItem(p2, quantity2);

		Product p3 = new Product();
		p3.setName("p1");
		p3.setPrice(new Price(price3));
		ShoppingCartItem it3 = new ShoppingCartItem(p3, quantity3);

		Set<ShoppingCartItem> products = new HashSet<ShoppingCartItem>();
		products.add(it1);
		products.add(it2);
		products.add(it3);

		testable.getProducts().addAll(products);

		BigDecimal expectedPrice = price1.multiply(new BigDecimal(quantity1))
				.add(price2.multiply(new BigDecimal(quantity2)))
				.add(price3.multiply(new BigDecimal(quantity3)));

		assertEquals(new Price(expectedPrice), testable.getTotalPrice());
	}
}
