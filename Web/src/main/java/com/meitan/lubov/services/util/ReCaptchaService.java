package com.meitan.lubov.services.util;

import net.tanesha.recaptcha.ReCaptchaException;
import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;
import net.tanesha.recaptcha.http.HttpLoader;
import net.tanesha.recaptcha.http.SimpleHttpLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Service;
import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.execution.RequestContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Date: Jul 8, 2010
 * Time: 3:51:53 PM
 *
 * @author denisk
 */
@Service("captchaService")
//todo unit test this
public class ReCaptchaService implements CaptchaService{
	private final Log log = LogFactory.getLog(getClass());

	@Autowired
	private Utils utils;
	private static final String PROXY_ADDRESS = "10.10.0.1";
	private static final int PROXY_PORT = 3128;
	private static final String MEITAN_USE_PROXY = "meitan.use_proxy";
	private static final String RECAPTCHA_PRIVATE_KEY = "6LcTPLsSAAAAAHuWXn5FMN4Cx96AXOdVVr0w2O0-";
	private static final String RECAPTCHA_CHALLENGE_FIELD = "recaptcha_challenge_field";
	private static final String RECAPTCHA_RESPONSE_FIELD = "recaptcha_response_field";

	@Override
	public void validateCaptcha(RequestContext requestContext) {
		ParameterMap parameterMap = requestContext.getExternalContext().getRequestParameterMap();
		String challenge = parameterMap.get(RECAPTCHA_CHALLENGE_FIELD);
		String response = parameterMap.get(RECAPTCHA_RESPONSE_FIELD);

		ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
		reCaptcha.setPrivateKey(RECAPTCHA_PRIVATE_KEY);

		String remoteAddress = ((SecurityContextHolderAwareRequestWrapper) requestContext.getExternalContext()
				.getNativeRequest()).getRemoteAddr();

		HttpLoader httpLoader = getHttpLoader();
		
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

	private HttpLoader getHttpLoader() {
		HttpLoader httpLoader;
		Boolean useProxy = Boolean.parseBoolean(utils.getMeitanProperty(MEITAN_USE_PROXY));
		if (useProxy) {
			httpLoader = new ProxyHttpLoader(PROXY_ADDRESS, PROXY_PORT);
		} else {
			httpLoader = new SimpleHttpLoader();
		}
		return httpLoader;
	}
}
