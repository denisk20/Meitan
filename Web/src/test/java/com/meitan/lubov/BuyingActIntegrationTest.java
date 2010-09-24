package com.meitan.lubov;

import com.meitan.lubov.model.persistent.BuyingAct;
import com.meitan.lubov.model.persistent.Client;
import com.meitan.lubov.model.persistent.ShoppingCartItem;
import com.meitan.lubov.services.dao.BuyingActDao;
import com.meitan.lubov.services.dao.ClientDao;
import com.meitan.lubov.services.dao.Dao;
import com.meitan.lubov.services.dao.ShoppingCartItemDao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author denis_k
 *         Date: 14.03.2010
 *         Time: 10:11:58
 */
public class BuyingActIntegrationTest extends GenericIntegrationTest<BuyingAct> {
    @Autowired
    private BuyingActDao testBuyingActDao;
	
	@Autowired
	private ShoppingCartItemDao testShoppingCartItemDao;

	@Autowired
	private ClientDao testClientDao;

    @Override
    protected void setUpBeanNames() {
        beanNames.add("ent_purchase1");
        beanNames.add("ent_purchase2");
        beanNames.add("ent_purchase3");
    }

    @Override
    protected Dao<BuyingAct, Long> getDAO() {
        return testBuyingActDao;
    }

    @Override
    protected void compareAdditionalProperties(BuyingAct beanFromSpring, BuyingAct beanFromDB) {
        assertThat(beanFromSpring.getClient(), is(beanFromDB.getClient()));
        assertThat(beanFromSpring.getProducts(), is(beanFromDB.getProducts()));
        assertThat(beanFromSpring.getTotalPrice(), is(beanFromDB.getTotalPrice()));
    }

    @Test(expected = PersistenceException.class)
    public void testInsertNullDate() throws CloneNotSupportedException {
        BuyingAct act = (BuyingAct) beansFromXml.get(0).clone();
        act.setDate(null);
        testBuyingActDao.makePersistent(act);
    }

    @Test(expected = PersistenceException.class)
    public void testInsertNullClient() throws CloneNotSupportedException {
        BuyingAct act = (BuyingAct) beansFromXml.get(0).clone();
        act.setClient(null);
        testBuyingActDao.makePersistent(act);
    }

	@Test
	public void testFindForCartItem() {
		ShoppingCartItem item = testShoppingCartItemDao.findAll().get(0);

		List<BuyingAct> acts = testBuyingActDao.findForCartItem(item.getId());

		assertEquals(1, acts.size());
	}

	@Test
	public void testFindForLogin() {
		Client c = testClientDao.findAll().get(0);
		String login = c.getLogin();
		//assume the client has some boughts
		final Set<BuyingAct> initialActs = c.getPurchases();
		final List<BuyingAct> foundActs = testBuyingActDao.findForLogin(login);

		assertEquals(new ArrayList(initialActs), foundActs);
	}
}
