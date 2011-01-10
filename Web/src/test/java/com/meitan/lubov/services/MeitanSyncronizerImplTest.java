package com.meitan.lubov.services;

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

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
		int initialCategoriesSize = testCategoryDao.findAll().size();
		int initialProductsSize = testProductDao.findAll().size();
		MeitanSyncronizer syncronizer = (MeitanSyncronizer) applicationContext.getBean("testMeitanSyncronizerImpl");
		final String testsPath = "file://" + utils.getHomePath() + TESTS_ROOT;
		syncronizer.setUrl(testsPath + "meitan.ru.catalog.html");
		syncronizer.setPrefixUrl("");
		syncronizer.setProductsUrl("");
		syncronizer.setFilePrefix(testsPath);
		syncronizer.sync();

		assertTrue(testCategoryDao.findAll().size() == initialCategoriesSize + 1);
		assertTrue(testProductDao.findAll().size() == initialProductsSize + 2);
	}

}
