package com.meitan.lubov.services;

import com.meitan.lubov.model.persistent.Product;
import com.meitan.lubov.model.persistent.Category;

import java.util.ArrayList;

/**
 * Date: Feb 7, 2010
 * Time: 8:18:02 AM
 *
 * @author denisk
 */

//todo split this service into individual DAOs
public interface MeitanService {
	ArrayList<Product> getNewProducts();

	ArrayList<Product> getAllProducts();

	ArrayList<Product> getProductsForCategory(long categoryId);

	ArrayList<Category> getAllCategories();

	void persistProduct(Product product);
}
