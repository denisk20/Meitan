package com.meitan.lubov.services.util;

import com.meitan.lubov.model.persistent.Authority;
import com.meitan.lubov.model.persistent.Client;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.execution.RequestContext;

import java.io.IOException;
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
	private ApplicationContext applicationContext;
	public void authenticateUser(Client user) {
		GrantedAuthority[] authorities =
				new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_CLIENT")};

		authenticateUser(user, authorities);
	}

	private void authenticateUser(Client user, GrantedAuthority[] authorities) {
		UsernamePasswordAuthenticationToken token =
				new UsernamePasswordAuthenticationToken(
						user.getLogin(), user.getPassword(), authorities);
		SecurityContextHolder.getContext().setAuthentication(token);

		if (this.applicationContext != null) {
            applicationContext.publishEvent(new InteractiveAuthenticationSuccessEvent(token, this.getClass()));
        }
	}

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
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
