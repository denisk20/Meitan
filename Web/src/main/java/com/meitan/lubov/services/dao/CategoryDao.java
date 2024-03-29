package com.meitan.lubov.services.dao;

import com.meitan.lubov.model.persistent.Category;
import com.meitan.lubov.services.dao.Dao;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Date: Mar 5, 2010
 * Time: 10:01:40 PM
 *
 * @author denisk
 */
public interface CategoryDao extends Dao<Category, Long>{
	Category merge(Category c);

    ImageDao getImageDao();

    void setImageDao(ImageDao imageDao);

	@Transactional
	Category getCategoryByName(String name);
}
