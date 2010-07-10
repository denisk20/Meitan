package com.meitan.lubov;

import com.meitan.lubov.model.persistent.Authority;
import com.meitan.lubov.services.dao.AuthorityDao;
import com.meitan.lubov.services.dao.Dao;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.fail;

/**
 * Date: Jul 9, 2010
 * Time: 10:58:37 AM
 *
 * @author denisk
 */
public class AuthorityIntegrationTest extends GenericIntegrationTest <Authority>{
	@Autowired
	private AuthorityDao authorityDao;

	@Override
		protected void setUpBeanNames() {
		beanNames.add("ent_creamLoverAuthority");
		beanNames.add("ent_creamAdmierAuthority1");
		beanNames.add("ent_creamAdmierAuthority2");
	}

	@Override
	protected Dao<Authority, Long> getDAO() {
		return authorityDao;
	}

	@Test
	@Ignore("todo")
	//todo
	public void testCreateAuthority() {

	}
}
