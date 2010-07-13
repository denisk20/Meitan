package com.meitan.lubov.services.dao;

import com.meitan.lubov.model.persistent.BuyingAct;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Date: Mar 4, 2010
 * Time: 6:30:12 PM
 *
 * @author denisk
 */
public interface BuyingActDao extends Dao<BuyingAct, Long>{
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	List<BuyingAct> findForCartItem(Long itemId);
}
