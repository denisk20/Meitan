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
	private final static String PASSPORT_SERIES_REGEXP = "[a-zA-ZА-Я]{2}";
	private final static String PASSPORT_NUMBER_REGEXP = "[\\d]{5}";
	private static final String EMAIL_REGEXP = "[a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}";
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
		if (email == null) {
			context.getMessageContext()
					.addMessage(new MessageBuilder()
							.error()
							.source("messages")
							.code("emailCantBeNull")
							.defaultText("Email can't be null")
							.build());
			valid = false;
		}

		Pattern emailPattern = Pattern.compile(EMAIL_REGEXP);
		Matcher emailMatcher = emailPattern.matcher(email);
		if (!emailMatcher.matches()) {
			context.getMessageContext()
					.addMessage(new MessageBuilder()
							.error()
							.source("messages")
							.code("emailIsWrong")
							.defaultText("Email address seems to be wrong: ")
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

		Pattern seriesPattern = Pattern.compile(PASSPORT_SERIES_REGEXP);
		Matcher seriesMatcher = seriesPattern.matcher(series);
		if (series == null || !seriesMatcher.matches()) {
			context.getMessageContext()
					.addMessage(new MessageBuilder()
							.error()
							.source("messages")
							.code("passportSeriesIsWrong")
							.defaultText("Passport series is wrong")
							.build());
			valid = false;
		}

		Pattern numberPattern = Pattern.compile(PASSPORT_NUMBER_REGEXP);
		Matcher numberMatcher = numberPattern.matcher(number);
		if (number == null || !numberMatcher.matches()) {
			context.getMessageContext()
					.addMessage(new MessageBuilder()
							.error()
							.source("messages")
							.code("passportNumberIsWrong")
							.defaultText("Passport number is wrong")
							.build());
			valid = false;
		}




		if (!valid) {
			throw new IllegalArgumentException("Passport validation failed");
		}

	}

	public void validateQuickRegistration(Client c, ValidationContext context) {
		boolean valid = true;

		String email = c.getEmail();

		Pattern emailPattern = Pattern.compile(EMAIL_REGEXP);
		Matcher emailMatcher = emailPattern.matcher(email);
		if (email == null || !emailMatcher.matches()) {
			context.getMessageContext()
					.addMessage(new MessageBuilder()
							.error()
							.source("messages")
							.code("emailIsWrong")
							.defaultText("Email address seems to be wrong")
							.build());
			valid = false;
		}

		Client existingClient = null;
		if (c.getId() != 0) {
			existingClient = clientDao.findById(c.getId());
		}
		if (existingClient != null) {
			if (email != null) {
				if (!email.equals(existingClient.getEmail())) {
					if (!isEmailUnique(email, context)) {
						valid = false;
					}
				}
			}
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
