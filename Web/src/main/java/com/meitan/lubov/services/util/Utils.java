package com.meitan.lubov.services.util;

import com.sun.facelets.el.TagMethodExpression;
import com.sun.facelets.tag.Location;
import com.sun.facelets.tag.TagAttribute;
import org.jboss.el.MethodExpressionLiteral;
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
	private static final int STRING_LIMIT = 30;
	private static final String DOTS = "...";

	public ArrayList asList(Set s) {
		if (s != null) {
			return new ArrayList(s);
		} else {
			return null;
		}
	}

	//todo move this into SecurityService 
	public String getMD5(String source) {
		PasswordEncoder passwordEncoder = new Md5PasswordEncoder();
		String pass = passwordEncoder.encodePassword(source, null);
		return pass;
	}

	public TagMethodExpression getMethodExpression(String expression) {
		Location location = new Location("myPath", 1,1);
		TagAttribute attribute = new TagAttribute(location, "meitan", "custom", "custom", "no value");
		TagMethodExpression result = new TagMethodExpression(attribute, new MethodExpressionLiteral(expression, String.class, new Class[0]));

		return result;
	}

	public String getShortName(String longName) {
		String result="";
		if (longName.length() > STRING_LIMIT) {
			result = longName.substring(0, STRING_LIMIT);
		}
		result = longName + DOTS;

		return result;
	}
}
