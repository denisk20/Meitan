package com.meitan.lubov;

import com.meitan.lubov.model.persistent.BuyingAct;
import com.meitan.lubov.services.dao.BuyingActDao;
import com.meitan.lubov.services.dao.Dao;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.PersistenceException;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;

/**
 * @author denis_k
 *         Date: 14.03.2010
 *         Time: 10:11:58
 */
public class BuyingActIntegrationTest extends GenericIntegrationTest<BuyingAct> {
    @Autowired
    private BuyingActDao buyingActDao;

    @Override
    protected void setUpBeanNames() {
        beanNames.add("ent_purchase1");
        beanNames.add("ent_purchase2");
        beanNames.add("ent_purchase3");
    }

    @Override
    protected Dao<BuyingAct, Long> getDAO() {
        return buyingActDao;
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
        buyingActDao.makePersistent(act);
    }

    @Test(expected = PersistenceException.class)
    public void testInsertNullClient() throws CloneNotSupportedException {
        BuyingAct act = (BuyingAct) beansFromXml.get(0).clone();
        act.setClient(null);
        buyingActDao.makePersistent(act);
    }
}
