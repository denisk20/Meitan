package com.meitan.lubov;

import com.meitan.lubov.model.persistent.Category;
import com.meitan.lubov.model.persistent.Image;
import com.meitan.lubov.services.dao.CategoryDao;
import com.meitan.lubov.services.dao.Dao;
import com.meitan.lubov.services.dao.ImageDao;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.PersistenceException;
import java.io.File;
import java.io.IOException;
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
	private CategoryDao categoryDao;
	@Autowired
	private ImageDao imageDao;

	private static final Integer EXPECTED_CATEGORY_COUNT = 1;

	@Autowired
	private com.meitan.lubov.services.util.FileBackupRestoreManager creamsImageRestoreManager;

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

	@Test(expected = PersistenceException.class)
	public void testNullName() {
		Category c = beansFromDb.get(0);
		c.setName(null);

		categoryDao.makePersistent(c);
		categoryDao.flush();
	}

	@Test(expected = PersistenceException.class)
	public void testNonUniqueName() {
		Category c = beansFromDb.get(0);
		String name = c.getName();

		Category created = new Category(name);

		categoryDao.makePersistent(created);

		categoryDao.flush();
	}

	@Test
	public void testMakeTransient() throws IOException {
		Category c = beansFromDb.get(0);
		Image image = c.getImage();
		String absolutePath = image.getAbsolutePath();
		File imageFile = new File(absolutePath);
		assertTrue("File doesn't exist for path " + absolutePath, imageFile.exists());

		creamsImageRestoreManager.backup();

		try {
			categoryDao.makeTransient(c);

			Category deletedCategory = categoryDao.findById(c.getId());
			assertNull("Category should have been deleted", deletedCategory);

			Image deletedImage = imageDao.findById(image.getId());
			assertNull("Image should have been deleted", deletedImage);

			assertFalse("file should have been deleted, but still exists", imageFile.exists());
		} finally {
			creamsImageRestoreManager.restore();
			assertTrue("Failed to restore image " + creamsImageRestoreManager.getBasePath(), imageFile.exists());
		}

	}
}
