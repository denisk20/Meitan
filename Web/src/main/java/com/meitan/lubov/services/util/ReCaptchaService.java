package com.meitan.lubov.services.util;

import net.tanesha.recaptcha.ReCaptchaException;
import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.security.wrapper.SavedRequestAwareWrapper;
import org.springframework.stereotype.Service;
import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.execution.RequestContext;

/**
 * Date: Jul 8, 2010
 * Time: 3:51:53 PM
 *
 * @author denisk
 */
@Service("captchaService")
public class ReCaptchaService implements CaptchaService{
	@Override
	public void validateCaptcha(RequestContext requestContext) {
		ParameterMap parameterMap = requestContext.getExternalContext().getRequestParameterMap();
		String challenge = parameterMap.get("recaptcha_challenge_field");
		String response = parameterMap.get("recaptcha_response_field");

		ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
		reCaptcha.setPrivateKey("6LcTPLsSAAAAAHuWXn5FMN4Cx96AXOdVVr0w2O0-");

		String remoteAddress = ((SavedRequestAwareWrapper) requestContext.getExternalContext()
				.getNativeRequest()).getRemoteAddr();

		ProxyHttpLoader httpLoader = new ProxyHttpLoader("10.10.0.1", 3128);
		reCaptcha.setHttpLoader(httpLoader);

		ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(remoteAddress, challenge, response);

		if (!reCaptchaResponse.isValid()) {
			requestContext.getMessageContext()
					.addMessage(new MessageBuilder()
							.error()
							.defaultText("You entered wrong words, try again")
							.build());
			throw new ReCaptchaException(reCaptchaResponse.getErrorMessage());
		}
	}
}
