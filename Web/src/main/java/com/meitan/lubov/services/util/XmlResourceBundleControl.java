package com.meitan.lubov.services.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Date: Sep 21, 2010
 * Time: 5:05:03 PM
 *
 * @author denisk
 */
public class XmlResourceBundleControl extends ResourceBundle.Control {
	public List<String> getFormats(String baseName) {
		if (baseName == null)
			throw new NullPointerException();
		return Arrays.asList("xml");
	}

	public ResourceBundle newBundle(String baseName,
									Locale locale,
									String format,
									ClassLoader loader,
									boolean reload)
					 throws IllegalAccessException,
							InstantiationException,
			IOException {
		if (baseName == null || locale == null
			  || format == null || loader == null)
			throw new NullPointerException();
		ResourceBundle bundle = null;
		if (format.equals("xml")) {
			String bundleName = toBundleName(baseName, locale);
			String resourceName = toResourceName(bundleName, format);
			InputStream stream = null;
			if (reload) {
				URL url = loader.getResource(resourceName);
				if (url != null) {
					URLConnection connection = url.openConnection();
					if (connection != null) {
						// Disable caches to get fresh data for
						// reloading.
						connection.setUseCaches(false);
						stream = connection.getInputStream();
					}
				}
			} else {
				stream = loader.getResourceAsStream(resourceName);
			}
			if (stream != null) {
				BufferedInputStream bis = new BufferedInputStream(stream);
				bundle = new XMLResourceBundle(bis);
				bis.close();
			}
		}
		return bundle;
	}

	private static class XMLResourceBundle extends ResourceBundle {
		private Properties props;
		XMLResourceBundle(InputStream stream) throws IOException {
			props = new Properties();
			props.loadFromXML(stream);
		}
		protected Object handleGetObject(String key) {
			return props.getProperty(key);
		}

		@Override
		public Enumeration<String> getKeys() {
			return null;
		}
	}

}
