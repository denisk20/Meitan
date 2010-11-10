package com.meitan.lubov.services.dao;

import com.meitan.lubov.model.persistent.Authority;
import com.meitan.lubov.model.persistent.Client;
import org.springframework.transaction.annotation.Transactional;

/**
 * Date: Jul 9, 2010
 * Time: 10:51:40 AM
 *
 * @author denisk
 */
public interface AuthorityDao extends Dao<Authority, Long>{
	@Transactional
	void assignAuthority(Client client, String role);

	boolean clientHasRole(Client client, String role);
}
