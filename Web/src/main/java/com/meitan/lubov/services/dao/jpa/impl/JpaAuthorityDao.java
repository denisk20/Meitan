package com.meitan.lubov.services.dao.jpa.impl;

import com.meitan.lubov.model.persistent.Authority;
import com.meitan.lubov.services.dao.AuthorityDao;
import com.meitan.lubov.services.dao.jpa.JpaDao;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

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

		return criteria.list();
	}
}
