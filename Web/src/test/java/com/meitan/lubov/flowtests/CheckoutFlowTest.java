package com.meitan.lubov.flowtests;

import com.meitan.lubov.model.PriceAware;
import com.meitan.lubov.model.components.Name;
import com.meitan.lubov.model.persistent.Authority;
import com.meitan.lubov.model.persistent.BuyingAct;
import com.meitan.lubov.model.persistent.Client;
import com.meitan.lubov.model.persistent.Product;
import com.meitan.lubov.services.commerce.ShoppingCart;
import com.meitan.lubov.services.commerce.ShoppingCartImpl;
import com.meitan.lubov.services.dao.AuthorityDao;
import com.meitan.lubov.services.dao.BuyingActDao;
import com.meitan.lubov.services.dao.ClientDao;
import com.meitan.lubov.services.dao.ProductDao;
import com.meitan.lubov.services.dao.ShoppingCartItemDao;
import com.meitan.lubov.services.util.MailService;
import com.meitan.lubov.services.util.SecurityService;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.expression.ExpressionInvocationTargetException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.webflow.config.FlowDefinitionResource;
import org.springframework.webflow.config.FlowDefinitionResourceFactory;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.FlowExecutionExceptionHandler;
import org.springframework.webflow.engine.FlowExecutionExceptionHandlerSet;
import org.springframework.webflow.engine.State;
import org.springframework.webflow.execution.ActionExecutionException;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.FlowSession;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowBuilderContext;

import javax.mail.MessagingException;
import java.io.File;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author denis_k
 *         Date: 21.06.2010
 *         Time: 11:37:08
 */
//todo how to test the fact that the flow is secured?
public class CheckoutFlowTest extends AbstractFlowIntegrationTest {

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
	@Autowired
	private AuthorityDao testAuthorityDao;

	@Override
	protected FlowDefinitionResource getResource(FlowDefinitionResourceFactory resourceFactory) {
		return resourceFactory
				.createFileResource(rootPath + "/Web/src/main/webapp/WEB-INF/flows/checkout/checkout-flow.xml");
	}

	@Override
	protected FlowDefinitionResource[] getModelResources(FlowDefinitionResourceFactory resourceFactory) {
		FlowDefinitionResource[] superResources = super.getModelResources(resourceFactory);

		ArrayList<FlowDefinitionResource> result = new ArrayList<FlowDefinitionResource>();

		result.addAll(Arrays.asList(superResources));

		Resource localFlowResource1 = new FileSystemResource(new File(rootPath + "/Web/src/main/webapp/WEB-INF/flows/checkout/checkout-flow.xml"));
		result.add(new FlowDefinitionResource("checkout", localFlowResource1, null));

		return result.toArray(new FlowDefinitionResource[0]);
	}

	@Override
	protected void configureFlowBuilderContext(MockFlowBuilderContext builderContext) {
		super.configureFlowBuilderContext(builderContext);
		builderContext.registerBean("cart", cart);
		builderContext.registerBean("securityService", testSecurityService);
		builderContext.registerBean("mailService", mailService);
		builderContext.registerBean("clientDao", testClientDao);
	}

	@After
	public void after() {
		//make sure there are no user in session
		SecurityContextHolder.getContext().setAuthentication(null);
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

		assertCurrentStateEquals("quickRegistrationFirstTime");
	}

	@Test
	public void testDecisionStateUserInSession() {
		MockExternalContext context = new MockExternalContext();
		startFlow(context);

		context.setEventId("order");
		String email = "some@email.com";
		Client c = new Client(new Name(), email);
		c.setEmail(email);
		Authority a = new Authority(c, SecurityService.ROLE_CLIENT);
		c.getRoles().add(a);
		c.setLogin(email);
		context.setCurrentUser(c.getLogin());
		testClientDao.makePersistent(c);

		testSecurityService.authenticateUser(c);

		resumeFlow(context);

		assertCurrentStateEquals("order");
	}

	@Test
	public void testAnonymousReturns() {
		String email = "some@email.com";
		String reEnterdEmail = email;
		Boolean shouldCreateUser = Boolean.FALSE;

		anonymousBurglesAgain(email, reEnterdEmail, shouldCreateUser);
	}

	@Test
	public void testAnonymousReturns2() {
		String email = "some@email.com";
		String enterdEmail = email + "blah";

		anonymousBurglesAgain(email, enterdEmail, Boolean.TRUE);
	}

	@Test(expected = IllegalAccessException.class)
	public void testAnonymousReturns3_theCrime() throws Throwable {
		String email = "some@email.com";
		String adminEmail = "admin@meitan-kh.com";
		Client admin = createAdmin(adminEmail);
		//you thief!!!
		String enteredEmail = admin.getEmail();

		try {
			try {
				anonymousBurglesAgain(email, enteredEmail, Boolean.TRUE);
			} catch (ActionExecutionException e) {
				e.printStackTrace();
				throw e.getCause();
			}
		} catch (ExpressionInvocationTargetException e) {
			e.printStackTrace();
			throw e.getCause();
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAnoymousReturns4_theCrimeThroughAdjust() throws Throwable {
		String adminEmail = "admin@meitan-kh.com";
		Client admin = createAdmin(adminEmail);

		MockExternalContext context = new MockExternalContext();
		setCurrentState("quickRegistrationAdjustDetails");
		resumeFlow(context);
		Client c = createAndPersistClient("some@email.com", SecurityService.ROLE_UNREGISTERED);
		Client stub = new Client(null, adminEmail);
		stub.setId(c.getId());
		getFlowScope().put("anonymousClient", stub);
		context.setEventId("quickreg");
		getViewScope().put("viewName", "WTF view");
		try {
			try {
				resumeFlow(context);
			} catch (ActionExecutionException e) {
				e.printStackTrace();
				throw e.getCause();
			}
		} catch (ExpressionInvocationTargetException e) {
			e.printStackTrace();
			throw e.getCause();
		}
	}

	private Client createAdmin(String adminEmail) {
		Client admin = new Client(new Name("admin", "", ""), adminEmail);
		admin.setLogin("Ted");
		testClientDao.makePersistent(admin);
		testAuthorityDao.assignAuthority(admin, SecurityService.ROLE_ADMIN);
		return admin;
	}

	@Test
	public void testDecisionStateWrongUserInSession() {
		MockExternalContext context = new MockExternalContext();
		startFlow(context);

		context.setEventId("order");
		Client c = new Client(new Name(), "some@email.com");

		testSecurityService.addCurrentSessionAuthority(c, "ROLE_WRONG");

		resumeFlow(context);

		assertCurrentStateEquals("quickRegistrationFirstTime");
	}


	@Test
	public void testAnonymousUserWorkflow() {
		MockExternalContext context = new MockExternalContext();
		startFlow(context);

		context.setEventId("order");

		resumeFlow(context);

		assertCurrentStateEquals("quickRegistrationFirstTime");
		//not persistent yet
		Client anonymous = (Client) getFlowScope().get("anonymousClient", Client.class);
		assertNotNull(anonymous);
		assertEquals(new Long(0), anonymous.getId());

		final String email = "some@mail.add";
		anonymous.setEmail(email);

		List<Product> prods = testProductDao.findAll();
		for (Product p : prods) {
			cart.addItem(p);
		}

		assertNull(SecurityContextHolder.getContext().getAuthentication());

		context.setEventId("quickreg");
		resumeFlow(context);

		assertSame(getFlowScope().get("anonymousClient"), anonymous);
		
		//persisted
		assertTrue(anonymous.getId() != 0);

		assertCurrentStateEquals("order");

		Set<Authority> roles = anonymous.getRoles();
		assertEquals(1, roles.size());
		Authority role = roles.iterator().next();
		assertEquals(SecurityService.ROLE_UNREGISTERED, role.getRole());

		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		assertNotNull(authentication);
		final Collection<GrantedAuthority> auths = authentication.getAuthorities();
		assertNotNull(auths);
		assertEquals(1, auths.size());
		final GrantedAuthority authority = auths.iterator().next();
		assertEquals(SecurityService.ROLE_UNREGISTERED, authority.getAuthority());

		//set currentUser var
		Principal p = new UsernamePasswordAuthenticationToken(anonymous.getLogin(), null);
		context.setCurrentUser(p);

		context.setEventId("order");
		resumeFlow(context);

		assertCurrentStateEquals("success");
		assertTrue(cart.getItems().isEmpty());

		//check that user was NOT logged out
		Authentication authenticationAfterBuy = SecurityContextHolder.getContext().getAuthentication();
		assertNotNull(authenticationAfterBuy);
		assertEquals(authentication, authenticationAfterBuy);

		assertProductsWereBought(anonymous.getLogin(), prods);

		//we're back to checkout
		setCurrentState("checkout");
		context.setEventId("order");
		resumeFlow(context);

		//we can re-use previous user
		assertEquals(anonymous, getFlowScope().get("anonymousClient"));

		//assertThat auth in session is the same, that is, no re-auth happened
		assertSame(authentication, SecurityContextHolder.getContext().getAuthentication());

		assertCurrentStateEquals("changeQuickregDetails");

		//accept settings
		context.setEventId("change");
		resumeFlow(context);

		assertCurrentStateEquals("quickRegistrationAdjustDetails");

		//change email
		String newEmail = "another_new@email.com";
		Client stub = new Client(null, newEmail);
		stub.setId(anonymous.getId());
		stub.setRoles(anonymous.getRoles());
		getFlowScope().put("anonymousClient", stub);
		context.setEventId("quickreg");

		resumeFlow(context);
		//restore
		getFlowScope().put("anonymousClient", anonymous);

		assertCurrentStateEquals("order");
		testClientDao.flush();
		Client loaded = testClientDao.findById(anonymous.getId());
		//ok, it changed user in DB
		assertEquals(newEmail, loaded.getEmail());

		//step back
		setCurrentState("checkout");
		context.setEventId("order");
		//refresh current user
		context.setCurrentUser(anonymous.getLogin());
		resumeFlow(context);

		assertCurrentStateEquals("changeQuickregDetails");
		context.setEventId("itsOK");
		resumeFlow(context);

		assertCurrentStateEquals("order");

		Client anotherLoaded = testClientDao.findById(anonymous.getId());
		assertEquals(anonymous, anotherLoaded);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAdjustQuickRegDetails() throws Throwable {
		String email = "some@email.com";
		Client c = new Client(new Name(), email);
		c.setEmail(email);
		c.setLogin(email);

		testAuthorityDao.assignAuthority(c, SecurityService.ROLE_UNREGISTERED);
		testSecurityService.authenticateUser(c);

		MockExternalContext context = new MockExternalContext();
		startFlow(context);

		getFlowScope().put("anonymousClient", c);

		setCurrentState("quickRegistrationAdjustDetails");

		context.setEventId("quickreg");
		try {
			try {
				resumeFlow(context);
			} catch (Exception e) {
				e.printStackTrace();
				throw e.getCause();
			}
		} catch (ActionExecutionException e) {
			e.printStackTrace();
			throw e.getCause();
		}
	}

	@Test
	public void changeUnregisteredUserEmail() {
		String email = "some@email.com";
		Client c = new Client(new Name(), email);
		c.setEmail(email);
		c.setLogin(email);

		testClientDao.makePersistent(c);
		testAuthorityDao.assignAuthority(c, SecurityService.ROLE_UNREGISTERED);
		testSecurityService.authenticateUser(c);

		MockExternalContext context = new MockExternalContext();
		startFlow(context);

		Client stub = new Client();


		String newEmail = "new@email.com";
		stub.setEmail(newEmail);
		stub.setId(c.getId());

		getFlowScope().put("anonymousClient", stub);

		setCurrentState("quickRegistrationAdjustDetails");
		context.setEventId("quickreg");

		resumeFlow(context);

		assertEquals(newEmail, c.getLogin() );
		String login = SecurityContextHolder.getContext().getAuthentication().getName();
		assertEquals(newEmail, login);

		Client loaded = testClientDao.getByLogin(newEmail);

		assertEquals(newEmail, loaded.getLogin());
		assertEquals(newEmail, loaded.getEmail());

	}

	private Client createAndPersistClient(String email, String role) {
		Client c = new Client(new Name(), email);
		c.setEmail(email);
		c.setLogin(email);

		testClientDao.makePersistent(c);
		testAuthorityDao.assignAuthority(c, role);
		return c;
	}

	private void assertProductsWereBought(String login, List<Product> prods) {
		//check that goods were bought

		List<BuyingAct> boughts = testBuyingActDao.findForLogin(login);
		assertEquals(1, boughts.size());

		BuyingAct act = boughts.get(0);

		final HashSet<PriceAware> boughtProducts = act.getProductsSet();
		for (Product prod : prods) {
			assertTrue(boughtProducts.contains(prod));
		}
	}

	private void anonymousBurglesAgain(String email, String reEnterdEmail, Boolean shouldCreateUser) {
		MockExternalContext context = new MockExternalContext();
		startFlow(context);
		String stateId = "quickRegistrationFirstTime";

		//remove exceptions handlers
		removeExceptionHandlers(stateId);

		//checkout

		createAndPersistClient(email, SecurityService.ROLE_UNREGISTERED);
		//just persist, not authenticate
		context.setEventId("order");
		resumeFlow(context);

		assertCurrentStateEquals(stateId);

		Client stub = (Client) getFlowScope().get("anonymousClient");
		assertEquals(new Long(0), stub.getId());
		stub.setEmail(reEnterdEmail);

		context.setEventId("quickreg");
		resumeFlow(context);

		if (shouldCreateUser) {
			assertSame(stub, getFlowScope().get("anonymousClient"));
		} else {
			assertNotSame(stub, getFlowScope().get("anonymousClient"));
		}
	}

	private void removeExceptionHandlers(String stateId) {
		FlowExecution flowExecution1 = getFlowExecution();
		FlowSession flowSession = flowExecution1.getActiveSession();
		Flow flow = (Flow) flowSession.getDefinition();
		State state = flow
				.getStateInstance(stateId);
		FlowExecutionExceptionHandlerSet exceptionHandlerSet = state.getExceptionHandlerSet();
		FlowExecutionExceptionHandler[] executionExceptionHandlers = exceptionHandlerSet.toArray();
		for (FlowExecutionExceptionHandler eh : executionExceptionHandlers) {
			exceptionHandlerSet.remove(eh);
		}
	}

	private static class MockMailSerice implements MailService {
		@Override
		public void sendBuyingActNotification(ShoppingCart cart, Principal currentUser) throws MessagingException {
			//do nothing
		}
	}
}