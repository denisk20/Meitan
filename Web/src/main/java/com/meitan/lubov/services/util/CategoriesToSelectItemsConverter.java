package com.meitan.lubov.services.util;

import com.meitan.lubov.model.persistent.Category;
import org.springframework.binding.convert.converters.Converter;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

/**
 * @author denis_k
 *         Date: 14.05.2010
 *         Time: 14:26:31
 * I left this only as a reference
*/
public class CategoriesToSelectItemsConverter implements Converter {
	@Override
	public Class getSourceClass() {
		return Object.class;
	}

	@Override
	public Class getTargetClass() {
		return SelectItem.class;
	}

	@Override
	public Object convertSourceToTargetClass(Object source, Class targetClass) throws Exception {
		List<SelectItem> result = null;
		if (source instanceof List) {
			List<Category> categories = (List<Category>) source;
			result = new ArrayList<SelectItem>();
			for (Category c : categories) {
				SelectItem it = new SelectItem();
				it.setLabel(c.getName());
				it.setValue(c.getId());
				result.add(it);
			}
		}
		return result;
/*
		Category c = (Category) source;
		return new SelectItem(c.getId(), c.getName());
*/
	}
}
