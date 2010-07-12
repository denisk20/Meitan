package com.meitan.lubov.flowtests;

import com.meitan.lubov.model.PriceAware;
import com.meitan.lubov.model.persistent.Product;
import com.meitan.lubov.services.commerce.ShoppingCart;
import com.meitan.lubov.services.commerce.ShoppingCartImpl;
import org.junit.Test;
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
//todo how to test the fact that the flow is secured?
public class CheckoutFlowTest extends AbstractFlowIntegrationTest{

	private ShoppingCart cart = new ShoppingCartImpl();

	@Override
	protected FlowDefinitionResource getResource(FlowDefinitionResourceFactory resourceFactory) {
		return resourceFactory.createFileResource(rootPath + "/Web/src/main/webapp/WEB-INF/flows/checkout/checkout-flow.xml");
	}

	@Override
	protected void configureFlowBuilderContext(MockFlowBuilderContext builderContext) {
		builderContext.registerBean("cart", cart);
		//builderContext.getFlowBuilderServices().setConversionService(new DenisConversionService());
	}

	@Test
	public void testFlowStart() {
		MockExternalContext context = new MockExternalContext();
		startFlow(context);

		assertCurrentStateEquals("checkout");
	}

	@Test
	public void testDelete() {
		Product p1 = new Product("p1");
		Product p2 = new Product("p2");

		cart.addItem(p1);
		cart.addItem(p2);

		MockExternalContext context = new MockExternalContext();
		startFlow(context);

		//assuming p1 corresponds to the first item
		cart.getItemsDataModel().select(cart.getItems().get(0));

		context.setEventId("delete");
		resumeFlow(context);

		ArrayList<PriceAware> cartItems = cart.getPriceAwares();
		assertFalse("Item wasn't deleted properly", cartItems.contains(p1));
		assertEquals(1, cartItems.size());
	}
}