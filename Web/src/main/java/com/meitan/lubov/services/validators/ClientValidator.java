package com.meitan.lubov.services.validators;

import com.meitan.lubov.model.persistent.Client;
import com.meitan.lubov.services.dao.ClientDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		boolean valid = true;
		if (original == null || conformed == null) {
			context.getMessageContext()
					.addMessage(new MessageBuilder()
							.error()
							.defaultText("Password can't be null")
							.build());
			valid = false;
		}

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
							.source("messages")
							.code("passwordsDontMatch")
							.defaultText("Passwords don't match")
							.build());
			valid = false;
		}

		String email = c.getEmail();
		if (email == null || email.equals("")) {
			context.getMessageContext()
					.addMessage(new MessageBuilder()
							.error()
							.source("messages")
							.code("emailCantBeNull")
							.defaultText("Email can't be null")
							.build());
			valid = false;
		}

		Pattern emailPattern = Pattern.compile("[a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}");
		Matcher emailMatcher = emailPattern.matcher(email);
		if (!emailMatcher.matches()) {
			context.getMessageContext()
					.addMessage(new MessageBuilder()
							.error()
							.source("messages")
							.code("emailIsWrong")
							.defaultText("Email address seems to be wrong: " + email)
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

	public void validateSign(Client c, ValidationContext context) {
		String series = c.getPassport().getSeries();
		String number = c.getPassport().getNumber();

		boolean valid = true;
		if (series == null) {

		}

		if (!valid) {
			throw new IllegalArgumentException("Passport validation failed");
		}

	}

	private boolean isLoginUnique(String login, ValidationContext context) {
		boolean valid = true;
		Client clientWithSameLogin = clientDao.getByLogin(login);
		if (clientWithSameLogin != null) {
			context.getMessageContext()
					.addMessage(new MessageBuilder()
							.error()
							.source("messages")
							.code("loginNotUnique")
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
							.source("messages")
							.code("emailNotUnique")
							.defaultText("Client already exists with email " + email)
							.build());
			valid = false;
		}

		return valid;
	}

}
