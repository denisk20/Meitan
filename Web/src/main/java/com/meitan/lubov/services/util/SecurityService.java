package com.meitan.lubov.services.util;

import com.meitan.lubov.model.persistent.Client;
import com.meitan.lubov.services.dao.ClientDao;
import com.meitan.lubov.services.dao.jpa.impl.JpaClientDao;
import org.springframework.transaction.annotation.Transactional;

/**
 * Date: Sep 28, 2010
 * Time: 3:34:18 PM
 *
 * @author denisk
 */
public interface SecurityService {
	String ROLE_UNREGISTERED = "ROLE_UNREGISTERED";
	String ROLE_CLIENT = "ROLE_CLIENT";
	String ROLE_CONSULTANT = "ROLE_CONSULTANT";
	String ROLE_ADMIN = "ROLE_ADMIN";
	String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";

	@Transactional(readOnly = true)
	void authenticateUser(Client user);

	@Transactional(readOnly = true)
	void addCurrentSessionAuthority(Client user, String newRole);

	String isUserAuthenticated();

	void tryToLogoutAnonymous(String login);

	ClientDao getClientDao();

	void setClientDao(ClientDao clientDao);
}
