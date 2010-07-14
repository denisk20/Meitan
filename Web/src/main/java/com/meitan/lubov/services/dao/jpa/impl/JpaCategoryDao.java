package com.meitan.lubov.services.dao.jpa.impl;

import com.meitan.lubov.model.persistent.Category;
import com.meitan.lubov.model.persistent.Image;
import com.meitan.lubov.model.persistent.Product;
import com.meitan.lubov.services.dao.CategoryDao;
import com.meitan.lubov.services.dao.ImageDao;
import com.meitan.lubov.services.dao.ProductDao;
import com.meitan.lubov.services.dao.jpa.JpaDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	@Autowired
	private ImageDao imageDao;

	@Override
	@Transactional
	public void makeTransient(Category c) {
		super.makeTransient(c);

		for (Product p : c.getProducts()) {
			p.getCategories().remove(c);
		}

		Image image = c.getImage();

		if (image != null) {
			imageDao.removeImageFromEntity(c, image);
		}
	}

    @Override
    public ImageDao getImageDao() {
        return imageDao;
    }

    @Override
    public void setImageDao(ImageDao imageDao) {
        this.imageDao = imageDao;
    }

}
