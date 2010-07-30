package com.meitan.lubov.flowtests;

import com.meitan.lubov.model.persistent.Product;
import com.meitan.lubov.services.dao.NewsBoardDao;
import com.meitan.lubov.services.dao.ProductDao;
import com.meitan.lubov.services.util.DenisConversionService;
import com.meitan.lubov.services.util.FileUploadHandler;
import com.meitan.lubov.services.util.Utils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.faces.model.OneSelectionTrackingListDataModel;
import org.springframework.webflow.config.FlowDefinitionResource;
import org.springframework.webflow.config.FlowDefinitionResourceFactory;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowBuilderContext;

import javax.faces.model.DataModel;
import java.util.ArrayList;

/**
 * @author denis_k
 *         Date: 16.06.2010
 *         Time: 17:59:34
 */
public class AboutFlowTest extends AbstractFlowIntegrationTest {
	@Autowired
	private ProductDao testProductDao;

	//	todo make this test
	@Autowired
	private NewsBoardDao newsBoardDao;
	
	private FileUploadHandler fileUploadHandler = new FileUploadHandler();

	@Override
	protected FlowDefinitionResource getResource(FlowDefinitionResourceFactory resourceFactory) {
		return resourceFactory.createFileResource(rootPath + "/Web/src/main/webapp/WEB-INF/flows/about/about-flow.xml");
	}

	@Override
	protected void configureFlowBuilderContext(MockFlowBuilderContext builderContext) {
		builderContext.registerBean("productDao", testProductDao);
		builderContext.registerBean("newsBoardDao", newsBoardDao);
		builderContext.registerBean("fileUploadHandler", fileUploadHandler);
		builderContext.registerBean("utils", new Utils());
		builderContext.getFlowBuilderServices().setConversionService(new DenisConversionService());
	}

	@Test
	public void testStartAboutFlow() {
		MockExternalContext context = new MockExternalContext();
		startFlow(context);

		assertCurrentStateEquals("aboutUs");
		DataModel newProductsDataModel = (DataModel) getRequiredFlowAttribute("newProducts");
		ArrayList<Product> newProducts = (ArrayList<Product>) newProductsDataModel.getWrappedData();
		assertEquals("Wrong number of products", 1, newProducts.size());
	}

	@Test
	public void testNavigateToGood() {
		setCurrentState("aboutUs");
		MockExternalContext context = new MockExternalContext();
		context.setEventId("selectGood");
		getFlowScope().put("newProducts", new OneSelectionTrackingListDataModel());

		resumeFlow(context);

		assertFlowExecutionEnded();
		assertFlowExecutionOutcomeEquals("goodFlow");
	}

}
