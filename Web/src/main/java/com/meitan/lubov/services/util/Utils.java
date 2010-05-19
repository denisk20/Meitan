package com.meitan.lubov.services.util;

import java.util.ArrayList;
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
}
