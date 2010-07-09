package com.meitan.lubov.services.util;

import org.springframework.binding.message.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.webflow.execution.RequestContext;

/**
 * @author denis_k
 *         Date: 09.07.2010
 *         Time: 22:01:48
 */
@Service("passwordsValidator")
public class PasswordValidator {
	public void validatePasswords(String original, String conformed, RequestContext requestContext) throws Exception {
		if (original == null || conformed == null) {
			throw new Exception("Illegal arguments: original = " + original + ", conformed= " + conformed);
		}
		if (!original.equals(conformed)) {
			requestContext.getMessageContext()
					.addMessage(new MessageBuilder()
							.error()
							.defaultText("Passwords don't match")
							.build());
			throw new IllegalArgumentException("Passwords don't match");
		}
	}
}
