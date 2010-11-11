package com.meitan.lubov.services.validators;

import com.meitan.lubov.model.persistent.Client;
import com.meitan.lubov.services.dao.ClientDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author denis_k
 *         Date: 09.07.2010
 *         Time: 22:01:48
 */
@Service("clientValidator")
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
			valid = false;
		}

		if (!original.equals(conformed)) {
			context.getMessageContext()
					.addMessage(new MessageBuilder()
							.error()
							.defaultText("Passwords don't match")
							.build());
			valid = false;
		}

		String email = c.getEmail();
		if (email == null || email.equals("")) {
			context.getMessageContext()
					.addMessage(new MessageBuilder()
							.error()
							.defaultText("Email can't be null")
							.build());
			valid = false;
		}

		Client existingClient = null;
		if (c.getId() != 0) {
			existingClient = clientDao.findById(c.getId());
		}
		String login = c.getLogin();
		if (existingClient != null) {
			//check if they changed login
			if (!login.equals(existingClient.getLogin())) {
				if (!isLoginUnique(login, context)) {
					valid = false;
				}
			}
			if (email != null) {
				if (!email.equals(existingClient.getEmail())) {
					if (!isEmailUnique(email, context)) {
						valid = false;
					}
				}
			}
		} else {
			//new client
			if (!isLoginUnique(login, context)) {
				valid = false;
			}
			if (!isEmailUnique(email, context)) {
				valid = false;
			}
		}
		if (!valid) {
			throw new IllegalArgumentException("Passwords don't match");
		}
	}

	private boolean isLoginUnique(String login, ValidationContext context) {
		boolean valid = true;
		Client clientWithSameLogin = clientDao.getByLogin(login);
		if (clientWithSameLogin != null) {
			context.getMessageContext()
					.addMessage(new MessageBuilder()
							.error()
							.defaultText("Client already exists with login " + login)
							.build());
			valid = false;
		}
		return valid;
	}

	private boolean isEmailUnique(String email, ValidationContext context) {
		boolean valid = true;
		List<Client> clientsWithSameEmailsList = clientDao.findByEmail(email);
		if (clientsWithSameEmailsList.size() > 1) {
			throw new IllegalStateException("Multiple clients in DB with email " + email);
		} else if (clientsWithSameEmailsList.size() != 0) {
			context.getMessageContext()
					.addMessage(new MessageBuilder()
							.error()
							.defaultText("Client already exists with email " + email)
							.build());
			valid = false;
		}

		return valid;
	}

}
