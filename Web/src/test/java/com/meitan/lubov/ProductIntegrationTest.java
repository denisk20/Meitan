package com.meitan.lubov;

import com.meitan.lubov.model.persistent.Category;
import com.meitan.lubov.model.persistent.Image;
import com.meitan.lubov.model.persistent.Product;
import com.meitan.lubov.services.dao.CategoryDao;
import com.meitan.lubov.services.dao.Dao;
import com.meitan.lubov.services.dao.ImageDao;
import com.meitan.lubov.services.dao.ProductDao;
import com.meitan.lubov.services.util.FileBackupRestoreManager;
import com.meitan.lubov.services.util.Selectable;
import com.meitan.lubov.services.util.SelectableImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.PersistenceException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;

/**
 * @author denis_k
 *         Date: 14.03.2010
 *         Time: 10:23:11
 */
public class ProductIntegrationTest extends GenericIntegrationTest<Product> {
	@Autowired
	private ProductDao testProductDao;
	@Autowired
	private CategoryDao testCategoryDao;
	@Autowired
	private ImageDao testImageDao;
	private static final Integer EXPECTED_NEW_PRODUCTS_COUNT = 1;
	private static final int EXPECTED_PRODUCTS_FOR_CATEGORY_COUNT = 2;

	@Override
	protected void setUpBeanNames() {
		beanNames.add("ent_megaCream");
		beanNames.add("ent_gigaCream");
	}

	@Override
	protected Dao<Product, Long> getDAO() {
		return testProductDao;
	}

	@Override
	protected String[] getExcludedProperties() {
		return new String[] {"description"};
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
		List<Product> newProducts = testProductDao.getNew();
		assertThat(EXPECTED_NEW_PRODUCTS_COUNT, is(newProducts.size()));
	}

	@Test
	public void testProductsForCategory() {
		Category category = (Category) applicationContext.getBean("ent_creams");
		List<Category> categories = testCategoryDao.findByExample(category);
		assertThat(categories.size(), is(1));

		category = categories.get(0);
		List<Product> products = testProductDao.getForCategory(category.getId());
		assertThat(products.size(), is(EXPECTED_PRODUCTS_FOR_CATEGORY_COUNT));
	}

	@Test(expected = PersistenceException.class)
	public void nullableName() {
		Product p = beansFromDb.get(0);
		p.setName(null);

		testProductDao.makePersistent(p);
		testProductDao.flush();
	}

	@Test(expected = PersistenceException.class)
	public void nonUniqueName() {
		Product p = beansFromDb.get(0);

		Product created = new Product(p.getName());

		testProductDao.makePersistent(created);
		testProductDao.flush();
	}

	@Test
	public void testMakePersistent() {
		String c1Name = "new category 1";
		String c2Name = "new category 2";

		Category c1 = new Category(c1Name);
		Category c2 = new Category(c2Name);

		testCategoryDao.makePersistent(c1);
		testCategoryDao.makePersistent(c2);

		Product p = new Product("new product");
		String[] cIds = new String[2];
		cIds[0] = c1.getId().toString();
		cIds[1] = c2.getId().toString();

		p.setCategoriesIdArray(cIds);

		testProductDao.makePersistent(p);

		Set<Category> categorySet = p.getCategories();
		assertEquals("Wrong number of categories in product " + p, 2, categorySet.size());

		assertTrue("No category " + c1 + " in product " + p, categorySet.contains(c1));
		assertTrue("No category " + c2 + " in product " + p, categorySet.contains(c2));

		assertTrue("No product " + p + " in category " + c1, c1.getProducts().contains(p));
		assertTrue("No product " + p + " in category " + c2, c2.getProducts().contains(p));
	}

	@Test
	public void testAssignCategoriesToProducts() {
		String c1Name = "new category 1";
		String c2Name = "new category 2";

		Category c1 = new Category(c1Name);
		Category c2 = new Category(c2Name);

		testCategoryDao.makePersistent(c1);
		testCategoryDao.makePersistent(c2);

		Product p = new Product("new product");
		testProductDao.makePersistent(p);

		Selectable<Category> c1Select = new SelectableImpl<Category>(c1, true);
		Selectable<Category> c2Select = new SelectableImpl<Category>(c2, false);

		ArrayList<Selectable<Category>> selects = new ArrayList<Selectable<Category>>();
		selects.add(c1Select);
		selects.add(c2Select);

		testProductDao.assignCategoriesToProduct(p, selects);

		p = testProductDao.findById(p.getId());

		Set<Category> categories = p.getCategories();

		assertEquals("Wrong number of categories for product " + p, 1, categories.size());

		assertTrue("Wrong category in the set: " + c1, categories.contains(c1));
		assertFalse("Wrong category in the set: " + c2, categories.contains(c2));


		assertEquals("Wrong number of products for category " + c1, 1, c1.getProducts().size());
		assertEquals("Wrong number of products for category " + c2, 0, c2.getProducts().size());

		assertTrue("No product for category", c1.getProducts().contains(p));
		assertFalse("Extra product for category", c2.getProducts().contains(p));

		//now flip the categories
		
		c1Select.deselect();
		c2Select.select();

		selects.clear();

		selects.add(c1Select);
		selects.add(c2Select);

		testProductDao.assignCategoriesToProduct(p, selects);

		p = testProductDao.findById(p.getId());

		Set<Category> categoriesAfterTheFlip = p.getCategories();

		assertEquals("Wrong number of categories for product " + p, 1, categoriesAfterTheFlip.size());

		assertFalse("Wrong category in the set: " + c1, categoriesAfterTheFlip.contains(c1));
		assertTrue("Wrong category in the set: " + c2, categoriesAfterTheFlip.contains(c2));

		assertEquals("Wrong number of products for category " + c1, 0, c1.getProducts().size());
		assertEquals("Wrong number of products for category " + c2, 1, c2.getProducts().size());

		assertFalse("Extra product for category", c1.getProducts().contains(p));
		assertTrue("No product for category", c2.getProducts().contains(p));
	}

	@Test
	public void testDeleteById() throws IOException {
		Product p = beansFromDb.get(0);
		Set<Category> categories = p.getCategories();
		Set<Image> images = p.getImages();

		HashSet<FileBackupRestoreManager> restoreManagers = new HashSet<FileBackupRestoreManager>();
		String pathPrefix = testImageDao.getPathPrefix(); 
		for (Image i : images) {
			FileBackupRestoreManager restoreManager = new FileBackupRestoreManager(pathPrefix + i.getUrl());
			restoreManagers.add(restoreManager);
		}

		assertEquals("Wrong number of images in product " + p, 2, images.size());
		assertEquals("Wrong number of categories in product " + p, 1, categories.size());

		for (FileBackupRestoreManager manager : restoreManagers) {
			manager.backup();
		}

		try {
			testProductDao.deleteById(p.getId());
			testProductDao.flush();

			Product deletedProduct = testProductDao.findById(p.getId());
			assertNull("Product wasn't deleted properly " + p, deletedProduct);

			for (Category c : categories) {
				assertFalse("Product " + p + " wasn't deleted from category " + c, c.getProducts().contains(p));
			}

			for (Image i : images) {
				Image loadedImage = testImageDao.findById(i.getId());
				assertNull("Image wasn't removed from DB: " + i, loadedImage);
				File imageFile = new File(pathPrefix + i.getUrl());
				assertFalse("Image wasn't removed from disk: " + i, imageFile.exists());
			}
		} finally {
			for (FileBackupRestoreManager manager : restoreManagers) {
				manager.restore();
			}
		}

	}
}
