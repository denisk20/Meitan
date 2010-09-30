package com.meitan.lubov.flowtests;

import com.meitan.lubov.model.PriceAware;
import com.meitan.lubov.model.components.Name;
import com.meitan.lubov.model.persistent.Authority;
import com.meitan.lubov.model.persistent.Client;
import com.meitan.lubov.services.dao.AuthorityDao;
import com.meitan.lubov.services.dao.ClientDao;
import com.meitan.lubov.services.util.CaptchaService;
import com.meitan.lubov.services.util.PasswordValidator;
import com.meitan.lubov.services.util.ReCaptchaService;
import com.meitan.lubov.services.util.SecurityService;
import com.meitan.lubov.services.util.Utils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.webflow.config.FlowDefinitionResource;
import org.springframework.webflow.config.FlowDefinitionResourceFactory;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowBuilderContext;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author denis_k
 *         Date: 21.06.2010
 *         Time: 11:37:08
 */
public class RegisterFlowTest extends AbstractFlowIntegrationTest{
	private CaptchaService captchaService = new ReCaptchaService() {
		@Override
		public void validateCaptcha(RequestContext requestContext) {
			//do nothing
		}
	};

	private PasswordValidator passwordValidator = new PasswordValidator();
	private Utils utils = new Utils();
	@Autowired
	private AuthorityDao testAuthorityDao;
	@Autowired
	private ClientDao testClientDao;
	@Autowired
	private SecurityService testSecurityService;

	@Override
	protected FlowDefinitionResource getResource(FlowDefinitionResourceFactory resourceFactory) {
		return resourceFactory.createFileResource(rootPath + "/Web/src/main/webapp/WEB-INF/flows/register/register-flow.xml");
	}

	@Override
	protected void configureFlowBuilderContext(MockFlowBuilderContext builderContext) {
		super.configureFlowBuilderContext(builderContext);
		builderContext.registerBean("captchaService", captchaService);
		builderContext.registerBean("clientDao", testClientDao);
		builderContext.registerBean("authorityDao", testAuthorityDao);
		builderContext.registerBean("securityService", testSecurityService);
		builderContext.registerBean("utils", utils);
		builderContext.registerBean("passwordValidator", passwordValidator);
	}

	@Ignore("Exception is thrown, but never bubbles up...")
	@Test(expected = IllegalArgumentException.class)
	public void testDifferentPasswords() {
		MockExternalContext context = new MockExternalContext();
		startFlow(context);

		assertCurrentStateEquals("register");
		Client newClient = (Client) getFlowAttribute("newClient");
		String pass = "abc";
		newClient.setPassword(pass);
		newClient.setConformedPassword(pass + "oops");

		context.setEventId("register");
		resumeFlow(context);
	}

	@Test
	public void testRegister() {
		List<Client> initialClients = testClientDao.findAll();
		List<Authority> initialAuthorities = testAuthorityDao.findAll();

		MockExternalContext context = new MockExternalContext();
		startFlow(context);

		assertCurrentStateEquals("register");
		Client newClient = (Client) getFlowAttribute("newClient");
		assertNotNull(newClient);
		String pass = "abc";
		newClient.setPassword(pass);
		newClient.setConformedPassword(pass);
		newClient.setName(new Name("first", "patro", "second"));
		newClient.setEmail("a@b.com");
		String login = "login";
		newClient.setLogin(login);

		context.setEventId("register");
		resumeFlow(context);

		String md5 = utils.getMD5(pass);
		assertEquals(md5, newClient.getPassword());
		List<Client> modifiedClients = testClientDao.findAll();
		List<Authority> modifiedAuthorities = testAuthorityDao.findAll();
		assertEquals(initialClients.size() + 1, modifiedClients.size());
		assertEquals(initialAuthorities.size() + 1, modifiedAuthorities.size());

		modifiedClients.removeAll(initialClients);
		Client loadedClient = modifiedClients.get(0);
		assertEquals(newClient, loadedClient);
		Set<Authority> roles = loadedClient.getRoles();
		assertEquals(1, roles.size());
		Authority auth = roles.iterator().next();
		String roleClient = "ROLE_CLIENT";
		assertEquals(roleClient, auth.getRole());

		modifiedAuthorities.removeAll(initialAuthorities);
		assertEquals(auth, modifiedAuthorities.get(0));

		Authentication authentication =
				SecurityContextHolder.getContext().getAuthentication();
		assertEquals(login, authentication.getPrincipal());
		assertEquals(md5, authentication.getCredentials());
		Collection<GrantedAuthority> grantedAuthorityCollection = authentication.getAuthorities();
		assertEquals(1, grantedAuthorityCollection.size());
		GrantedAuthority grantedAuthority = grantedAuthorityCollection.iterator().next();
		assertEquals(roleClient, grantedAuthority.getAuthority());
	}
}