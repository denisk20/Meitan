package com.meitan.lubov;

import com.meitan.lubov.services.dao.Dao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Date: Mar 5, 2010
 * Time: 11:19:21 PM
 *
 * @author denisk
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testsSetup.xml"})
public abstract class GenericIntegrationTest<T> extends AbstractTransactionalJUnit4SpringContextTests {
	protected static final String TEST_UPLOAD_DIRECTORY = "testUpload"; 
	protected static final String PATH_DELIM = "/"; 
	protected List<String> beanNames = new ArrayList<String>();
	protected List<T> beansFromXml = new ArrayList<T>();
	protected List<T> beansFromDb = new ArrayList<T>();

	@Before
	public void beforeTest() {
		setUpBeanNames();
		getComparedBeans();
	}

	protected abstract void setUpBeanNames();

	protected abstract Dao<T, Long> getDAO();

	@SuppressWarnings("unchecked")
	private void getComparedBeans() {
		String[] excludedProperties = getExcludedProperties();
		Dao<T, Long> dao = getDAO();
		for (String beanName : beanNames) {
			T beanFromSpring = (T) applicationContext.getBean(beanName);
			List<T> resultDBList = dao.findByExample(beanFromSpring, excludedProperties);
			int itemsFound = resultDBList.size();
			if (itemsFound != 1) {
				fail("Found " + itemsFound + " items using " + beanFromSpring + " as example");
			}
			T beanFromDB = resultDBList.get(0);
			beansFromXml.add(beanFromSpring);
			beansFromDb.add(beanFromDB);
		}
	}

	@Test
    public void compareSpringDBBeans() {
        final int beansFromDBCount = beansFromDb.size();
        final int beanFromSpringCount = beansFromXml.size();
        if (beansFromDBCount != beanFromSpringCount) {
            fail("There are " + beanFromSpringCount + " bean(s) from Spring container " +
                    "but " + beansFromDBCount + " bean(s) from DB");
        }
        for (int i = 0; i < beansFromXml.size(); i++) {
            T beanFromSpring = beansFromXml.get(i);
            T beanFromDB = beansFromDb.get(i);
            compareEquality(beanFromSpring, beanFromDB);
            compareAdditionalProperties(beanFromSpring, beanFromDB);
        }
    }

	protected String[] getExcludedProperties() {
		return new String[0];
	}

    private void compareEquality(T beanFromSpring, T beanFromDB) {
        assertThat(beanFromSpring, is(beanFromDB));
    }

    protected void compareAdditionalProperties(T beanFromSpring, T beanFromDB){}

}