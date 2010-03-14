package com.meitan.lubov;

import com.meitan.lubov.model.persistent.Category;
import com.meitan.lubov.model.persistent.Product;
import com.meitan.lubov.services.dao.CategoryDao;
import com.meitan.lubov.services.dao.Dao;
import com.meitan.lubov.services.dao.ProductDao;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;

/**
 * @author denis_k
 *         Date: 14.03.2010
 *         Time: 10:23:11
 */
public class ProductIntegrationTest extends GenericIntegrationTest<Product>{
    @Autowired
    private ProductDao productDao;
    @Autowired
    private CategoryDao categoryDao;
    private static final Integer EXPECTED_NEW_PRODUCTS_COUNT = 1;
    private static final int EXPECTED_PRODUCTS_FOR_CATEGORY_COUNT = 1;

    @Override
    protected void setUpBeanNames() {
        beanNames.add("ent_megaCream");
        beanNames.add("ent_gigaCream");
    }

    @Override
    protected Dao<Product, Long> getDAO() {
        return productDao;
    }

    @Override
    protected void compareAdditionalProperties(Product beanFromSpring, Product beanFromDB) {
        assertThat(beanFromSpring.getCategories(), is(beanFromDB.getCategories()));
        assertThat(beanFromSpring.getImages(), is(beanFromDB.getImages()));
        assertThat(beanFromSpring.getPrice(), is(beanFromDB.getPrice()));
        assertThat(beanFromSpring.getPurchases(), is(beanFromDB.getPurchases()));
    }

    @Test
    public void testGetNewProducts() {
        List<Product> newProducts = productDao.getNew();
        assertThat(EXPECTED_NEW_PRODUCTS_COUNT, is(newProducts.size()));
    }

    @Test
    public void testProductsForCategory() {
        Category category = (Category) applicationContext.getBean("ent_creams");
        List<Category> categories = categoryDao.findByExample(category);
        assertThat(categories.size(), is(1));

        category = categories.get(0);
        List<Product> products = productDao.getForCategory(category.getId());
        assertThat(categories.size(), is(EXPECTED_PRODUCTS_FOR_CATEGORY_COUNT));
    }
}
