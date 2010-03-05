package com.meitan.lubov.services.dao;

import com.meitan.lubov.model.persistent.Product;

import java.util.ArrayList;

/**
 * Date: Mar 4, 2010
 * Time: 2:07:26 PM
 *
 * @author denisk
 */
public interface ProductDao extends Dao <Product, Long>{
	ArrayList<Product> getAll();

	ArrayList<Product> getNew();

	ArrayList<Product> getForCategory(Long categoryId);
}
