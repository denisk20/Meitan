package com.meitan.lubov.services.util;

import com.meitan.lubov.model.components.Name;
import com.meitan.lubov.model.persistent.Authority;
import com.meitan.lubov.model.persistent.Client;
import com.meitan.lubov.services.dao.AuthorityDao;
import com.meitan.lubov.services.dao.ClientDao;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.execution.RequestContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author denis_k
 *         Date: 10.07.2010
 *         Time: 15:37:25
 */
//todo unit test
@Service("securityService")
public class SecurityService implements ApplicationContextAware{
	public ApplicationContext applicationContext;
	public static final String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";
	public static final String ROLE_CLIENT = "ROLE_CLIENT";
	public static final String ROLE_CONSULTANT = "ROLE_CONSULTANT";
	public static final String ROLE_ADMIN = "ROLE_ADMIN";

	//todo unit test
	public void authenticateUser(Client user) {
		Set<Authority> roles = user.getRoles();

		final int size = roles.size();
		GrantedAuthority[] authorities =
				new GrantedAuthority[size];
		Iterator<Authority> it = roles.iterator();
		for (int i = 0; i < size; i++) {
			Authority a = it.next();
			authorities[i] = new GrantedAuthorityImpl(a.getRole());
		}

		authenticateUser(user, authorities);
	}

	private void authenticateUser(Client user, GrantedAuthority[] authorities) {
		UsernamePasswordAuthenticationToken token =
				new UsernamePasswordAuthenticationToken(
						user.getLogin(), user.getPassword(), Arrays.asList(authorities));
		SecurityContextHolder.getContext().setAuthentication(token);

		if (this.applicationContext != null) {
            applicationContext.publishEvent(new InteractiveAuthenticationSuccessEvent(token, this.getClass()));
        }
	}

	//todo unit test
	public void addCurrentSessionAuthority(Client user, String newRole) {
		Set<Authority> roles = user.getRoles();
		int newRolesCount = roles.size() + 1;
		GrantedAuthority[] authorities = new GrantedAuthority[newRolesCount];
		int i=0;
		Iterator<Authority> it = roles.iterator();
		while (it.hasNext()) {
			authorities[i] = new GrantedAuthorityImpl(it.next().getRole());
			i++;
		}
		authorities[i] = new GrantedAuthorityImpl(newRole);

		authenticateUser(user, authorities);
	}

	//todo unit test
	public String isUserAuthenticated() {
		String result = "no";

		HashSet<String> acceptableRoles = new HashSet<String>();
		acceptableRoles.add(ROLE_CLIENT);
		acceptableRoles.add(ROLE_CONSULTANT);
		acceptableRoles.add(ROLE_ADMIN);
		acceptableRoles.add(ROLE_ANONYMOUS);

		HashSet<String> actualRoles = new HashSet<String>();
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			Collection<GrantedAuthority> authorities = authentication.getAuthorities();
			if (authorities != null) {
				for (GrantedAuthority auth : authorities) {
					actualRoles.add(auth.getAuthority());
				}
				if (acceptableRoles.removeAll(actualRoles)) {
					result = "yes";
				}
			}
		}

		return result;
	}

	//todo unit test
	public void tryToLogoutAnonymous(String login) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			String currentUser = (String) authentication.getPrincipal();
			if (login.equals(currentUser)) {
				final Collection<GrantedAuthority> grantedAuthorityCollection = authentication.getAuthorities();
				if (grantedAuthorityCollection != null) {
					final GrantedAuthority authority = grantedAuthorityCollection.iterator().next();
					final String role = authority.getAuthority();
					if (grantedAuthorityCollection.size() == 1 && role.equals(ROLE_ANONYMOUS)) {
						SecurityContextHolder.getContext().setAuthentication(null);
					}
				}
			}
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
