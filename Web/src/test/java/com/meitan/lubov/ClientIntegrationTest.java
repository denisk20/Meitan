package com.meitan.lubov;

import com.meitan.lubov.model.components.Name;
import com.meitan.lubov.model.persistent.*;
import com.meitan.lubov.services.commerce.ShoppingCartImpl;
import com.meitan.lubov.services.dao.*;
import com.meitan.lubov.services.util.SecurityService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.webflow.test.MockRequestContext;

import javax.persistence.PersistenceException;
import java.util.*;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;

/**
 * Date: Mar 5, 2010
 * Time: 11:41:10 PM
 *
 * @author denisk
 */
public class ClientIntegrationTest extends GenericIntegrationTest<Client> {
	@Autowired
	private ClientDao testClientDao;
	@Autowired
	private ProductDao testProductDao;
	@Autowired
	private AuthorityDao testAuthorityDao;
	@Autowired
	private BuyingActDao testBuyingActDao;

	private int expectedClientCount = 2;

	@Override
	protected void setUpBeanNames() {
		beanNames.add("ent_creamLover");
		beanNames.add("ent_creamAdmier");
	}

	@Override
	protected Dao<Client, Long> getDAO() {
		return testClientDao;
	}

	@Override
	protected void compareAdditionalProperties(Client beanFromSpring, Client beanFromDB) {
		assertThat(beanFromSpring.getPurchases(), is(beanFromDB.getPurchases()));
		assertThat(beanFromSpring.getLogin(), is(beanFromDB.getLogin()));
		assertThat(beanFromSpring.getPassword(), is(beanFromDB.getPassword()));
		assertThat(beanFromSpring.isEnabled(), is(beanFromDB.isEnabled()));
	}

	@Test
	public void testCreateClient() {
		Client newClient = new Client();
		Name name = new Name();
		name.setFirstName("ab");
		name.setPatronymic("xy");
		name.setSecondName("cd");

		newClient.setEmail("a@b.com");
		newClient.setLogin("Login");
		newClient.setJoinDate(new Date());
		testClientDao.makePersistent(newClient);

		List<Client> clients = testClientDao.findAll();
		assertThat(clients.size(), is(expectedClientCount + 1));
	}

	@Test
	public void testUpdateClient() {
		Client client = beansFromDb.get(0);
		String oldEmail = client.getEmail();
		String newEmail = oldEmail + "_changed";

		client.setEmail(newEmail);

		Client reloaded = testClientDao.findById(client.getId());

		assertThat(newEmail, is(reloaded.getEmail()));
	}

	@Test
	//todo this should be extended
	public void testDeleteClient() {
		Client client = beansFromDb.get(0);
		Set<Authority> roles = client.getRoles();
		for (Authority a : roles) {
			testAuthorityDao.makeTransient(a);
		}
		client.getRoles().clear();
		client.getPurchases().clear();
		for (BuyingAct act : client.getPurchases()) {
			for (ShoppingCartItem i : act.getProducts()) {
				((Product) i.getItem()).getPurchases().remove(act);
			}
			testBuyingActDao.makeTransient(act);

		}
		testClientDao.makeTransient(client);

		testClientDao.flush();
		Client reloaded = testClientDao.findById(client.getId());
		assertNull(reloaded);
	}

	@Test(expected = PersistenceException.class)
	public void insertNullableEmail() {
		Client c = beansFromDb.get(0);
		c.setEmail(null);
		testClientDao.makePersistent(c);
		testClientDao.flush();
	}

	@Test(expected = PersistenceException.class)
	public void insertNonUniqueEmail() {
		Client c = beansFromDb.get(0);
		String nonUniqueEmail = c.getEmail();

		Client created = new Client(new Name("first", "second", "third"), nonUniqueEmail);
		testClientDao.makePersistent(created);
		testClientDao.flush();
	}

	@Test(expected = PersistenceException.class)
	public void insertNonUniqueLogin() {
		Client c = beansFromDb.get(0);
		String nonUniqueLogin = c.getLogin();
		String email = c.getEmail();

		Client created = new Client(new Name("first", "second", "third"), "prefix." + email + "suffix");
		created.setLogin(nonUniqueLogin);

		testClientDao.makePersistent(created);
		testClientDao.flush();
	}

	@Test(expected = PersistenceException.class)
	public void insertNullableLogin() {
		Client c = beansFromDb.get(0);
		c.setLogin(null);
		testClientDao.makePersistent(c);
		testClientDao.flush();
	}

	@Test
	public void testGetByLogin() {
		Client result = testClientDao.getByLogin(CLIENT_LOGIN);
		assertNotNull("Client was null", result);
		assertThat(result.getLogin(), is(CLIENT_LOGIN));
	}

	@Test
	public void testBuyGoods() {
		Client c = testClientDao.getByLogin(CLIENT_LOGIN);
		Set<BuyingAct> initialPurchases = new HashSet(c.getPurchases());
		int initialBoughtCount = initialPurchases.size();

		List<Product> products = testProductDao.findAll();
		assertTrue(products.size() >= 2);

		Product p1 = products.get(0);
		Product p2 = products.get(1);

		ShoppingCartImpl cart = new ShoppingCartImpl();
		cart.addItem(p1);
		cart.addItem(p2);

		testClientDao.buyGoods(cart, CLIENT_LOGIN);

		testClientDao.flush();
		c = testClientDao.findById(c.getId());
		Set<BuyingAct> finalPurchases = c.getPurchases();
		assertEquals(initialBoughtCount + 1, finalPurchases.size());

		finalPurchases.removeAll(initialPurchases);
		assertEquals(1, finalPurchases.size());

		BuyingAct act = finalPurchases.iterator().next();

		assertEquals(2, act.getProducts().size());

		//todo find out why this don't work
		//assertEquals(cart.getItems(), new ArrayList(act.getProducts()));
	}

	@Test
	public void testAddAuthority() {
		Set<Authority> roles = beansFromDb.get(0).getRoles();
		assertNotNull(roles);
		assertTrue(roles.size() > 0);

		Authority a = roles.iterator().next();

		Client c = a.getClient();

		final String role = "ROLE_PICACHU";
		Authority newAuth = new Authority(c, role);
		testAuthorityDao.makePersistent(newAuth);
		c.getRoles().add(newAuth);

		c = testClientDao.findById(c.getId());
		assertTrue(c.getRoles().contains(a));

		a = testAuthorityDao.findById(a.getId());
		assertTrue(a.getClient().equals(c));
	}

	@Test
	public void testDeleteAuthority() {
		Client c = beansFromDb.get(0);
		Authority a = c.getRoles().iterator().next();

		c.getRoles().remove(a);
		a.setClient(null);
		testAuthorityDao.flush();

		c = testClientDao.findById(c.getId());
		assertFalse(c.getRoles().contains(a));

		a = testAuthorityDao.findById(a.getId());
		assertNull(a.getClient());
	}

/*
	@Test(expected = PersistenceException.class)
	public void testNonUniquePassport() {
		Client c = beansFromDb.get(0);
		Passport p = c.getPassport();

		Client created = new Client(new Name("first", "second", "third"), "a@b.com", p, new Date());
		testClientDao.makePersistent(created);

		testClientDao.flush();
	}

	@Test(expected = PersistenceException.class)
	public void insertNullablePassport() {
		Client c = beansFromDb.get(0);
		Passport p = new Passport();

		Client created = new Client(new Name("first", "second", "third"), "a@b.com", p, new Date());
		testClientDao.makePersistent(created);

		testClientDao.flush();
	}

	@Test(expected = PersistenceException.class)
	public void insertNullablePassportSeries() {
		Client c = beansFromDb.get(0);
		Passport p = null;

		Client created = new Client(new Name("first", "second", "third"), "a@b.com", p, new Date());
		testClientDao.makePersistent(created);

		testClientDao.flush();
	}
*/

	@Test(expected = PersistenceException.class)
	public void insertNullableDate() {
		Client c = beansFromDb.get(0);
		c.setJoinDate(null);

		testClientDao.makePersistent(c);
		testClientDao.flush();
	}

	@Test
	public void testSaveOrFetchClientByEmail_newClient() throws IllegalAccessException {
		final String email = "non.existing@email";
		Client c = new Client(new Name(), email);
		c.setLogin(email);

		final Client created = testClientDao.saveOrFetchUnregisteredClientByEmail(new MockRequestContext(), c);
		assertSame(c, created);
		assertNotNull(c.getId());

		Client loaded = testClientDao.findById(c.getId());
		assertEquals(c, loaded);
	}

	@Test(expected = IllegalAccessException.class)
	public void testSaveOrFetchClientByEmail_existingClient() throws IllegalAccessException {
		Client c = beansFromDb.get(0);
		testClientDao.saveOrFetchUnregisteredClientByEmail(new MockRequestContext(), c);
	}

	@Test
	public void testFindByLoginOrCreateNew() {
		Client c = beansFromDb.get(0);
		UsernamePasswordAuthenticationToken principal = new UsernamePasswordAuthenticationToken(c.getLogin(), null);
		final Client loaded = testClientDao.findByLoginOrCreateNew(principal);

		assertEquals(c, loaded);


	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindByLoginOrCreateNew_nullLogin() {
		UsernamePasswordAuthenticationToken principal =
				new UsernamePasswordAuthenticationToken("is this login or what?", null);
		testClientDao.findByLoginOrCreateNew(principal);
	}

	@Test
	public void testFindByLoginOrCreateNew_createNew() {
		Client c = testClientDao.findByLoginOrCreateNew(null);
		assertNotNull(c);
		assertEquals(new Long(0), c.getId());
	}
}
