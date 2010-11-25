package com.meitan.lubov.services.util;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AbstractProcessingFilter;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

/**
 * Date: Nov 25, 2010
 * Time: 4:45:23 PM
 *
 * @author denisk
 */
public class LoginErrorPhaseListener implements PhaseListener {

	private static final long serialVersionUID = -1216620620302322995L;

	@Override
	public void beforePhase(final PhaseEvent phaseEvent) {
		Exception e = (Exception) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(
				WebAttributes.AUTHENTICATION_EXCEPTION);

		if (e instanceof BadCredentialsException) {
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(
					WebAttributes.AUTHENTICATION_EXCEPTION, null);
			phaseEvent.getFacesContext().addMessage("Username or password not valid.", new FacesMessage("Faces message: Username or password not valid."));
		}
	}

	@Override
	public void afterPhase(final PhaseEvent arg0) {
	}

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.RENDER_RESPONSE;
	}

}
