package com.meitan.lubov.services.util;

import org.springframework.webflow.execution.RequestContext;

/**
 * Date: Jul 8, 2010
 * Time: 3:49:56 PM
 *
 * @author denisk
 */
public interface CaptchaService {
	void validateCaptcha(RequestContext requestContext);
}
