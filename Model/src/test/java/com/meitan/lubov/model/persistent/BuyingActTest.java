package com.meitan.lubov.model.persistent;

import com.meitan.lubov.model.Name;
import com.meitan.lubov.model.Price;
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
		p1.setPrice(new Price(new BigDecimal(1)));
		Product p2 = new Product();
		p2.setName("p1");
		p2.setPrice(new Price(new BigDecimal(1)));
		Product p3 = new Product();
		p3.setName("p1");
		p3.setPrice(new Price(new BigDecimal(1)));

		Set<Product> products = new HashSet<Product>();
		products.add(p1);
		products.add(p2);
		products.add(p3);

		testable.getProducts().addAll(products);

		assertEquals(products, testable.getProducts());

	}

	@Test
	public void testGetTotalPrice() throws Exception {
		Price price = new Price(new BigDecimal(1));
		testable.setTotalPrice(price);

		assertEquals(price, testable.getTotalPrice());
	}
}
