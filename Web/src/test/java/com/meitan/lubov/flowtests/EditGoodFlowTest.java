package com.meitan.lubov.flowtests;

import com.meitan.lubov.model.persistent.Category;
import com.meitan.lubov.model.persistent.Product;
import com.meitan.lubov.services.dao.CategoryDao;
import com.meitan.lubov.services.dao.ProductDao;
import com.meitan.lubov.services.util.Selectable;
import com.meitan.lubov.services.util.selectors.CategoriesSelector;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.webflow.config.FlowDefinitionResource;
import org.springframework.webflow.config.FlowDefinitionResourceFactory;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowBuilderContext;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * @author denis_k
 *         Date: 18.06.2010
 *         Time: 15:56:23
 */
public class EditGoodFlowTest extends AbstractFlowIntegrationTest{
	@Autowired
	private CategoryDao testCategoryDao;

	@Override
	protected FlowDefinitionResource getResource(FlowDefinitionResourceFactory resourceFactory) {
		return resourceFactory.createFileResource(rootPath + "/Web/src/main/webapp/WEB-INF/flows/editGood/editGood-flow.xml");
	}

	@Override
	protected void configureFlowBuilderContext(MockFlowBuilderContext builderContext) {
		super.configureFlowBuilderContext(builderContext);
		builderContext.registerBean("categoryDao", testCategoryDao);
	}

	@Test
	public void testEditGood() {
		Category newCategory = new Category("myCategory");
		testCategoryDao.makePersistent(newCategory);

		Product p = testProductDao.findAll().get(0);

		boolean initialNew = p.isNew();

		MockExternalContext context = new MockExternalContext();
		MutableAttributeMap input = new LocalAttributeMap();
		input.put("id", p.getId());

		startFlow(input, context);

		assertCurrentStateEquals("editGood");

		CategoriesSelector categoriesSelector = (CategoriesSelector) getFlowScope().getRequired("categoriesSelector");
		HashSet<Category> selectedCategories = new HashSet<Category>();

		for (Selectable<Category> s : categoriesSelector.getItems()) {
			if (s.isSelected()) {
				selectedCategories.add(s.getItem());
			}
		}

		int selectedItemsCount = selectedCategories.size();
		assertTrue(selectedItemsCount > 0);
		
		assertEquals("Wrong items selected", p.getCategories(), selectedCategories);

		Product productFromFlowScope = (Product) getViewScope().getRequired("product", Product.class);

		assertEquals("Products don't match", p, productFromFlowScope);

		categoriesSelector.addItem(newCategory, true);

		String newProductName = "newProductName";
		p.setName(newProductName);
		p.setNew(! initialNew);

		context.setEventId("save");

		resumeFlow(context);

		Product reloadedProduct = testProductDao.findById(p.getId());
		assertEquals("Wrong product name", newProductName, reloadedProduct.getName());
		assertEquals("Wrong NEW status of a product", ! initialNew, reloadedProduct.isNew());
		assertTrue("Category wasn't attached to a product", reloadedProduct.getCategories().contains(newCategory));

        assertFlowExecutionEnded();
		assertFlowExecutionOutcomeEquals("save");
	}

	@Test
	public void testBack() {
		setCurrentState("editGood");

		MockExternalContext context = new MockExternalContext();
		context.setEventId("back");

		resumeFlow(context);

		assertFlowExecutionEnded();
		assertFlowExecutionOutcomeEquals("cancel");
	}
}
