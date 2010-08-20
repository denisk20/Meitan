package com.meitan.lubov.services.util;

import com.meitan.lubov.model.persistent.Product;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.stereotype.Component;

/**
 * Date: Aug 20, 2010
 * Time: 4:35:43 PM
 *
 * @author denisk
 */
//@Component
public class ProductValidator {
	public void validateEditGood(Product p, ValidationContext context) {
		if (! p.getName().equals("true")) {
			throw new IllegalArgumentException("False product!");
		}
	}
}
