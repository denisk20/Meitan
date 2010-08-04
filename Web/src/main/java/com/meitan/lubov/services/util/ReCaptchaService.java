package com.meitan.lubov.services.util;

import net.tanesha.recaptcha.ReCaptchaException;
import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;
import net.tanesha.recaptcha.http.HttpLoader;
import net.tanesha.recaptcha.http.SimpleHttpLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
	@Override
	public void validateCaptcha(RequestContext requestContext) {
		ParameterMap parameterMap = requestContext.getExternalContext().getRequestParameterMap();
		//todo!!!
		String challenge = parameterMap.get("recaptcha_challenge_field");
		String response = parameterMap.get("recaptcha_response_field");

		ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
		//todo!!!
		reCaptcha.setPrivateKey("6LcTPLsSAAAAAHuWXn5FMN4Cx96AXOdVVr0w2O0-");

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
		//todo!!!
		File pathToHibernateProperties = new File(System.getenv("MEITAN_PROPS") + "/common.properties");
		boolean useProxy = false;
		FileInputStream in = null;
		try {
			in = new FileInputStream(pathToHibernateProperties);
		} catch (FileNotFoundException e) {
			log.warn("Can't find common.properties!");
		}
		Properties meitanProps = new Properties();
		try {
			meitanProps.load(in);
			useProxy = Boolean.parseBoolean((String) meitanProps.get("meitan.use_proxy"));
		} catch (IOException e) {
			log.warn("Can't load common.properties!");
		}
		//todo
		HttpLoader httpLoader;
		if (useProxy) {
			//todo
			httpLoader = new ProxyHttpLoader("10.10.0.1", 3128);
		} else {
			httpLoader = new SimpleHttpLoader();
		}
		return httpLoader;
	}
}
