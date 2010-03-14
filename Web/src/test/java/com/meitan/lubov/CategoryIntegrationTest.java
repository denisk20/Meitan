package com.meitan.lubov;

import com.meitan.lubov.model.persistent.Category;
import com.meitan.lubov.services.dao.CategoryDao;
import com.meitan.lubov.services.dao.Dao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author denis_k
 *         Date: 14.03.2010
 *         Time: 10:27:22
 */
public class CategoryIntegrationTest extends GenericIntegrationTest<Category> {
    @Autowired
    private CategoryDao categoryDao;
    private static final Integer EXPECTED_CATEGORY_COUNT = 1;

    @Override
    protected void setUpBeanNames() {
        beanNames.add("ent_creams");
    }

    @Override
    protected Dao<Category, Long> getDAO() {
        return categoryDao;
    }

    @Override
    protected void compareAdditionalProperties(Category beanFromSpring, Category beanFromDB) {
        assertThat(beanFromSpring.getImage(), is(beanFromDB.getImage()));
        assertThat(beanFromSpring.getProducts(), is(beanFromDB.getProducts()));
    }

    @Test
    public void testFindAll() {
        List<Category> all = categoryDao.findAll();
        assertThat(EXPECTED_CATEGORY_COUNT, is(all.size()));
    }
}
