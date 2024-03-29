package com.meitan.lubov.services.dao.jpa.impl;

import com.meitan.lubov.model.persistent.BuyingAct;
import com.meitan.lubov.services.dao.BuyingActDao;
import com.meitan.lubov.services.dao.jpa.JpaDao;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Date: Mar 4, 2010
 * Time: 6:32:26 PM
 *
 * @author denisk
 */
@Service("buyingActDao")
@Repository
public class JpaBuyingActDao extends JpaDao<BuyingAct, Long> implements BuyingActDao {
	@Override
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<BuyingAct> findForCartItem(Long itemId) {
		List<BuyingAct> result = em.createNamedQuery("findForCartItem").setParameter("itemId", itemId).getResultList();
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<BuyingAct> findForLogin(String login) {
		List<BuyingAct> result = em.createNamedQuery("findForLogin").setParameter("login", login).getResultList();
		return result;
	}

	@Override
	public List<BuyingAct> findByExample(BuyingAct exampleInstance, String... excludeProperty) {
		final List<BuyingAct> result = super.findByExample(exampleInstance, excludeProperty);
		return getDistinct(result);
	}
}
