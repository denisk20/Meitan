package com.meitan.lubov.flowtests;

import com.meitan.lubov.model.PriceAware;
import com.meitan.lubov.model.persistent.Product;
import com.meitan.lubov.services.commerce.ShoppingCart;
import com.meitan.lubov.services.commerce.ShoppingCartImpl;
import com.meitan.lubov.services.dao.ClientDao;
import com.meitan.lubov.services.util.CaptchaService;
import com.meitan.lubov.services.util.ReCaptchaService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.webflow.config.FlowDefinitionResource;
import org.springframework.webflow.config.FlowDefinitionResourceFactory;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowBuilderContext;

import java.util.ArrayList;

/**
 * @author denis_k
 *         Date: 21.06.2010
 *         Time: 11:37:08
 */
public class RegisterFlowTest extends AbstractFlowIntegrationTest{
	private CaptchaService captchaService = new ReCaptchaService();

	@Autowired
	private ClientDao testClientDao;

	@Override
	protected FlowDefinitionResource getResource(FlowDefinitionResourceFactory resourceFactory) {
		return resourceFactory.createFileResource(rootPath + "/Web/src/main/webapp/WEB-INF/flows/register/register-flow.xml");
	}

	@Override
	protected void configureFlowBuilderContext(MockFlowBuilderContext builderContext) {
		super.configureFlowBuilderContext(builderContext);
		builderContext.registerBean("captchaService", captchaService);
		builderContext.registerBean("clientDao", testClientDao);
	}

	@Test
	public void testFlowStart() {
		MockExternalContext context = new MockExternalContext();
		startFlow(context);

		assertCurrentStateEquals("register");
	}


}