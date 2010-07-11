package com.meitan.lubov.services.util;

import com.meitan.lubov.model.persistent.Client;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.event.authentication.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.execution.RequestContext;

import java.io.IOException;

/**
 * @author denis_k
 *         Date: 10.07.2010
 *         Time: 15:37:25
 */
@Service("securityService")
public class SecurityService implements ApplicationContextAware{
	private ApplicationContext applicationContext;
	public void authenticateUser(Client user) {
		GrantedAuthority[] authorities = 
				new GrantedAuthority[] {new GrantedAuthorityImpl("ROLE_CLIENT")};

		UsernamePasswordAuthenticationToken token =
				new UsernamePasswordAuthenticationToken(
						user.getLogin(), user.getPassword(), authorities);
		SecurityContextHolder.getContext().setAuthentication(token);

		if (this.applicationContext != null) {
            applicationContext.publishEvent(new InteractiveAuthenticationSuccessEvent(token, this.getClass()));
        }
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
