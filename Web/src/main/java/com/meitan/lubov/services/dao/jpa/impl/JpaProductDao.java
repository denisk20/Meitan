package com.meitan.lubov.services.dao.jpa.impl;

import com.meitan.lubov.model.persistent.Product;
import com.meitan.lubov.services.dao.ProductDao;
import com.meitan.lubov.services.dao.jpa.JpaDao;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

/**
 * Date: Mar 4, 2010
 * Time: 2:10:26 PM
 *
 * @author denisk
 */
@Service("productDao")
@Repository
public class JpaProductDao extends JpaDao<Product, Long> implements ProductDao {

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public ArrayList<Product> getAll() {
		ArrayList<Product> result = (ArrayList<Product>)
				em.createNamedQuery("getProducts").getResultList();
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public ArrayList<Product> getNew() {
		ArrayList<Product> result = (ArrayList<Product>)
				em.createNamedQuery("getProductsNew").getResultList();
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public ArrayList<Product> getForCategory(Long categoryId) {
		ArrayList<Product> result = (ArrayList<Product>)
				em.createNamedQuery("getProductsForCategory")
						.setParameter("categoryId", categoryId)
						.getResultList();
		return result;
	}
}
