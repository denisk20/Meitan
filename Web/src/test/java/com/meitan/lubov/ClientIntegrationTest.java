package com.meitan.lubov;

import com.meitan.lubov.model.persistent.Client;
import com.meitan.lubov.services.dao.ClientDao;
import com.meitan.lubov.services.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.assertThat;
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

	@Override
	protected void setUpBeanNames() {
		beanNames.add("ent_creamLover");
	}

	@Override
	protected Dao<Client, Long> getDAO() {
		return clientDao;
	}

	@Override
	protected void compareAdditionalProperties(Client beanFromSpring, Client beanFromDB) {
		assertThat(beanFromSpring.getPurchases(), is(beanFromDB.getPurchases()));
	}
}
