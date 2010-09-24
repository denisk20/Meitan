package com.meitan.lubov.flowtests;

import com.meitan.lubov.model.PriceAware;
import com.meitan.lubov.model.components.Name;
import com.meitan.lubov.model.persistent.BuyingAct;
import com.meitan.lubov.model.persistent.Client;
import com.meitan.lubov.model.persistent.Product;
import com.meitan.lubov.model.persistent.ShoppingCartItem;
import com.meitan.lubov.services.commerce.ShoppingCart;
import com.meitan.lubov.services.commerce.ShoppingCartImpl;
import com.meitan.lubov.services.dao.BuyingActDao;
import com.meitan.lubov.services.dao.ClientDao;
import com.meitan.lubov.services.dao.ProductDao;
import com.meitan.lubov.services.dao.ShoppingCartItemDao;
import com.meitan.lubov.services.util.MailService;
import com.meitan.lubov.services.util.SecurityService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.webflow.config.FlowDefinitionResource;
import org.springframework.webflow.config.FlowDefinitionResourceFactory;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowBuilderContext;

import javax.mail.MessagingException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * @author denis_k
 *         Date: 21.06.2010
 *         Time: 11:37:08
 */
//todo how to test the fact that the flow is secured?
public class CheckoutFlowTest extends AbstractFlowIntegrationTest{

	private ShoppingCart cart = new ShoppingCartImpl();
	private MailService mailService = new MockMailSerice();

	@Autowired
	private SecurityService testSecurityService;

	@Autowired
	private ProductDao testProductDao;
	@Autowired
	private ClientDao testClientDao;
	@Autowired
	private ShoppingCartItemDao testShoppingCartItemDao;
	@Autowired
	private BuyingActDao testBuyingActDao;

	@Override
	protected FlowDefinitionResource getResource(FlowDefinitionResourceFactory resourceFactory) {
		return resourceFactory.createFileResource(rootPath + "/Web/src/main/webapp/WEB-INF/flows/checkout/checkout-flow.xml");
	}

	@Override
	protected void configureFlowBuilderContext(MockFlowBuilderContext builderContext) {
		super.configureFlowBuilderContext(builderContext);
		builderContext.registerBean("cart", cart);
		builderContext.registerBean("securityService", testSecurityService);
		builderContext.registerBean("mailService", mailService);
		builderContext.registerBean("clientDao", testClientDao);
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

	@Test
	public void testDecisionStateNoUser() {
		MockExternalContext context = new MockExternalContext();
		startFlow(context);

		context.setEventId("order");
		//no user associated
		resumeFlow(context);

		assertCurrentStateEquals("quickRegistration");
	}

	@Test
	public void testDecisionStateUserInSession() {
		MockExternalContext context = new MockExternalContext();
		startFlow(context);

		context.setEventId("order");
		Client c = new Client(new Name(), "some@email.com");

		testSecurityService.authenticateUser(c);

		resumeFlow(context);

		assertCurrentStateEquals("order");
	}

	@Test
	public void testDecisionStateWrongUserInSession() {
		MockExternalContext context = new MockExternalContext();
		startFlow(context);

		context.setEventId("order");
		Client c = new Client(new Name(), "some@email.com");

		testSecurityService.addCurrentSessionAuthority(c, "ROLE_WRONG");

		resumeFlow(context);

		assertCurrentStateEquals("quickRegistration");
	}

	@Test
	public void testCreateAnonymousUser() {
		MockExternalContext context = new MockExternalContext();
		startFlow(context);

		context.setEventId("order");

		resumeFlow(context);

		assertCurrentStateEquals("quickRegistration");

		//not persistent yet
		Client anonymous = (Client) getViewScope().get("anonymousClient", Client.class);
		assertNotNull(anonymous);

		final String email = "some@mail.add";
		anonymous.setEmail(email);
		anonymous.setLogin(email);

		List<Product> prods = testProductDao.findAll();
		for (Product p : prods) {
			cart.addItem(p);
		}

		context.setEventId("quickreg");
		resumeFlow(context);

		assertCurrentStateEquals("order");

		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		assertNotNull(authentication);
		final Collection<GrantedAuthority> auths = authentication.getAuthorities();
		assertNotNull(auths);
		assertEquals(1, auths.size());
		final GrantedAuthority authority = auths.iterator().next();
		assertEquals(SecurityService.ROLE_ANONYMOUS, authority.getAuthority());

		//set currentUser var
		Principal p = new UsernamePasswordAuthenticationToken(anonymous.getLogin(), null);

		context.setEventId("order");
		context.setCurrentUser(p);
		resumeFlow(context);
		
		assertTrue(cart.getItems().isEmpty());

		//check that user was logged out
		assertNull(SecurityContextHolder.getContext().getAuthentication());

		//check that goods were bought

		List<BuyingAct> boughts = testBuyingActDao.findForLogin(anonymous.getLogin());
		assertEquals(1, boughts.size());

		BuyingAct act = boughts.get(0);

		final HashSet<PriceAware> boughtProducts = act.getProductsSet();
		for (Product prod : prods) {
			assertTrue(boughtProducts.contains(prod));
		}

	}

	private static class MockMailSerice implements MailService {
		@Override
		public void sendBuyingActNotification(ShoppingCart cart, Principal currentUser) throws MessagingException {
			//do nothing
		}
	}
}