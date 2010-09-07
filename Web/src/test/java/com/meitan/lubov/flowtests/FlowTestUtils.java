package com.meitan.lubov.flowtests;

import com.meitan.lubov.model.ImageAware;
import com.meitan.lubov.model.persistent.Product;
import org.springframework.binding.mapping.Mapper;
import org.springframework.binding.mapping.MappingResults;
import org.springframework.faces.model.OneSelectionTrackingListDataModel;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.engine.EndState;
import org.springframework.webflow.engine.Flow;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * @author denis_k
 *         Date: 21.06.2010
 *         Time: 13:01:57
 */
public class FlowTestUtils {
	public static Flow createMockImagesManagerFlow(final Long itemId, final String className) {
		Flow imagesManager = new Flow("imagesManager");
		imagesManager.setInputMapper(new Mapper(){
			@Override
			public MappingResults map(Object source, Object target) {
				AttributeMap sourceMap = (AttributeMap) source;
				Long imageAwareId = (Long) sourceMap.get("imageAwareId");
				assertEquals("Wrong Id of category was selected", itemId, imageAwareId);
				assertEquals("Wrong className", className, sourceMap.get("className"));
				return null;
			}
		});
		new EndState(imagesManager, "save");
		return imagesManager;
	}

	public static Flow createMockEditCategoryFlow(final Long categoryId) {
		Flow editCategory = new Flow("editCategory");
		editCategory.setInputMapper(new Mapper(){
			@Override
			public MappingResults map(Object source, Object target) {
				assertEquals("Wrong Id of category was selected", categoryId, ((AttributeMap) source).get("id", Long.class));
				return null;
			}
		});
		new EndState(editCategory, "save");
		return editCategory;
	}

	public static Flow getMockEditProductSubflow(final Long prodId) {
		Flow editCategorySubflow = new Flow("editGood");
		editCategorySubflow.setInputMapper(new Mapper(){
			@Override
			public MappingResults map(Object source, Object target) {
				assertEquals("Wrong Id of product was selected", prodId, ((AttributeMap) source).get("id", Long.class));
				return null;
			}
		});
		new EndState(editCategorySubflow, "save");
		return editCategorySubflow;
	}

//	public static OneSelectionTrackingListDataModel getProductDataModel(Long selectedProdId) {
//		Product p1 = new Product("p1");
//		p1.setId(selectedProdId);
//
//		Product p2 = new Product("p2");
//
//		ArrayList<Product> products = new ArrayList<Product>();
//		products.add(p1);
//		products.add(p2);
//
//		OneSelectionTrackingListDataModel dataModel = new OneSelectionTrackingListDataModel(products);
//		dataModel.select(p1);
//		return dataModel;
//	}

}
