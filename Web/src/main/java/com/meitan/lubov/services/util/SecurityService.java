package com.meitan.lubov.services.util;

import com.meitan.lubov.model.persistent.Client;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
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

	public String doLogin() throws IOException, ServletException {
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

		RequestDispatcher dispatcher = ((ServletRequest) context.getRequest())
				.getRequestDispatcher("/j_spring_security_check");

		dispatcher.forward((ServletRequest) context.getRequest(),
				(ServletResponse) context.getResponse());

		FacesContext.getCurrentInstance().responseComplete();
		// It's OK to return null here because Faces is just going to exit.
		return null;
	}

}
