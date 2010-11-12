package com.meitan.lubov.services.util;

import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Date: Nov 12, 2010
 * Time: 12:36:13 PM
 *
 * @author denisk
 */
public class ControlledResourceBundleMessageSource extends ResourceBundleMessageSource {
	private ResourceBundle.Control control;

	public ResourceBundle.Control getControl() {
		return control;
	}

	public void setControl(ResourceBundle.Control control) {
		this.control = control;
	}

	@Override
	protected ResourceBundle doGetBundle(String basename, Locale locale) throws MissingResourceException {
		return ResourceBundle.getBundle(basename, locale, getBundleClassLoader(), control);
	}
}
