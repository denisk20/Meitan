package com.meitan.lubov.services.dao.jpa.impl;

import com.meitan.lubov.model.persistent.BuyingAct;
import com.meitan.lubov.services.dao.BuyingActDao;
import com.meitan.lubov.services.dao.jpa.JpaDao;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

/**
 * Date: Mar 4, 2010
 * Time: 6:32:26 PM
 *
 * @author denisk
 */
@Service("buyingActDao")
@Repository
public class JpaBuyingActDao extends JpaDao<BuyingAct, Long> implements BuyingActDao {
}
