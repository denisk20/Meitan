package com.meitan.lubov;

import com.meitan.lubov.model.persistent.Authority;
import com.meitan.lubov.model.persistent.Client;
import com.meitan.lubov.services.dao.AuthorityDao;
import com.meitan.lubov.services.dao.ClientDao;
import com.meitan.lubov.services.dao.Dao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * Date: Jul 9, 2010
 * Time: 10:58:37 AM
 *
 * @author denisk
 */
public class AuthorityIntegrationTest extends GenericIntegrationTest <Authority>{
	@Autowired
	private AuthorityDao testAuthorityDao;

	@Autowired
	private ClientDao testClientDao;

	@Override
		protected void setUpBeanNames() {
		beanNames.add("ent_creamLoverAuthority");
		beanNames.add("ent_creamAdmierAuthority1");
		beanNames.add("ent_creamAdmierAuthority2");
	}

	@Override
	protected Dao<Authority, Long> getDAO() {
		return testAuthorityDao;
	}

	@Test
	public void testCreateAuthority() {
		String role = "ROLE_MEGAMAN";
		Client c = testClientDao.findAll().get(0);

		Authority a = new Authority(c, role);

		testAuthorityDao.makePersistent(a);

		assertNotNull(a.getId());
		Authority loaded = testAuthorityDao.findById(a.getId());
		assertNotNull(loaded);
	}

	@Test
	public void testDeleteAuthority() {
		Authority a = testAuthorityDao.findAll().get(0);
		testAuthorityDao.makeTransient(a);
		testAuthorityDao.flush();

		a = testAuthorityDao.findById(a.getId());
		assertNull(a);
	}
}
