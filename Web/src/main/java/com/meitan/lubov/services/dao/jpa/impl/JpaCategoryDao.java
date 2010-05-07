package com.meitan.lubov.services.dao.jpa.impl;

import com.meitan.lubov.model.persistent.Category;
import com.meitan.lubov.model.persistent.Image;
import com.meitan.lubov.services.dao.CategoryDao;
import com.meitan.lubov.services.dao.jpa.JpaDao;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;

/**
 * Date: Mar 5, 2010
 * Time: 10:04:30 PM
 *
 * @author denisk
 */
@Service("categoryDao")
@Repository
public class JpaCategoryDao extends JpaDao<Category, Long> implements CategoryDao {

	@Override
	@Transactional
	public void dropImage(Category c) {
		Image image = c.getImage();
		if (image == null) {
			//todo log this
			return;
		}
		String path = image.getAbsolutePath();
		if (path == null) {
			//todo log this as well
		} else {
			File file = new File(path);
			if (!file.exists()) {
				throw new IllegalStateException("No corresponding image file for image " + image);
			}
			boolean deleted = file.delete();
			if (!deleted) {
				throw new IllegalArgumentException("Can't delete image: " + file);
			}
		}
		c.setImage(null);
	}

	@Override
	public void makeTransient(Category entity) {
		super.makeTransient(entity);
		dropImage(entity);
	}
}
