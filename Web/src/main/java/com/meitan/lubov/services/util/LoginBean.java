package com.meitan.lubov.services.util;

import org.springframework.stereotype.Component;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * Date: Nov 25, 2010
 * Time: 4:44:00 PM
 *
 * @author denisk
 */
@Component
public class LoginBean {
	//managed properties for the login page, username/password/etc...

	// This is the action method called when the user clicks the "login" button

	public String doLogin() throws IOException, ServletException {
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

//		RequestDispatcher dispatcher = ((ServletRequest) context.getRequest())
//				.getRequestDispatcher("spring/j_spring_security_check");
//
//		dispatcher.forward((ServletRequest) context.getRequest(),
//				(ServletResponse) context.getResponse());

		context.dispatch("spring/loginProcess");

		FacesContext.getCurrentInstance().responseComplete();
		// It's OK to return null here because Faces is just going to exit.
		return null;
	}

}
