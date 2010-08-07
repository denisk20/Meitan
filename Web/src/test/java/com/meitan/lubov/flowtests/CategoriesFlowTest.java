package com.meitan.lubov.flowtests;

import com.meitan.lubov.model.ImageAware;
import com.meitan.lubov.model.persistent.Category;
import com.meitan.lubov.services.dao.CategoryDao;
import com.meitan.lubov.services.util.DenisConversionService;
import com.meitan.lubov.services.util.FileBackupRestoreManager;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.mapping.Mapper;
import org.springframework.binding.mapping.MappingResults;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.faces.model.OneSelectionTrackingListDataModel;
import org.springframework.webflow.config.FlowDefinitionResource;
import org.springframework.webflow.config.FlowDefinitionResourceFactory;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.engine.EndState;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowBuilderContext;
import org.springframework.webflow.test.MockParameterMap;

import javax.faces.model.DataModel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author denis_k
 *         Date: 18.06.2010
 *         Time: 12:09:01
 */
public class CategoriesFlowTest extends AbstractFlowIntegrationTest {
	@Autowired
	private CategoryDao testCategoryDao;

	@Override
	protected FlowDefinitionResource[] getModelResources(FlowDefinitionResourceFactory resourceFactory) {
		FlowDefinitionResource[] superResources = super.getModelResources(resourceFactory);


		ArrayList<FlowDefinitionResource> result = new ArrayList<FlowDefinitionResource>();

		result.addAll(Arrays.asList(superResources));

		Resource localFlowResource3 = new FileSystemResource(new File(rootPath + "/Web/src/main/webapp/WEB-INF/flows/abstractEditable/abstractEditable-flow.xml"));
		result.add(new FlowDefinitionResource("abstractEditable", localFlowResource3, null));

		return result.toArray(new FlowDefinitionResource[0]);
	}

	@Override
	protected FlowDefinitionResource getResource(FlowDefinitionResourceFactory resourceFactory) {
		return resourceFactory.createFileResource(rootPath + "/Web/src/main/webapp/WEB-INF/flows/categories/categories-flow.xml");
	}

	@Override
	protected void configureFlowBuilderContext(MockFlowBuilderContext builderContext) {
		builderContext.registerBean("categoryDao", testCategoryDao);
		builderContext.getFlowBuilderServices().setConversionService(new DenisConversionService());
	}

	@Test
	public void testFlowStart() {
		MockExternalContext context = new MockExternalContext();
		startFlow(context);

		assertCurrentStateEquals("categories");
		List<Category> categories = (List<Category>) getRequiredViewAttribute("categories");
		assertEquals("Wrong number of categories fetched from DB", 1, categories.size());
	}

	@Test
	public void testViewAllGoods() {
		MockExternalContext context = new MockExternalContext();
		startFlow(context);
		setCurrentState("categories");
		context.setEventId("all");

		resumeFlow(context);

		assertFlowExecutionEnded();
		assertFlowExecutionOutcomeEquals("selectFlow");
	}

	@Test
	public void testViewGoodsForCategory() {
		MockExternalContext context = new MockExternalContext();
		startFlow(context);

		setCurrentState("categories");
		Category c = new Category("some category");
		c.setId(1L);
		ArrayList<Category> list = new ArrayList<Category>();
		list.add(c);

		getViewScope().put("categories", list);

		context.setEventId("select");

		resumeFlow(context);

		assertFlowExecutionEnded();
		assertFlowExecutionOutcomeEquals("selectFlow");
	}

	@Test
	public void testEditCategory() {
		MockExternalContext context = new MockExternalContext();
		startFlow(context);
		
		setCurrentState("categories");

		long categoryId = 1L;

		getFlowDefinitionRegistry().registerFlowDefinition(FlowTestUtils.createMockEditCategoryFlow(categoryId));


		MockParameterMap map = new MockParameterMap();
		map.put("id", Long.toString(categoryId));
		context.setRequestParameterMap(map);

		context.setEventId("edit");

		resumeFlow(context);


		assertFlowExecutionActive();
		assertCurrentStateEquals("categories");
	}

	//todo merge this with testEditCategory
	@Test
	public void testEditImages() {
		MockExternalContext context = new MockExternalContext();
		startFlow(context);

		setCurrentState("categories");

		long categoryId = 1L;

		getFlowDefinitionRegistry().registerFlowDefinition(FlowTestUtils.createMockImagesManagerFlow(categoryId, Category.class.getName()));

		MockParameterMap map = new MockParameterMap();
		map.put("id", Long.toString(categoryId));
		context.setRequestParameterMap(map);

		context.setEventId("editImages");

		resumeFlow(context);

		assertFlowExecutionActive();
		assertCurrentStateEquals("categories");
	}

	@Test
	//todo update to test product deletion as well
	public void testDeleteCategory() throws IOException {
		MockExternalContext context = new MockExternalContext();
		startFlow(context);
		Category persistentCategory = testCategoryDao.findAll().get(0);

		FileBackupRestoreManager restoreManager = new FileBackupRestoreManager(rootPath + persistentCategory.getImage().getUrl());
		restoreManager.backup();

		ArrayList<Category> categories = new ArrayList<Category>();
		categories.add(persistentCategory);

		try {
			setCurrentState("categories");

			getViewScope().put("categories", categories);


			MockParameterMap map = new MockParameterMap();
			map.put("id", persistentCategory.getId().toString());
			context.setRequestParameterMap(map);

			context.setEventId("delete");

			resumeFlow(context);

			assertFlowExecutionActive();
			assertCurrentStateEquals("categories");

			Category deleted = testCategoryDao.findById(persistentCategory.getId());
			assertNull("Category wasn't deleted properly", deleted);
			testCategoryDao.flush();
		} finally {
			restoreManager.restore();
		}
	}

	@Test
	public void testAddCategorySubflow() {
		setCurrentState("categories");

		Flow addCategorySubflow = new Flow("addCategory");
		new EndState(addCategorySubflow, "categoryCreated");
		getFlowDefinitionRegistry().registerFlowDefinition(addCategorySubflow);

		MockExternalContext context = new MockExternalContext();
		context.setEventId("add");

		resumeFlow(context);

		assertFlowExecutionActive();
		assertCurrentStateEquals("categories");
	}

	@Test
	public void testAddGoodSubflow() {
		setCurrentState("categories");

		Flow addGoodSubflow = new Flow("addGood");
		new EndState(addGoodSubflow, "create");
		getFlowDefinitionRegistry().registerFlowDefinition(addGoodSubflow);

		MockExternalContext context = new MockExternalContext();
		context.setEventId("addGood");

		resumeFlow(context);

		assertFlowExecutionActive();
		assertCurrentStateEquals("categories");
	}

}
