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
	@Transactional
	public Category getCategoryByName(String name) {
		List resultList = em.createNamedQuery("getCategoryByName").setParameter("name", name).getResultList();
		int size = resultList.size();
		if (size > 1) {
			throw new IllegalStateException("Multiple categories with name " + name);
		}
		if (size > 0) {
			return (Category) resultList.get(0);
		} else {
			return null;
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
