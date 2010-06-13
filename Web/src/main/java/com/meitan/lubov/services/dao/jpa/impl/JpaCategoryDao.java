package com.meitan.lubov.services.dao.jpa.impl;

import com.meitan.lubov.model.persistent.Category;
import com.meitan.lubov.model.persistent.Image;
import com.meitan.lubov.services.dao.CategoryDao;
import com.meitan.lubov.services.dao.ImageDao;
import com.meitan.lubov.services.dao.jpa.JpaDao;
import com.meitan.lubov.services.util.Selectable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

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
	public void merge(Category c) {
		
		em.merge(c);
	}

	@Override
	//todo unit test this
	public void makeTransient(Category entity) {
		super.makeTransient(entity);
		Image image = entity.getImage();
		//todo what about deleting image from DB? Unit test this
		//todo what about the products?????????????
		if (image != null) {
			imageDao.deleteFromDisk(image);
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
