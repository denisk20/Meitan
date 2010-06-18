package com.meitan.lubov.flowtests;

import com.meitan.lubov.model.persistent.Category;
import com.meitan.lubov.services.dao.CategoryDao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.webflow.config.FlowDefinitionResource;
import org.springframework.webflow.config.FlowDefinitionResourceFactory;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowBuilderContext;

/**
 * @author denis_k
 *         Date: 18.06.2010
 *         Time: 10:54:25
 */
public class AddCategoryFlowTest extends AbstractFlowIntegrationTest {

	@Autowired
	private CategoryDao testCategoryDao;

	@Override
	protected FlowDefinitionResource getResource(FlowDefinitionResourceFactory resourceFactory) {
		return resourceFactory.createFileResource(rootPath + "/Web/src/main/webapp/WEB-INF/flows/addCategory/addCategory-flow.xml");
	}

	@Override
	protected void configureFlowBuilderContext(MockFlowBuilderContext builderContext) {
		builderContext.registerBean("categoryDao", testCategoryDao);
	}

	@Test
	public void testFlowStart() {
		MockExternalContext context = new MockExternalContext();
		startFlow(context);

		assertCurrentStateEquals("addCategory");
		Category newCategory = (Category) getRequiredFlowAttribute("newCategory", Category.class);

		final String name = "myName";

		newCategory.setName(name);

		assertEquals("Wrong transient category ID :", new Long(0), newCategory.getId());

		context.setEventId("create");

		resumeFlow(context);

		assertFlowExecutionEnded();
		assertFlowExecutionOutcomeEquals("categoryCreated");
		String outputAttributeName = "createdCategoryName";
		String flowOutput = (String) getFlowExecutionOutcome().getOutput().get(outputAttributeName, String.class);
		assertNotNull("Flow output was null", flowOutput);
		assertEquals("Wrong flow output", "myName", flowOutput);
		
		assertNotNull(newCategory.getId());

		Category created = testCategoryDao.findById(newCategory.getId());
		assertNotNull("Category wasn't created", created);
		assertEquals("Category has wrong name", name, created.getName());
	}
}
