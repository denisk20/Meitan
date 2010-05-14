package com.meitan.lubov.services.dao.jpa.impl;

import com.meitan.lubov.model.persistent.Category;
import com.meitan.lubov.model.persistent.Image;
import com.meitan.lubov.services.dao.CategoryDao;
import com.meitan.lubov.services.dao.jpa.JpaDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
	private Log log = LogFactory.getLog(getClass());
	@Override
	@Transactional
	public void dropImage(Category c) {
		Image image = c.getImage();
		if (image == null) {
			log.error("Image was null for category " + c);
		} else {
			c.setImage(null);

			String path = image.getAbsolutePath();
			if (path == null) {
				log.error("Path was null for image " + image);
			} else {
				File file = new File(path);
				if (!file.exists()) {
					//can't throw exception here...
					log.error("No corresponding image file for image " + image);
				} else {
					boolean deleted = file.delete();
					if (!deleted) {
						throw new IllegalArgumentException("Can't delete image: " + file);
					}
				}
			}
		}
	}

	@Override
	public void makeTransient(Category entity) {
		super.makeTransient(entity);
		dropImage(entity);
	}
}
