package com.meitan.lubov;

import com.meitan.lubov.model.components.Name;
import com.meitan.lubov.model.persistent.BuyingAct;
import com.meitan.lubov.model.persistent.Client;
import com.meitan.lubov.model.persistent.Product;
import com.meitan.lubov.services.commerce.ShoppingCartImpl;
import com.meitan.lubov.services.dao.ClientDao;
import com.meitan.lubov.services.dao.Dao;
import com.meitan.lubov.services.dao.ProductDao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
public class ClientIntegrationTest extends GenericIntegrationTest<Client>{
	//todo make testClientDao
	@Autowired
	private ClientDao testClientDao;
	@Autowired
	private ProductDao testProductDao;

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
    public void testDeleteClient() {
        Client client = beansFromDb.get(0);
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
}
