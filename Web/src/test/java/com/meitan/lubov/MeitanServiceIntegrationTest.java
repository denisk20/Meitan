package com.meitan.lubov;

import com.meitan.lubov.model.persistent.BuyingAct;
import com.meitan.lubov.model.persistent.Client;
import com.meitan.lubov.model.persistent.Consultant;
import com.meitan.lubov.model.persistent.Product;
import com.meitan.lubov.model.persistent.ProductCategory;
import com.meitan.lubov.services.dao.BuyingActDao;
import com.meitan.lubov.services.dao.CategoryDao;
import com.meitan.lubov.services.dao.ClientDao;
import com.meitan.lubov.services.dao.ConsultantDao;
import com.meitan.lubov.services.dao.ProductDao;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
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
public class MeitanServiceIntegrationTest extends AbstractTransactionalJUnit4SpringContextTests {

	EntityManager em;

	@Autowired
	ProductDao productDao;
	@Autowired
	BuyingActDao buyingActDao;
	@Autowired
	CategoryDao categoryDao;
	@Autowired
	ClientDao clientDao;
	@Autowired
	ConsultantDao consultantDao;

	@Autowired
	@Qualifier("megaCream")
	private Product megaCream;

	@Autowired
	@Qualifier("gigaCream")
	private Product gigaCream;

	@Autowired
	@Qualifier("purchase1")
	private BuyingAct purchase1;

	@Autowired
	@Qualifier("purchase2")
	private BuyingAct purchase2;

	@Autowired
	@Qualifier("purchase3")
	private BuyingAct purchase3;
	@Autowired
	@Qualifier("creamLover")
	private Client creamLoverClient;
	@Autowired
	@Qualifier("creamAdmier")
	private Consultant creamAdmierConsultant;
	@Autowired
	@Qualifier("creams")
	private ProductCategory creams;

	@PersistenceContext(unitName = "testMeitanDatabase")
	public void setEm(EntityManager em) {
		this.em = em;
	}

	@Before
	public void setupData() {
		productDao.makePersistent(megaCream);
		productDao.makePersistent(gigaCream);
		buyingActDao.makePersistent(purchase1);
		buyingActDao.makePersistent(purchase2);
		buyingActDao.makePersistent(purchase3);
		categoryDao.makePersistent(creams);
		clientDao.makePersistent(creamLoverClient);
		consultantDao.makePersistent(creamAdmierConsultant);
	}

	@Test
	@Ignore
	public void testSomething() {
		System.out.println("Mega cream name:" + megaCream.getName() + "");
	}
}
