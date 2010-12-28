package com.meitan.lubov.services.dao;

import com.meitan.lubov.model.persistent.Category;
import com.meitan.lubov.model.persistent.Product;
import com.meitan.lubov.services.util.Selectable;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Date: Mar 4, 2010
 * Time: 2:07:26 PM
 *
 * @author denisk
 */
public interface ProductDao extends Dao <Product, Long>{
	ArrayList<Product> getNew();

	ArrayList<Product> getForCategory(Long categoryId);

	void assignCategoriesToProduct(Product p, Collection<Selectable<Category>> selectableCategories);

    ImageDao getImageDao();

    void setImageDao(ImageDao imageDao);

    CategoryDao getCategoryDao();

    void setCategoryDao(CategoryDao categoryDao);

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	ArrayList<Product> getTop();
}
