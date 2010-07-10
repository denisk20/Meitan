package com.meitan.lubov.services.util;

import com.meitan.lubov.model.persistent.Client;
import org.springframework.security.context.SecurityContextHolder;
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
public class SecurityService {
	public void authenticateUser(Client user) {
		UsernamePasswordAuthenticationToken token =
				new UsernamePasswordAuthenticationToken(user.getLogin(), user.getPassword());
		SecurityContextHolder.getContext().setAuthentication(token);
	}

	public void doLogin(RequestContext requestContext) {
		ExternalContext context = requestContext.getExternalContext();

		
		return ;
	}

}
