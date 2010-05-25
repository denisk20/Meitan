package com.meitan.lubov.services.dao.jpa.impl;

import com.meitan.lubov.model.persistent.Category;
import com.meitan.lubov.model.persistent.Product;
import com.meitan.lubov.services.dao.CategoryDao;
import com.meitan.lubov.services.dao.ProductDao;
import com.meitan.lubov.services.dao.jpa.JpaDao;
import com.meitan.lubov.services.util.Selectable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * Date: Mar 4, 2010
 * Time: 2:10:26 PM
 *
 * @author denisk
 */
@Service("productDao")
@Repository
public class JpaProductDao extends JpaDao<Product, Long> implements ProductDao {

private final Log log = LogFactory.getLog(getClass());
    @Autowired
    private CategoryDao categoryDao;

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public ArrayList<Product> getNew() {
		ArrayList<Product> result = (ArrayList<Product>)
				em.createNamedQuery("getProductsNew").getResultList();
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public ArrayList<Product> getForCategory(Long categoryId) {
		ArrayList<Product> result = (ArrayList<Product>)
				em.createNamedQuery("getProductsForCategory")
						.setParameter("categoryId", categoryId)
						.getResultList();
		return result;
	}

    @Override
    public void makePersistent(Product entity) {
        Object[] categoryIds = entity.getCategoriesIdArray();

        super.makePersistent(entity);
        if (categoryIds != null) {
            for (Object o : categoryIds) {
                Long id = Long.parseLong((String) o);
                Category c = categoryDao.findById(id);
                entity.getCategories().add(c);
                c.getProducts().add(entity);
                categoryDao.makePersistent(c);
            }
        } else {
            log.info("CategoriesIdArray was empty for product " + entity);
        }
    }

	@Override
	@Transactional
	public void assignCategoriesToProduct(Product p, Collection<Selectable<Category>> selectableCategories) {
		p = findById(p.getId());

		for (Selectable<Category> selectable : selectableCategories) {
			Category category = selectable.getItem();
			if (selectable.isSelected()) {
				category.getProducts().add(p);
				p.getCategories().add(category);
			} else {
				category.getProducts().remove(p);
				p.getCategories().remove(category);
			}
		}
		em.merge(p);
	}
}
