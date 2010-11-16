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
import com.meitan.lubov.services.util.FileUploadHandler;
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
	@Autowired
	private FileUploadHandler fileUploadHandler;

	private ShoppingCart cart = new ShoppingCartImpl();
	private Utils utils = new Utils();

	@Override
	protected FlowDefinitionResource getResource(FlowDefinitionResourceFactory resourceFactory) {
		return resourceFactory.createFileResource(rootPath + "/Web/src/main/webapp/WEB-INF/flows/viewGoods/viewGoods-flow.xml");
	}

	@Override
	protected void configureFlowBuilderContext(MockFlowBuilderContext builderContext) {
		super.configureFlowBuilderContext(builderContext);
		builderContext.registerBean("productDao", testProductDao);
		builderContext.registerBean("cart", cart);
		builderContext.getFlowBuilderServices().setConversionService(new DenisConversionService());
	}

	@Override
	protected FlowDefinitionResource[] getModelResources(FlowDefinitionResourceFactory resourceFactory) {
		FlowDefinitionResource[] superResources = super.getModelResources(resourceFactory);

		ArrayList<FlowDefinitionResource> result = new ArrayList<FlowDefinitionResource>();

		result.addAll(Arrays.asList(superResources));

		Resource localFlowResource1 = new FileSystemResource(new File(rootPath + "/Web/src/main/webapp/WEB-INF/flows/viewGoods/viewGoods-flow.xml"));
		Resource localFlowResource2 = new FileSystemResource(new File(rootPath + "/Web/src/main/webapp/WEB-INF/flows/baseGood/baseGood-flow.xml"));
		Resource localFlowResource3 = new FileSystemResource(new File(rootPath + "/Web/src/main/webapp/WEB-INF/flows/abstractEditable/abstractEditable-flow.xml"));
		result.add(new FlowDefinitionResource("baseGood", localFlowResource2, null));
		result.add(new FlowDefinitionResource("viewGoods", localFlowResource1, null));
		result.add(new FlowDefinitionResource("abstractEditable", localFlowResource3, null));

		return result.toArray(new FlowDefinitionResource[0]);
	}

	@Test
	public void testViewAllFlowStart() {
		MockExternalContext context = new MockExternalContext();
		LocalAttributeMap input = new LocalAttributeMap("id", null);

		startFlow(input, context);

		assertCurrentStateEquals("allGoodsList");

		ArrayList<Product> allProducts = (ArrayList<Product>) getViewScope().getRequired("products");
		assertFalse("all products can't be empty (I swear)", allProducts.isEmpty());
	}

	@Test
	public void testParticularCategoryFlowStart() {
		Category c = testCategoryDao.findAll().get(0);
		MockExternalContext context = new MockExternalContext();
		LocalAttributeMap input = new LocalAttributeMap("id", c.getId());

		startFlow(input, context);

		assertCurrentStateEquals("goodsListForCategory");

		ArrayList<Product> allProducts = (ArrayList<Product>) getViewScope().getRequired("products");

		assertEquals("Wrong products fetched", utils.asList(c.getProducts()), allProducts);
	}

	@Test
	public void testSelectGood() {
		LocalAttributeMap input = new LocalAttributeMap("abstractEditable", null);
		MockExternalContext context = new MockExternalContext();

		startFlow(input, context);

		context.setEventId("select");
		resumeFlow(context);

		assertFlowExecutionEnded();
		assertFlowExecutionOutcomeEquals("selectFlow");
	}

	@Test
	public void testEditSubflow() {
		final Long prodId = new Long(1L);
//		OneSelectionTrackingListDataModel dataModel = FlowTestUtils.getProductDataModel(prodId);
		MockExternalContext context = new MockExternalContext();
		startFlow(context);

		setCurrentState("allGoodsList");


		resumeFlow(context);

		MockParameterMap map = new MockParameterMap();
		map.put("id", prodId.toString());

		context.setRequestParameterMap(map);

		context.setEventId("edit");
		
		Flow editProductSubflow = FlowTestUtils.getMockEditProductSubflow(prodId);
		
		getFlowDefinitionRegistry().registerFlowDefinition(editProductSubflow);

		getFlowScope().put("id", null);
		
		resumeFlow(context);

		assertFlowExecutionActive();
		assertCurrentStateEquals("goodsListForCategory");
	}

	@Test
	public void testEditImagesSubflow() {
		MockExternalContext context = new MockExternalContext();
		startFlow(context);


		assertCurrentStateEquals("allGoodsList");

		MockParameterMap map = new MockParameterMap();
		final Long prodId = new Long(1L);
		//todo this actually sets IDs of both input and request parameters.
		// I can't figure out how to reset input after this because
		// I don't have access to the source of org.springframework.binding.mapping.Mapper
		map.put("id", prodId.toString());

		context.setRequestParameterMap(map);

		Flow imagesManagerSubflow = FlowTestUtils.createMockImagesManagerFlow(prodId, Product.class.getName());

		getFlowDefinitionRegistry().registerFlowDefinition(imagesManagerSubflow);

		context.setEventId("editImages");

		resumeFlow(context);

		assertFlowExecutionActive();

		assertCurrentStateEquals("goodsListForCategory");

	}

	@Test
	public void testDeleteGood() throws IOException {
		LocalAttributeMap input = new LocalAttributeMap("id", null);
		MockExternalContext context = new MockExternalContext();

		startFlow(input, context);


		ArrayList<Product> products = (ArrayList<Product>) getViewScope().getRequired("products");
		Product p = products.get(0);

		Set<Image> images = p.getImages();
		ArrayList<Image> imagesList = new ArrayList<Image>(images);
		FileBackupRestoreManager[] restoreManagers =
				new FileBackupRestoreManager[imagesList.size()];
		for (int i = 0; i<imagesList.size(); i++) {
			restoreManagers[i] = new FileBackupRestoreManager(utils.getImageUploadDirectoryPath() + imagesList.get(i).getUrl());
			restoreManagers[i].backup();
		}

		MockParameterMap map = new MockParameterMap();
		map.put("id", p.getId().toString());
		context.setRequestParameterMap(map);

		context.setEventId("delete");
		try {
			resumeFlow(context);

			assertFlowExecutionActive();
			assertCurrentStateEquals("conformDelete");

			context.setEventId("ok");
			resumeFlow(context);
			
			Product loaded = testProductDao.findById(p.getId());
			testProductDao.flush();
			assertNull("Product wasn't properly deleted: " + p, loaded);

//			assertFlowExecutionActive();
//			assertCurrentStateEquals("allGoodsList");
		} finally {
			for (FileBackupRestoreManager m : restoreManagers) {
				m.restore();
			}
		}
	}

	@Test
	public void testBuyGood() {
		LocalAttributeMap input = new LocalAttributeMap("id", null);
		MockExternalContext context = new MockExternalContext();

		startFlow(input, context);



		ArrayList<Product> products = (ArrayList<Product>) getViewScope().getRequired("products");
		Product p = products.get(0);

		MockParameterMap map = new MockParameterMap();
		map.put("productId", p.getId().toString());
		context.setRequestParameterMap(map);

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
