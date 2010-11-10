package com.meitan.lubov.services.validator;

import com.meitan.lubov.model.persistent.Client;
import com.meitan.lubov.services.dao.ClientDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.stereotype.Component;
import org.springframework.webflow.execution.RequestContext;

import java.util.List;

/**
 * @author denis_k
 *         Date: 09.07.2010
 *         Time: 22:01:48
 */
@Component
public class ClientValidator {
	@Autowired
	private ClientDao clientDao;

	public void validateUserProfile(Client c, ValidationContext context) throws Exception {
		String original = c.getPassword();
		String conformed = c.getConformedPassword();
		if (original == null || conformed == null) {
			throw new Exception("Illegal arguments: original = " + original + ", conformed= " + conformed);
		}

		boolean valid = true;
		if (original.equals("") || conformed.equals("")) {
			context.getMessageContext()
					.addMessage(new MessageBuilder()
							.error()
							.defaultText("Password can't be null")
							.build());
			valid=false;
		}

		if (!original.equals(conformed)) {
			context.getMessageContext()
					.addMessage(new MessageBuilder()
							.error()
							.defaultText("Passwords don't match")
							.build());
			valid=false;
		}

		String email = c.getEmail();
		if (email == null || email.equals("")) {
			context.getMessageContext()
					.addMessage(new MessageBuilder()
							.error()
							.defaultText("Email can't be null")
							.build());
			valid=false;
		}

		if (!valid) {
			throw new IllegalArgumentException("Passwords don't match");
		}
	}

}
