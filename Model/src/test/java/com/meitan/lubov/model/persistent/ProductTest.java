package com.meitan.lubov.model.persistent;

import com.meitan.lubov.model.components.Name;
import com.meitan.lubov.model.components.Price;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Date: Jan 27, 2010
 * Time: 7:35:05 PM
 *
 * @author denisk
 */
public class ProductTest {
	private Product testable;

	@Before
	public void init() {
		testable = new Product();
	}

	@Test
	public void testGetName() throws Exception {
		final String someName = "some name";
		testable.setName(someName);
		assertEquals(someName, testable.getName());
	}

	@Test
	public void testGetDescription() throws Exception {
		final String description = "some description";
		testable.setDescription(description);
		assertEquals(description, testable.getDescription());
	}

	@Test
	public void testIsNew() throws Exception {
		final boolean neww = true;
		testable.setNew(neww);
		assertEquals(neww, testable.isNew());
	}

	@Test
	public void testGetCategories() throws Exception {
		Category cat1 = new Category();
		cat1.setName("some name");
		Category cat2 = new Category();
		cat2.setName("some name");

		HashSet<Category> categories = new HashSet<Category>();
		categories.add(cat1);
		categories.add(cat2);
		testable.getCategories().addAll(categories);

		assertEquals(categories, testable.getCategories());
	}

	@Test
	public void testGetImages() throws Exception {
		Image img1 = new Image("some url");
		Image img2 = new Image("some url");

		Set<Image> images = new HashSet<Image>();
		images.add(img1);
		images.add(img2);

		testable.getImages().addAll(images);

		assertEquals(images, testable.getImages());
	}

	@Test
	public void testGetPrice() throws Exception {
		Price price = new Price(new BigDecimal(10));
		testable.setPrice(price);

		assertEquals(price, testable.getPrice());
	}

	@Test
	public void testGetBoughts() {
		BuyingAct act1 = new BuyingAct();
		final Client client1 = new Client();
		client1.setName(new Name());
		client1.setEmail("a@b.com");
		act1.setClient(client1);
		act1.setDate(new Date());
		BuyingAct act2 = new BuyingAct();
		final Client client2 = new Client();
		client2.setName(new Name());
		client2.setEmail("c@d.com");
		act2.setClient(client2);
		act2.setDate(new Date());

		Set<BuyingAct> purchases = new HashSet<BuyingAct>();
		purchases.add(act1);
		purchases.add(act2);

		testable.getPurchases().addAll(purchases);
		assertEquals(purchases, testable.getPurchases());
	}
}
