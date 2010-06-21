package com.meitan.lubov.flowtests;

import com.meitan.lubov.model.persistent.Product;
import com.meitan.lubov.services.dao.ProductDao;
import com.meitan.lubov.services.util.DenisConversionService;
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
		builderContext.registerBean("productDao", testProductDao);
		builderContext.getFlowBuilderServices().setConversionService(new DenisConversionService());
	}

	@Test
	public void testFlowStart() {
		Product p = testProductDao.findAll().get(0);
		LocalAttributeMap input = new LocalAttributeMap();
		input.put("goodId", p.getId());
		MockExternalContext context = new MockExternalContext();
		startFlow(input, context);

		assertCurrentStateEquals("good");
		
		Product loaded = (Product) getFlowScope().getRequired("product", Product.class);
		assertEquals("Wrong product loaded", p, loaded);
	}
}
