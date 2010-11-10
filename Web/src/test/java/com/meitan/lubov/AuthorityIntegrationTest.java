package com.meitan.lubov;

import com.meitan.lubov.model.persistent.Authority;
import com.meitan.lubov.model.persistent.Client;
import com.meitan.lubov.services.dao.AuthorityDao;
import com.meitan.lubov.services.dao.ClientDao;
import com.meitan.lubov.services.dao.Dao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

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

	@Test
	public void testAddAuthority() {
		final String role = "ROLE_BATMAN";
		Client c = testClientDao.findAll().get(0);
		testAuthorityDao.assignAuthority(c, role);

		testAuthorityDao.flush();

		Authority exampleAuthority = new Authority(c, role);
		assertTrue(c.getRoles().contains(exampleAuthority));
		List<Authority> authorityList = testAuthorityDao.findByExample(exampleAuthority);
		assertEquals(1, authorityList.size());

		Authority loaded = authorityList.get(0);
		assertEquals(c, loaded.getClient());
		assertEquals(role, loaded.getRole());
	}

	@Test
	public void testAddExistingAuthority() {
		Client c = testClientDao.findAll().get(0);

		int initialAuthoritiesCount = testAuthorityDao.findAll().size();
		Set<Authority> authoritySet = c.getRoles();
		//we assume this
		assertTrue(authoritySet.size() > 0);
		Authority auth = authoritySet.iterator().next();

		String existingRole = auth.getRole();

		testAuthorityDao.assignAuthority(c, existingRole);
//		testAuthorityDao.flush();

		Client loaded = testClientDao.findById(c.getId());

		assertEquals(authoritySet, loaded.getRoles());

		assertEquals(initialAuthoritiesCount, testAuthorityDao.findAll().size());
	}

	@Test
	public void testClientHasRole() {
		Client c = testClientDao.findAll().get(0);
		String role = c.getRoles().iterator().next().getRole();

		assertTrue(testAuthorityDao.clientHasRole(c, role));
		assertFalse(testAuthorityDao.clientHasRole(c, role+"_does_not_exist"));
	}
}
