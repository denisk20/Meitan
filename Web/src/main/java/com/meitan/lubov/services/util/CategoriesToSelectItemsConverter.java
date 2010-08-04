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
		return ArrayList.class;
	}

	@Override
	public Class getTargetClass() {
		return ArrayList.class;
	}

	@Override
	public Object convertSourceToTargetClass(Object source, Class targetClass) throws Exception {
		ArrayList<SelectItem> result = null;
		if (source instanceof Category) {
			Category category = (Category) source;
			result = new ArrayList<SelectItem>();
				SelectItem it = new SelectItem();
				it.setLabel(category.getName());
				it.setValue(category.getId());
				result.add(it);
		}
		return result;
/*
		Category c = (Category) source;
		return new SelectItem(c.getId(), c.getName());
*/
	}
}
