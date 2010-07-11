package com.meitan.lubov.services.dao.jpa.impl;

import com.meitan.lubov.model.persistent.Client;
import com.meitan.lubov.services.dao.ClientDao;
import com.meitan.lubov.services.dao.jpa.JpaDao;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Date: Mar 5, 2010
 * Time: 10:16:28 PM
 *
 * @author denisk
 */
@Repository
@Service("clientDao")
public class JpaClientDao extends JpaDao<Client, Long> implements ClientDao {

	@SuppressWarnings("unchecked")
	@Override
	public Client getByLogin(String login) {
		List<Client> result = em.createNamedQuery("getClientByLogin")
				.setParameter("login", login)
				.getResultList();
		if (result.size() != 1) {
			throw new IllegalStateException("Multiple users with login " + login);
		}
		return result.get(0);
	}
}
