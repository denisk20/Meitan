package com.meitan.lubov.services.util;

import org.springframework.faces.model.converter.FacesConversionService;
import org.springframework.stereotype.Component;

import javax.faces.model.SelectItem;
import java.util.ArrayList;

/**
 * @author denis_k
 *         Date: 14.05.2010
 *         Time: 15:57:16
 */
@Component("myConversionService")
public class DenisConversionService extends FacesConversionService {
	public DenisConversionService() {
		super();
		addConverter(new CategoriesToSelectItemsConverter());
		addAlias("selectItems", SelectItem.class);
	}
}
