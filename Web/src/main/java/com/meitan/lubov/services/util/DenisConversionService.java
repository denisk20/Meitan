package com.meitan.lubov.services.util;

import org.springframework.faces.model.converter.FacesConversionService;
import org.springframework.stereotype.Component;

import javax.faces.model.SelectItem;
import java.util.ArrayList;

/**
 * @author denis_k
 *         Date: 14.05.2010
 *         Time: 15:57:16
 * I left this only as a reference
 */
@Component("myConversionService")
//The value of above annotation is used in webflow-config.xml
public class DenisConversionService extends FacesConversionService {
	public DenisConversionService() {
		super();
		addConverter(new CategoriesToSelectItemsConverter());
		addAlias("selectItems", SelectItem.class);
	}
}
