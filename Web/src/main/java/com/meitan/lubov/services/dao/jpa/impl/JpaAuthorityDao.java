package com.meitan.lubov.services.dao.jpa.impl;

import com.meitan.lubov.model.persistent.Authority;
import com.meitan.lubov.model.persistent.Client;
import com.meitan.lubov.services.dao.AuthorityDao;
import com.meitan.lubov.services.dao.jpa.JpaDao;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Date: Jul 9, 2010
 * Time: 10:52:11 AM
 *
 * @author denisk
 */
@Repository
@Service("authorityDao")
public class JpaAuthorityDao extends JpaDao<Authority, Long> implements AuthorityDao {
	@Override
	public List<Authority> findByExample(Authority exampleInstance, String... excludeProperty) {
		Criteria criteria = ((Session) em.getDelegate()).createCriteria(Authority.class)
				.add(Restrictions.eq("role", exampleInstance.getRole()))
				.createCriteria("client")
				.add(Restrictions.eq("name", exampleInstance.getClient().getName()))
				.add(Restrictions.eq("email", exampleInstance.getClient().getEmail()));

		final List list = criteria.list();
		return getDistinct(list);
	}

	@Override
	@Transactional
	public void assignAuthority(Client client, String role) {
		if (clientHasRole(client, role)) {
			return;
		}
		Authority auth = new Authority(client, role);
		client.getRoles().add(auth);
		makePersistent(auth);
	}

	@Override
	public boolean clientHasRole(Client client, String role) {
		boolean result = false;
		for (Authority a : client.getRoles()) {
			if (a.getRole().equals(role)) {
				result = true;
				break;
			}
		}

		return result;
	}


}
