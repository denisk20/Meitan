package com.meitan.lubov.services;

import com.meitan.lubov.model.persistent.Category;
import com.meitan.lubov.model.persistent.Image;
import com.meitan.lubov.model.persistent.Product;
import com.meitan.lubov.services.dao.CategoryDao;
import com.meitan.lubov.services.dao.ProductDao;
import com.meitan.lubov.services.util.Utils;
import com.meitan.lubov.services.util.sync.MeitanSyncronizer;
import com.meitan.lubov.services.util.sync.MeitanSyncronizerImpl;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.List;
import javax.xml.xpath.XPathExpressionException;
import static org.junit.Assert.*;

/**
 * Date: Jan 8, 2011
 * Time: 6:07:59 PM
 *
 * @author denisk
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testsSetup.xml"})
//@Ignore
public class MeitanSyncronizerImplTest extends AbstractTransactionalJUnit4SpringContextTests {
	@Autowired
	private CategoryDao testCategoryDao;
	@Autowired
	private ProductDao testProductDao;
	@Autowired
	private Utils utils;
	private static final String TESTS_ROOT = "/Web/src/test/resources/meitanPages/";

	@Test
	public void getCategories() throws IOException, XPathExpressionException {
		HashSet<File> filesToDelete = new HashSet<File>();
		try {
			final List<Category> initialCategories = testCategoryDao.findAll();
			int initialCategoriesSize = initialCategories.size();
			final List<Product> initialProducts = testProductDao.findAll();
			int initialProductsSize = initialProducts.size();
			MeitanSyncronizer syncronizer = (MeitanSyncronizer) applicationContext.getBean("testMeitanSyncronizerImpl");
			final String testsPath = "file://" + utils.getHomePath() + TESTS_ROOT;
			syncronizer.setUrl(testsPath + "meitan.ru.catalog.html");
			syncronizer.setPrefixUrl("");
			syncronizer.setProductsUrl("");
			syncronizer.setFilePrefix(testsPath);
			syncronizer.sync();

			final List<Category> finalCategories = testCategoryDao.findAll();
			final List<Product> finalProducts = testProductDao.findAll();
			assertTrue(finalCategories.size() == initialCategoriesSize + 1);
			assertTrue(finalProducts.size() == initialProductsSize + 2);

			finalCategories.removeAll(initialCategories);
			finalProducts.removeAll(initialProducts);

			Category newCategory = finalCategories.get(0);
			assertNotNull(newCategory.getName());
			assertNotNull(newCategory.getDescription());
			assertFalse(newCategory.getName().isEmpty());
			assertFalse(newCategory.getDescription().isEmpty());

			for (Product p : finalProducts) {
				assertTrue(newCategory.getProducts().contains(p));
				for (Image i : p.getImages()) {
					final File productImageFile = new File(utils.getImageUploadDirectoryPath() + i.getUrl());
					assertTrue(productImageFile.exists());
					filesToDelete.add(productImageFile);
				}
				assertNotNull(p.getName());
				assertNotNull(p.getDescription());
				assertFalse(p.getName().isEmpty());
				assertFalse(p.getDescription().isEmpty());
				assertNotNull(p.getAvatar());
			}

			final Image categoryImage = newCategory.getImage();
			assertNotNull(categoryImage);
			final File categoryImageFile = new File(utils.getImageUploadDirectoryPath() + categoryImage.getUrl());
			filesToDelete.add(categoryImageFile);
			assertTrue(categoryImageFile.exists());
		} finally {
			for (File f : filesToDelete) {
				assertTrue(f.delete());
			}
		}
	}

}
