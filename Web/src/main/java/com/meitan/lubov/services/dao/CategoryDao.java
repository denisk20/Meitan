package com.meitan.lubov.services.dao;

import com.meitan.lubov.model.persistent.ProductCategory;
import com.meitan.lubov.services.dao.Dao;

import java.util.List;

/**
 * Date: Mar 5, 2010
 * Time: 10:01:40 PM
 *
 * @author denisk
 */
public interface CategoryDao extends Dao<ProductCategory, Long> {
	List<ProductCategory> getAll();
}
