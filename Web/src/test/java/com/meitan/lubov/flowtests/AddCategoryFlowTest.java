package com.meitan.lubov.flowtests;

import com.meitan.lubov.model.persistent.Category;
import com.meitan.lubov.services.dao.CategoryDao;
import com.meitan.lubov.services.dao.ProductDao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.webflow.config.FlowDefinitionResource;
import org.springframework.webflow.config.FlowDefinitionResourceFactory;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowBuilderContext;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author denis_k
 *         Date: 18.06.2010
 *         Time: 10:54:25
 */
public class AddCategoryFlowTest extends AbstractFlowIntegrationTest {

	@Autowired
	private CategoryDao testCategoryDao;

	@Autowired
	private ProductDao testProductDao;

	@Override
	protected FlowDefinitionResource getResource(FlowDefinitionResourceFactory resourceFactory) {
		return resourceFactory.createFileResource(rootPath + "/Web/src/main/webapp/WEB-INF/flows/addCategory/addCategory-flow.xml");
	}

	@Override
	protected void configureFlowBuilderContext(MockFlowBuilderContext builderContext) {
		super.configureFlowBuilderContext(builderContext);
		builderContext.registerBean("categoryDao", testCategoryDao);
	}

	@Override
	protected FlowDefinitionResource[] getModelResources(FlowDefinitionResourceFactory resourceFactory) {
		FlowDefinitionResource[] superResources = super.getModelResources(resourceFactory);

		ArrayList<FlowDefinitionResource> result = new ArrayList<FlowDefinitionResource>();

		result.addAll(Arrays.asList(superResources));

		Resource localFlowResource1 = new FileSystemResource(new File(rootPath + "/Web/src/main/webapp/WEB-INF/flows/baseGood/baseGood-flow.xml"));
		Resource localFlowResource2 = new FileSystemResource(new File(rootPath + "/Web/src/main/webapp/WEB-INF/flows/abstractEditable/abstractEditable-flow.xml"));
		result.add(new FlowDefinitionResource("baseGood", localFlowResource1, null));
		result.add(new FlowDefinitionResource("abstractEditable", localFlowResource2, null));

		return result.toArray(new FlowDefinitionResource[0]);
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
