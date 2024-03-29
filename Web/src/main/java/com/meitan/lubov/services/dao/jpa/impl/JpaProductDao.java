package com.meitan.lubov.services.dao.jpa.impl;

import com.meitan.lubov.model.persistent.Category;
import com.meitan.lubov.model.persistent.Image;
import com.meitan.lubov.model.persistent.Product;
import com.meitan.lubov.model.persistent.ShoppingCartItem;
import com.meitan.lubov.services.dao.CategoryDao;
import com.meitan.lubov.services.dao.ImageDao;
import com.meitan.lubov.services.dao.ProductDao;
import com.meitan.lubov.services.dao.ShoppingCartItemDao;
import com.meitan.lubov.services.dao.jpa.JpaDao;
import com.meitan.lubov.services.util.Selectable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

	@Autowired
	private ImageDao imageDao;

	@Autowired
	private ShoppingCartItemDao shoppingCartItemDao;

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public ArrayList<Product> getNew() {
		ArrayList<Product> result = (ArrayList<Product>)
				em.createNamedQuery("getProductsNew").getResultList();
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public ArrayList<Product> getTop() {
		ArrayList<Product> result = (ArrayList<Product>)
				em.createNamedQuery("getProductsTop").getResultList();
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
	@Transactional
    public void makePersistent(Product entity) {
        Object[] categoryIds = entity.getCategoriesIdArray();

        super.makePersistent(entity);
        if (categoryIds != null) {
            for (Object o : categoryIds) {
                Long id = Long.parseLong((String) o);
                Category c = categoryDao.findById(id);
                entity.getCategories().add(c);
                c.getProducts().add(entity);
                //todo do we need this?
                //categoryDao.makePersistent(c);
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
		//em.merge(p);
	}

    @Override
	@Transactional
    public List<Product> findAll() {
        List<Product> result = super.findAll();
        return getDistinct(result);
    }

    @Override
	@Transactional
    public List<Product> findByExample(Product exampleInstance, String... excludeProperty) {
        List<Product> result = super.findByExample(exampleInstance, excludeProperty);
        return getDistinct(result);
    }

	@Override
	@Transactional
	public void deleteById(Long productId) {
		Product p = findById(productId);
		Set<Category> categories = p.getCategories();

		for (Category c : categories) {
			c.getProducts().remove(p);
		}
		int imageCount = p.getImages().size();
		for (int i = 0; i < imageCount; i++) {
			Image image = p.getImages().iterator().next();
			imageDao.removeImageFromEntity(p, image);
		}
		//delete avatar
//		Image avatar = p.getAvatar();
//		imageDao.makeTransient(avatar);
//		p.setAvatar(null);

		//delete all ShoppingCartItems
		List<ShoppingCartItem> items = shoppingCartItemDao.getForProduct(productId);
		for (ShoppingCartItem it : items) {
			shoppingCartItemDao.deleteById(it.getId());
		}
		super.deleteById(productId);
	}

	@Override
	@Transactional
	public Product getProductByName(String name) {
		List resultList = em.createNamedQuery("getProductByName").setParameter("name", name).getResultList();
		int size = resultList.size();
		if (size > 1) {
			throw new IllegalStateException("Multiple products with name " + name);
		}
		if (size > 0) {
			return (Product) resultList.get(0);
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

    @Override
    public CategoryDao getCategoryDao() {
        return categoryDao;
    }

    @Override
    public void setCategoryDao(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }
}
