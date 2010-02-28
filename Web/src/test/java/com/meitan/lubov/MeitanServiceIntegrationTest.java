package com.meitan.lubov;

import com.meitan.lubov.model.persistent.Product;
import com.meitan.lubov.services.MeitanService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Date: Feb 7, 2010
 * Time: 12:28:29 PM
 *
 * @author denisk
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testEnvironment.xml", "classpath:startData.xml"})
public class MeitanServiceIntegrationTest {

	@Autowired
	MeitanService meitanService;
	EntityManager em;

	@Autowired
	@Qualifier("megaCream")
	private Product megaCream;

	@Autowired
	@Qualifier("gigaCream")
	private Product gigaCream;

	@PersistenceContext(unitName = "testMeitanDatabase")
	public void setEm(EntityManager em) {
		this.em = em;
	}

	@Before
	public void setupData() {

		//todo DAOs!!!
		meitanService.persistProduct(megaCream);
		//meitanService.persistProduct(gigaCream);
	}

	@Test
	public void testSomething() {
		System.out.println("Mega cream name:" + megaCream.getName() +
				"");
	}
}
