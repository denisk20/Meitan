package com.meitan.lubov.flowtests;

import com.meitan.lubov.model.persistent.Product;
import com.meitan.lubov.services.dao.BoardItemDao;
import com.meitan.lubov.services.dao.NewsBoardDao;
import com.meitan.lubov.services.dao.ProductDao;
import com.meitan.lubov.services.util.DenisConversionService;
import com.meitan.lubov.services.util.FileUploadHandler;
import com.meitan.lubov.services.util.Utils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.faces.model.OneSelectionTrackingListDataModel;
import org.springframework.webflow.config.FlowDefinitionResource;
import org.springframework.webflow.config.FlowDefinitionResourceFactory;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowBuilderContext;

import javax.faces.model.DataModel;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

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
	@Autowired
	private BoardItemDao boardItemDao;
	
	private FileUploadHandler fileUploadHandler = new FileUploadHandler();

	@Override
	protected FlowDefinitionResource getResource(FlowDefinitionResourceFactory resourceFactory) {
		return resourceFactory.createFileResource(rootPath + "/Web/src/main/webapp/WEB-INF/flows/about/about-flow.xml");
	}

	@Override
	protected FlowDefinitionResource[] getModelResources(FlowDefinitionResourceFactory resourceFactory) {
		FlowDefinitionResource[] superResources = super.getModelResources(resourceFactory);


		ArrayList<FlowDefinitionResource> result = new ArrayList<FlowDefinitionResource>();

		result.addAll(Arrays.asList(superResources));

		Resource localFlowResource1 = new FileSystemResource(new File(rootPath + "/Web/src/main/webapp/WEB-INF/flows/abstractBoard/abstractBoard-flow.xml"));
		Resource localFlowResource2 = new FileSystemResource(new File(rootPath + "/Web/src/main/webapp/WEB-INF/flows/baseGood/baseGood-flow.xml"));
		Resource localFlowResource3 = new FileSystemResource(new File(rootPath + "/Web/src/main/webapp/WEB-INF/flows/abstractEditable/abstractEditable-flow.xml"));
		result.add(new FlowDefinitionResource("abstractBoard", localFlowResource1, null));
		result.add(new FlowDefinitionResource("baseGood", localFlowResource2, null));
		result.add(new FlowDefinitionResource("abstractEditable", localFlowResource3, null));

		return result.toArray(new FlowDefinitionResource[0]);
	}

	@Override
	protected void configureFlowBuilderContext(MockFlowBuilderContext builderContext) {
		super.configureFlowBuilderContext(builderContext);
		builderContext.registerBean("productDao", testProductDao);
		builderContext.registerBean("newsBoardDao", newsBoardDao);
		builderContext.registerBean("fileUploadHandler", fileUploadHandler);
		builderContext.registerBean("boardItemDao", boardItemDao);
		builderContext.registerBean("utils", new Utils());
		builderContext.getFlowBuilderServices().setConversionService(new DenisConversionService());
	}

	@Test
	public void testStartAboutFlow() {
		MockExternalContext context = new MockExternalContext();
		startFlow(context);

		assertCurrentStateEquals("aboutUs");
		ArrayList<Product> newProducts = (ArrayList<Product>) getRequiredViewAttribute("newProducts");
		assertEquals("Wrong number of products", 1, newProducts.size());
	}

	@Test
	public void testNavigateToGood() {
		MockExternalContext context = new MockExternalContext();
		startFlow(context);
		setCurrentState("aboutUs");
		context.setEventId("select");
		Product p = new Product("some product");
		p.setId(1L);
		ArrayList<Product> list = new ArrayList<Product>();
		list.add(p);
		OneSelectionTrackingListDataModel trackingListDataModel = new OneSelectionTrackingListDataModel(list);
		trackingListDataModel.select(p);
		getFlowScope().put("newProducts", trackingListDataModel);

		resumeFlow(context);

		assertFlowExecutionEnded();
		assertFlowExecutionOutcomeEquals("selectFlow");
	}

}
