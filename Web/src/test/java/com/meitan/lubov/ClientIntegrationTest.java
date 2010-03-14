package com.meitan.lubov;

import com.meitan.lubov.model.Name;
import com.meitan.lubov.model.persistent.Client;
import com.meitan.lubov.services.dao.ClientDao;
import com.meitan.lubov.services.dao.Dao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
	@Autowired
	ClientDao clientDao;

    private int expectedClientCount = 2;
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
	}

    @Test
    public void testCreateClient() {
        Client newClient = new Client();
        Name name = new Name();
        name.setFirstName("ab");
        name.setPatronymic("xy");
        name.setSecondName("cd");

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
}
