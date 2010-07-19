package com.meitan.lubov;

import com.meitan.lubov.model.components.Name;
import com.meitan.lubov.model.components.Passport;
import com.meitan.lubov.model.persistent.Client;
import com.meitan.lubov.model.persistent.Consultant;
import com.meitan.lubov.services.dao.AuthorityDao;
import com.meitan.lubov.services.dao.ClientDao;
import com.meitan.lubov.services.dao.ConsultantDao;
import com.meitan.lubov.services.dao.Dao;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.PersistenceException;
import java.util.Date;

/**
 * @author denis_k
 *         Date: 14.03.2010
 *         Time: 10:07:54
 */
@Ignore
public class ConsultantIntegrationTest extends GenericIntegrationTest<Consultant> {
	//todo make test dao
    @Autowired
    private ConsultantDao consultantDao;

	@Autowired
	private ClientDao testClientDao;

	@Autowired
	private AuthorityDao testAuthorityDao;

    @Override
    protected void setUpBeanNames() {
        beanNames.add("ent_creamAdmier");
    }

    @Override
    protected Dao<Consultant, Long> getDAO() {
        return consultantDao;
    }

	@Test(expected = PersistenceException.class)
	public void testNonUniquePassport() {
		Consultant c = beansFromDb.get(0);
		Passport p = c.getPassport();

		Consultant created = new Consultant(new Name("first", "second", "third"), "a@b.com", p, new Date());
		consultantDao.makePersistent(created);

		consultantDao.flush();
	}

	@Test(expected = PersistenceException.class)
	public void insertNullablePassport() {
		Consultant c = beansFromDb.get(0);
		Passport p = new Passport();

		Consultant created = new Consultant(new Name("first", "second", "third"), "a@b.com", p, new Date());
		consultantDao.makePersistent(created);

		consultantDao.flush();
	}

	@Test(expected = PersistenceException.class)
	public void insertNullablePassportSeries() {
		Consultant c = beansFromDb.get(0);
		Passport p = null;

		Consultant created = new Consultant(new Name("first", "second", "third"), "a@b.com", p, new Date());
		consultantDao.makePersistent(created);

		consultantDao.flush();
	}

	@Test(expected = PersistenceException.class)
	public void insertNullableDate() {
		Consultant c = beansFromDb.get(0);
		c.setJoinDate(null);

		consultantDao.makePersistent(c);
		consultantDao.flush();
	}

//	@Ignore
	@Test
	public void testPrototype() {
		int consultantsCount = consultantDao.findAll().size();
		int clientCount = testClientDao.findAll().size();

		Client c = new Client(new Name("first", "second", "third"), "a@b.com");
		c.setLogin("login");
		c.setPassword("pass");

		c.setJoinDate(new Date());
		testAuthorityDao.assignAuthority(c, "ROLE_CLIENT");
		testClientDao.makePersistent(c);

		Consultant consultant = (Consultant) c;
		consultantDao.makePersistent(consultant);
		consultantDao.flush();

		int newConsultantsCount = consultantDao.findAll().size();
		int newClientCount = testClientDao.findAll().size();

		assertEquals(clientCount + 1, newClientCount);
		assertEquals(consultantsCount + 1, newConsultantsCount);

	}
}
