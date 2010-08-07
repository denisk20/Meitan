package com.meitan.lubov.flowtests;

import com.meitan.lubov.model.persistent.Category;
import com.meitan.lubov.services.dao.CategoryDao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.webflow.config.FlowDefinitionResource;
import org.springframework.webflow.config.FlowDefinitionResourceFactory;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowBuilderContext;

/**
 * @author denis_k
 *         Date: 18.06.2010
 *         Time: 14:38:38
 */
public class EditCategoryFlowTest extends AbstractFlowIntegrationTest{
	@Autowired
	private CategoryDao testCategoryDao;

	@Override
	protected FlowDefinitionResource getResource(FlowDefinitionResourceFactory resourceFactory) {
		return resourceFactory.createFileResource(rootPath + "/Web/src/main/webapp/WEB-INF/flows/editCategory/editCategory-flow.xml");
	}

	@Override
	protected void configureFlowBuilderContext(MockFlowBuilderContext builderContext) {
		builderContext.registerBean("categoryDao", testCategoryDao);
	}


	@Test
	public void testFlowStart() {
		Category persistentCategory = testCategoryDao.findAll().get(0);
		MutableAttributeMap input = new LocalAttributeMap();
		input.put("id", persistentCategory.getId());

		MockExternalContext context = new MockExternalContext();
		startFlow(input, context);

		assertCurrentStateEquals("edit");

		Category loadedCategory = (Category) getRequiredFlowAttribute("category", Category.class);

		assertEquals("Categories doesn't match", persistentCategory, loadedCategory);
	}

	@Test
	public void testModifyCategory() {
		Category persistentCategory = testCategoryDao.findAll().get(0);
		final String newName = "newName";
		persistentCategory.setName(newName);

		setCurrentState("edit");
		getFlowScope().put("category", persistentCategory);

		MockExternalContext context = new MockExternalContext();
		context.setEventId("save");

		resumeFlow(context);

		assertFlowExecutionEnded();
		assertFlowExecutionOutcomeEquals("save");

		Category loaded = testCategoryDao.findById(persistentCategory.getId());
		assertEquals("Category name wasn't modified", newName, loaded.getName());
	}
}
