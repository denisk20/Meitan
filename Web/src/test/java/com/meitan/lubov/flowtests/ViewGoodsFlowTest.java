package com.meitan.lubov.flowtests;

import com.meitan.lubov.model.PriceAware;
import com.meitan.lubov.model.persistent.Category;
import com.meitan.lubov.model.persistent.Image;
import com.meitan.lubov.model.persistent.Product;
import com.meitan.lubov.services.commerce.ShoppingCart;
import com.meitan.lubov.services.commerce.ShoppingCartImpl;
import com.meitan.lubov.services.dao.CategoryDao;
import com.meitan.lubov.services.dao.ProductDao;
import com.meitan.lubov.services.dao.jpa.JpaDao;
import com.meitan.lubov.services.util.DenisConversionService;
import com.meitan.lubov.services.util.FileBackupRestoreManager;
import com.meitan.lubov.services.util.Utils;
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
import org.springframework.webflow.core.collection.LocalAttributeMap;
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
import java.util.Set;

/**
 * @author denis_k
 *         Date: 21.06.2010
 *         Time: 11:51:46
 */
public class ViewGoodsFlowTest extends AbstractFlowIntegrationTest {
	@Autowired
	private ProductDao testProductDao;
	@Autowired
	private CategoryDao testCategoryDao;

	private ShoppingCart cart = new ShoppingCartImpl();

	private Utils utils = new Utils();

	@Override
	protected FlowDefinitionResource getResource(FlowDefinitionResourceFactory resourceFactory) {
		return resourceFactory.createFileResource(rootPath + "/Web/src/main/webapp/WEB-INF/flows/viewGoods/viewGoods-flow.xml");
	}

	@Override
	protected void configureFlowBuilderContext(MockFlowBuilderContext builderContext) {
		builderContext.registerBean("productDao", testProductDao);
		builderContext.registerBean("cart", cart);
		builderContext.getFlowBuilderServices().setConversionService(new DenisConversionService());
	}

	@Override
	protected FlowDefinitionResource[] getModelResources(FlowDefinitionResourceFactory resourceFactory) {
		FlowDefinitionResource[] superResources = super.getModelResources(resourceFactory);

		Resource localFlowResource = new FileSystemResource(new File(rootPath + "/Web/src/main/webapp/WEB-INF/flows/viewGoods/viewGoods-flow.xml"));

		ArrayList<FlowDefinitionResource> result = new ArrayList<FlowDefinitionResource>();

		result.addAll(Arrays.asList(superResources));

		result.add(new FlowDefinitionResource("viewGoods", localFlowResource, null));

		return result.toArray(new FlowDefinitionResource[0]);
	}

	@Test
	public void testViewAllFlowStart() {
		MockExternalContext context = new MockExternalContext();
		LocalAttributeMap input = new LocalAttributeMap("categoryId", -1);

		startFlow(input, context);

		assertCurrentStateEquals("allGoodsList");

		DataModel dataModel = (DataModel) getViewScope().getRequired("products", DataModel.class);
		ArrayList<Product> allProducts = (ArrayList<Product>) dataModel.getWrappedData();
		assertFalse("all products can't be empty (I swear)", allProducts.isEmpty());
	}

	@Test
	public void testParticularCategoryFlowStart() {
		Category c = testCategoryDao.findAll().get(0);
		MockExternalContext context = new MockExternalContext();
		LocalAttributeMap input = new LocalAttributeMap("categoryId", c.getId());

		startFlow(input, context);

		assertCurrentStateEquals("goodsListForCategory");

		DataModel dataModel = (DataModel) getViewScope().getRequired("products", DataModel.class);
		ArrayList<Product> allProducts = (ArrayList<Product>) dataModel.getWrappedData();

		assertEquals("Wrong products fetched", utils.asList(c.getProducts()), allProducts);
	}

	@Test
	public void testSelectGood() {
		LocalAttributeMap input = new LocalAttributeMap("categoryId", -1);
		MockExternalContext context = new MockExternalContext();

		startFlow(input, context);

		OneSelectionTrackingListDataModel dm = (OneSelectionTrackingListDataModel)
				getViewScope().getRequired("products", OneSelectionTrackingListDataModel.class);

		ArrayList<Product> products = (ArrayList<Product>) dm.getWrappedData();
		Product p = products.get(0);

		dm.select(p);

		context.setEventId("selectGood");
		resumeFlow(context);

		assertFlowExecutionEnded();
		assertFlowExecutionOutcomeEquals("goodFlow");
	}

	@Test
	public void testEditSubflow() {
		final Long prodId = new Long(1L);
		OneSelectionTrackingListDataModel dataModel = FlowTestUtils.getProductDataModel(prodId);

		setCurrentState("allGoodsList");

		MockExternalContext context = new MockExternalContext();

		resumeFlow(context);
		getFlowScope().put("products", dataModel);

		context.setEventId("edit");

		Flow editProductSubflow = FlowTestUtils.getMockEditProductSubflow(prodId);
		
		getFlowDefinitionRegistry().registerFlowDefinition(editProductSubflow);

		getFlowScope().put("categoryId", -1L);
		
		resumeFlow(context);

		assertFlowExecutionActive();
		assertCurrentStateEquals("allGoodsList");
	}

	@Test
	public void testEditImagesSubflow() {
		final Long prodId = new Long(1L);
		OneSelectionTrackingListDataModel dataModel = FlowTestUtils.getProductDataModel(prodId);

		setCurrentState("allGoodsList");

		MockExternalContext context = new MockExternalContext();

		resumeFlow(context);
		getFlowScope().put("products", dataModel);

		context.setEventId("editImages");

		Flow imagesManagerSubflow = FlowTestUtils.createMockImagesManagerFlow(prodId);

		getFlowDefinitionRegistry().registerFlowDefinition(imagesManagerSubflow);

		getFlowScope().put("categoryId", -1L);

		resumeFlow(context);

		assertFlowExecutionActive();
		assertCurrentStateEquals("allGoodsList");

	}

	@Test
	public void testDeleteGood() throws IOException {
		LocalAttributeMap input = new LocalAttributeMap("categoryId", -1);
		MockExternalContext context = new MockExternalContext();

		startFlow(input, context);


		OneSelectionTrackingListDataModel dm = (OneSelectionTrackingListDataModel)
				getViewScope().getRequired("products", OneSelectionTrackingListDataModel.class);

		ArrayList<Product> products = (ArrayList<Product>) dm.getWrappedData();
		Product p = products.get(0);

		Set<Image> images = p.getImages();
		ArrayList<Image> imagesList = new ArrayList<Image>(images);
		FileBackupRestoreManager[] restoreManagers =
				new FileBackupRestoreManager[imagesList.size()];
		for (int i = 0; i<imagesList.size(); i++) {
			restoreManagers[i] = new FileBackupRestoreManager(rootPath + imagesList.get(i).getUrl());
			restoreManagers[i].backup();
		}

		dm.select(p);

		context.setEventId("delete");
		try {
			resumeFlow(context);

			Product loaded = testProductDao.findById(p.getId());
			assertNull("Product wasn't properly deleted: " + p, loaded);

			assertFlowExecutionActive();
			assertCurrentStateEquals("allGoodsList");
		} finally {
			for (FileBackupRestoreManager m : restoreManagers) {
				m.restore();
			}
		}
	}

	@Test
	public void testBuyGood() {
		LocalAttributeMap input = new LocalAttributeMap("categoryId", -1);
		MockExternalContext context = new MockExternalContext();

		startFlow(input, context);


		OneSelectionTrackingListDataModel dm = (OneSelectionTrackingListDataModel)
				getViewScope().getRequired("products", OneSelectionTrackingListDataModel.class);

		ArrayList<Product> products = (ArrayList<Product>) dm.getWrappedData();
		Product p = products.get(0);

		dm.select(p);

		context.setEventId("buy");

		System.out.println("Before crash cart items: " + cart.getItems());
		assertTrue("Cart should be empty", cart.getItems().isEmpty());

		resumeFlow(context);

		PriceAware fromCart = cart.getItems().iterator().next().getItem();
		assertEquals("Wrong item was added to the cart", p, fromCart);

		assertFlowExecutionActive();
		assertCurrentStateEquals("allGoodsList");
	}

	@Test
	public void testBack() {
		setCurrentState("goodsListForCategory");

		MockExternalContext context = new MockExternalContext();
		context.setEventId("back");

		resumeFlow(context);

		assertFlowExecutionEnded();
		assertFlowExecutionOutcomeEquals("back");
	}
}
