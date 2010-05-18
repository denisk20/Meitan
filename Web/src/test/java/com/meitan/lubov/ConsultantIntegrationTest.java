package com.meitan.lubov;

import com.meitan.lubov.model.components.Name;
import com.meitan.lubov.model.components.Passport;
import com.meitan.lubov.model.persistent.Consultant;
import com.meitan.lubov.services.dao.ConsultantDao;
import com.meitan.lubov.services.dao.Dao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.PersistenceException;
import java.util.Date;

/**
 * @author denis_k
 *         Date: 14.03.2010
 *         Time: 10:07:54
 */
public class ConsultantIntegrationTest extends GenericIntegrationTest<Consultant> {
    @Autowired
    private ConsultantDao consultantDao;

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
}
