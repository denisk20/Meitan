package com.meitan.lubov;

import com.meitan.lubov.model.components.Name;
import com.meitan.lubov.model.persistent.Client;
import com.meitan.lubov.services.dao.ClientDao;
import com.meitan.lubov.services.dao.Dao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.PersistenceException;
import java.util.List;

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
	ClientDao clientDao;

    private int expectedClientCount = 2;
	private static final String CLIENT_LOGIN = "client";

	@Override
	protected void setUpBeanNames() {
		beanNames.add("ent_creamLover");
		beanNames.add("ent_creamAdmier");
	}

	@Override
	protected Dao<Client, Long> getDAO() {
		return clientDao;
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
        clientDao.makePersistent(newClient);

        List<Client> clients = clientDao.findAll();
        assertThat(clients.size(), is(expectedClientCount + 1));
    }

    @Test
    public void testUpdateClient() {
        Client client = beansFromDb.get(0);
        String oldEmail = client.getEmail();
        String newEmail = oldEmail + "_changed";

        client.setEmail(newEmail);

        Client reloaded = clientDao.findById(client.getId());

        assertThat(newEmail, is(reloaded.getEmail()));
    }

    @Test
    public void testDeleteClient() {
        Client client = beansFromDb.get(0);
        clientDao.makeTransient(client);

        Client reloaded = clientDao.findById(client.getId());
        assertNull(reloaded);
    }

	@Test(expected = PersistenceException.class)
	public void insertNullableEmail() {
		Client c = beansFromDb.get(0);
		c.setEmail(null);
		clientDao.makePersistent(c);
		clientDao.flush();
	}

	@Test(expected = PersistenceException.class)
	public void insertNonUniqueEmail() {
		Client c = beansFromDb.get(0);
		String nonUniqueEmail = c.getEmail();

		Client created = new Client(new Name("first", "second", "third"), nonUniqueEmail);
		clientDao.makePersistent(created);
		clientDao.flush();
	}

	@Test(expected = PersistenceException.class)
	public void insertNonUniqueLogin() {
		Client c = beansFromDb.get(0);
		String nonUniqueLogin = c.getLogin();
		String email = c.getEmail();

		Client created = new Client(new Name("first", "second", "third"), "prefix." + email + "suffix");
		created.setLogin(nonUniqueLogin);

		clientDao.makePersistent(created);
		clientDao.flush();
	}

	@Test(expected = PersistenceException.class)
	public void insertNullableLogin() {
		Client c = beansFromDb.get(0);
		c.setLogin(null);
		clientDao.makePersistent(c);
		clientDao.flush();
	}

	@Test
	public void testGetByLogin() {
		Client result = clientDao.getByLogin(CLIENT_LOGIN);
		assertNotNull("Client was null", result);
		assertThat(result.getLogin(), is(CLIENT_LOGIN));
	}
}
