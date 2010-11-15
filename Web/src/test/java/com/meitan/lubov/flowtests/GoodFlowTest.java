package com.meitan.lubov.flowtests;

import com.meitan.lubov.model.persistent.Product;
import com.meitan.lubov.services.dao.ProductDao;
import com.meitan.lubov.services.util.DenisConversionService;
import com.meitan.lubov.services.util.Utils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.webflow.config.FlowDefinitionResource;
import org.springframework.webflow.config.FlowDefinitionResourceFactory;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowBuilderContext;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author denis_k
 *         Date: 21.06.2010
 *         Time: 11:37:08
 */
//todo how to test the fact that the flow is secured?
public class GoodFlowTest extends AbstractFlowIntegrationTest{
	@Autowired
	private ProductDao testProductDao;

	@Override
	protected FlowDefinitionResource getResource(FlowDefinitionResourceFactory resourceFactory) {
		return resourceFactory.createFileResource(rootPath + "/Web/src/main/webapp/WEB-INF/flows/good/good-flow.xml");
	}

	@Override
	protected void configureFlowBuilderContext(MockFlowBuilderContext builderContext) {
		super.configureFlowBuilderContext(builderContext);
		builderContext.registerBean("productDao", testProductDao);
		builderContext.registerBean("utils", new Utils());
		builderContext.getFlowBuilderServices().setConversionService(new DenisConversionService());
	}
	@Override
	protected FlowDefinitionResource[] getModelResources(FlowDefinitionResourceFactory resourceFactory) {
		FlowDefinitionResource[] superResources = super.getModelResources(resourceFactory);

		ArrayList<FlowDefinitionResource> result = new ArrayList<FlowDefinitionResource>();

		result.addAll(Arrays.asList(superResources));

//		Resource localFlowResource1 = new FileSystemResource(new File(rootPath + "/Web/src/main/webapp/WEB-INF/flows/viewGoods/viewGoods-flow.xml"));
		Resource localFlowResource2 = new FileSystemResource(new File(rootPath + "/Web/src/main/webapp/WEB-INF/flows/baseGood/baseGood-flow.xml"));
		Resource localFlowResource3 = new FileSystemResource(new File(rootPath + "/Web/src/main/webapp/WEB-INF/flows/abstractEditable/abstractEditable-flow.xml"));
		result.add(new FlowDefinitionResource("baseGood", localFlowResource2, null));
//		result.add(new FlowDefinitionResource("viewGoods", localFlowResource1, null));
		result.add(new FlowDefinitionResource("abstractEditable", localFlowResource3, null));

		return result.toArray(new FlowDefinitionResource[0]);
	}

	@Test
	public void testFlowStart() {
		Product p = testProductDao.findAll().get(0);
		LocalAttributeMap input = new LocalAttributeMap();
		input.put("id", p.getId());
		MockExternalContext context = new MockExternalContext();
		startFlow(input, context);

		assertCurrentStateEquals("good");
		
		Product loaded = (Product) getFlowScope().getRequired("product", Product.class);
		assertEquals("Wrong product loaded", p, loaded);
	}
}
