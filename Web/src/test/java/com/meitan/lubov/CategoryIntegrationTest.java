package com.meitan.lubov;

import com.meitan.lubov.model.persistent.Category;
import com.meitan.lubov.model.persistent.Image;
import com.meitan.lubov.model.persistent.Product;
import com.meitan.lubov.services.dao.CategoryDao;
import com.meitan.lubov.services.dao.Dao;
import com.meitan.lubov.services.dao.ImageDao;
import com.meitan.lubov.services.util.FileBackupRestoreManager;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.PersistenceException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * @author denis_k
 *         Date: 14.03.2010
 *         Time: 10:27:22
 */
public class CategoryIntegrationTest extends GenericIntegrationTest<Category> {
	private final static Logger log = Logger.getLogger(CategoryIntegrationTest.class);
	@Autowired
	private CategoryDao testCategoryDao;
	@Autowired
	private ImageDao testImageDao;

	private static final Integer EXPECTED_CATEGORY_COUNT = 1;

	private FileBackupRestoreManager creamsImageRestoreManager;

	@Override
	protected void setUpBeanNames() {
		beanNames.add("ent_creams");
	}

	@Override
	protected Dao<Category, Long> getDAO() {
		return testCategoryDao;
	}

	@Override
	protected void compareAdditionalProperties(Category beanFromSpring, Category beanFromDB) {
		assertThat(beanFromSpring.getImage(), is(beanFromDB.getImage()));
		assertThat(beanFromSpring.getProducts(), is(beanFromDB.getProducts()));
	}

	@Test
	public void testFindAll() {
		List<Category> all = testCategoryDao.findAll();
		assertThat(EXPECTED_CATEGORY_COUNT, is(all.size()));
	}

	@Test(expected = PersistenceException.class)
	public void testNullName() {
		Category c = beansFromDb.get(0);
		c.setName(null);

		testCategoryDao.makePersistent(c);
		testCategoryDao.flush();
	}

	@Test(expected = PersistenceException.class)
	public void testNonUniqueName() {
		Category c = beansFromDb.get(0);
		String name = c.getName();

		Category created = new Category(name);

		testCategoryDao.makePersistent(created);

		testCategoryDao.flush();
	}

	@Test
	public void testMakeTransient() throws IOException {
		Category c = beansFromDb.get(0);
		ArrayList<Product> products = new ArrayList<Product>(c.getProducts());
        Image image = c.getImage();

        creamsImageRestoreManager = new FileBackupRestoreManager(testImageDao.getPathPrefix() + image.getUrl());

        String absolutePath = testImageDao.getPathPrefix() + image.getUrl();
		File imageFile = new File(absolutePath);
		assertTrue("File doesn't exist for path " + absolutePath, imageFile.exists());

		creamsImageRestoreManager.backup();

		try {
			testCategoryDao.makeTransient(c);

			Category deletedCategory = testCategoryDao.findById(c.getId());
			assertNull("Category should have been deleted", deletedCategory);

			Image deletedImage = testImageDao.findById(image.getId());
			assertNull("Image should have been deleted", deletedImage);

			assertFalse("file should have been deleted, but still exists", imageFile.exists());

			for (Product p : products) {
				assertFalse("Category wasn't deleted from product: " + p, p.getCategories().contains(c));
			}
		} finally {
			creamsImageRestoreManager.restore();
			assertTrue("Failed to restore image " + creamsImageRestoreManager.getBasePath(), imageFile.exists());
		}

	}
}
