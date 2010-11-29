package com.meitan.lubov.flowtests;

import com.meitan.lubov.model.persistent.Category;
import com.meitan.lubov.model.persistent.Product;
import com.meitan.lubov.services.dao.CategoryDao;
import com.meitan.lubov.services.dao.ProductDao;
import com.meitan.lubov.services.util.DenisConversionService;
import com.meitan.lubov.services.util.Utils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.webflow.config.FlowDefinitionResource;
import org.springframework.webflow.config.FlowDefinitionResourceFactory;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowBuilderContext;

import java.util.ArrayList;
import java.util.List;

/**
 * @author denis_k
 *         Date: 18.06.2010
 *         Time: 15:07:16
 */
public class AddGoodFlowIntegrationTest extends AbstractFlowIntegrationTest{

	@Autowired
	private CategoryDao testCategoryDao;

	@Autowired
	private ProductDao testProductDao;

	@Override
	protected FlowDefinitionResource getResource(FlowDefinitionResourceFactory resourceFactory) {
		return resourceFactory.createFileResource(rootPath + "/Web/src/main/webapp/WEB-INF/flows/addGood/addGood-flow.xml");
	}

	@Override
	protected void configureFlowBuilderContext(MockFlowBuilderContext builderContext) {
		super.configureFlowBuilderContext(builderContext);
		builderContext.registerBean("categoryDao", testCategoryDao);
		builderContext.registerBean("utils", new Utils());
		builderContext.getFlowBuilderServices().setConversionService(new DenisConversionService());
	}

	@Test
	public void testFlowStart() {
		MockExternalContext context = new MockExternalContext();
		startFlow(context);
		
		assertCurrentStateEquals("addGood");

		ArrayList<Category> categories = (ArrayList<Category>) getRequiredViewAttribute("categories", ArrayList.class);
		assertEquals("Wrong number of categories fetched from DB", 1, categories.size());
	}

	@Test
	public void testAddGood() {
		setCurrentState("addGood");

		String name = "productName";
		Product newGood = new Product(name);
		getFlowScope().put("newGood", newGood);

		MockExternalContext context = new MockExternalContext();
		context.setEventId("create");

		resumeFlow(context);

		assertFlowExecutionEnded();
		assertFlowExecutionOutcomeEquals("create");

		List<Product> products = testProductDao.findByExample(newGood, "id");
		assertEquals("Wrong number of products fetched from database", 1, products.size());
		Product loaded = products.get(0);

		assertEquals("Wrong product", newGood, loaded);
	}

	@Test
	public void testBack() {
		setCurrentState("addGood");

		MockExternalContext context = new MockExternalContext();
		context.setEventId("back");

		resumeFlow(context);

		assertFlowExecutionEnded();
		assertFlowExecutionOutcomeEquals("back");
	}
}
