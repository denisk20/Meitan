package com.meitan.lubov.services.util;

import org.springframework.security.providers.encoding.Md5PasswordEncoder;
import org.springframework.security.providers.encoding.PasswordEncoder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author denis_k
 *         Date: 19.05.2010
 *         Time: 15:39:14
 */
public class Utils {
	public ArrayList asList(Set s) {
		if (s != null) {
			return new ArrayList(s);
		} else {
			return null;
		}
	}

	public String getMD5(String source) {
		PasswordEncoder passwordEncoder = new Md5PasswordEncoder();
		String pass = passwordEncoder.encodePassword(source, null);
		return pass;
	}
}
