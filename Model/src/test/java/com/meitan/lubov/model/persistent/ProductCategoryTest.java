package com.meitan.lubov.model.persistent;

import com.meitan.lubov.model.persistent.Image;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
/**
 * Date: Jan 27, 2010
 * Time: 7:56:02 PM
 *
 * @author denisk
 */
public class ProductCategoryTest {
	private Category testable;
	@Before
	public void setUp() throws Exception {
		testable = new Category();
	}

	@Test
	public void testGetName() throws Exception {
		String name = "name";
		testable.setName(name);

		assertEquals(name, testable.getName());
	}

	@Test
	public void testGetProducts() throws Exception {
		Product product1 = new Product();
		product1.setName("name");
		Product product2 = new Product();
		product2.setName("name");

		Set<Product>products = new HashSet<Product>();
		products.add(product1);
		products.add(product2);

		testable.getProducts().addAll(products);
		assertEquals(products, testable.getProducts());
	}

	@Test
	public void testGetImage() {
		Image image = new Image("url");
		testable.setImage(image);

		assertEquals(image, testable.getImage());
	}
}
