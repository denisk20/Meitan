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
import org.springframework.faces.model.OneSelectionTrackingListDataModel;
import org.springframework.webflow.config.FlowDefinitionResource;
import org.springframework.webflow.config.FlowDefinitionResourceFactory;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.engine.EndState;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowBuilderContext;

import javax.faces.model.DataModel;
import java.io.IOException;
import java.util.ArrayList;
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
		DataModel categoriesDataModel = (DataModel) getRequiredViewAttribute("categories", DataModel.class);
		List<Category> categories = (List<Category>) categoriesDataModel.getWrappedData();
		assertEquals("Wrong number of categories fetched from DB", 1, categories.size());
	}

	@Test
	public void testViewAllGoods() {
		MockExternalContext context = new MockExternalContext();

		setCurrentState("categories");
		context.setEventId("all");

		resumeFlow(context);

		assertFlowExecutionEnded();
		assertFlowExecutionOutcomeEquals("goodsListForCategory");
	}

	@Test
	public void testViewGoodsForCategory() {

		setCurrentState("categories");
		getViewScope().put("categories", new OneSelectionTrackingListDataModel());

		MockExternalContext context = new MockExternalContext();
		context.setEventId("selectCategory");

		resumeFlow(context);

		assertFlowExecutionEnded();
		assertFlowExecutionOutcomeEquals("goodsListForCategory");
	}

	@Test
	public void testEditCategory() {
		setCurrentState("categories");

		Category c = new Category("myCategory");
		long categoryId = 1L;
		c.setId(categoryId);

		ArrayList<Category> categories = new ArrayList<Category>();
		categories.add(c);
		OneSelectionTrackingListDataModel categoriesDataModel = new OneSelectionTrackingListDataModel(categories);
		categoriesDataModel.select(c);
		
		getViewScope().put("categories", categoriesDataModel);

		getFlowDefinitionRegistry().registerFlowDefinition(FlowTestUtils.createMockEditCategoryFlow(categoryId));

		MockExternalContext context = new MockExternalContext();
		context.setEventId("edit");
		
		resumeFlow(context);

		assertFlowExecutionActive();
		assertCurrentStateEquals("categories");
	}

	//todo merge this with testEditCategory
	@Test
	public void testEditImages() {
		setCurrentState("categories");

		Category c = new Category("myCategory");
		long categoryId = 1L;
		c.setId(categoryId);

		ArrayList<Category> imageAwares = new ArrayList<Category>();
		imageAwares.add(c);

		OneSelectionTrackingListDataModel categoriesDataModel = new OneSelectionTrackingListDataModel(imageAwares);
		categoriesDataModel.select(c);

		getViewScope().put("categories", categoriesDataModel);

		getFlowDefinitionRegistry().registerFlowDefinition(FlowTestUtils.createMockImagesManagerFlow(categoryId));

		MockExternalContext context = new MockExternalContext();
		context.setEventId("editimages");

		resumeFlow(context);

		assertFlowExecutionActive();
		assertCurrentStateEquals("categories");
	}

	@Test
	//todo update to test product deletion as well
	public void testDeleteCategory() throws IOException {
		Category persistentCategory = testCategoryDao.findAll().get(0);

		FileBackupRestoreManager restoreManager = new FileBackupRestoreManager(rootPath + persistentCategory.getImage().getUrl());
		restoreManager.backup();

		ArrayList<Category> categories = new ArrayList<Category>();
		categories.add(persistentCategory);

		OneSelectionTrackingListDataModel categoriesDataModel = new OneSelectionTrackingListDataModel(categories);
		categoriesDataModel.select(persistentCategory);

		try {
			setCurrentState("categories");

			getViewScope().put("categories", categoriesDataModel);

			MockExternalContext context = new MockExternalContext();
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
