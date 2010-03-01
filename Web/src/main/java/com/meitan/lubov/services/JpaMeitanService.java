package com.meitan.lubov.services;

import com.meitan.lubov.model.persistent.Product;
import com.meitan.lubov.model.persistent.ProductCategory;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Date: Feb 7, 2010
 * Time: 7:51:52 AM
 *
 * @author denisk
 */
@Service("meitanService")
@Repository
public class JpaMeitanService implements MeitanService {

	private EntityManager em;

	@PersistenceContext
	public void setEm(EntityManager em) {
		this.em = em;
	}

	@Override
	@Transactional
	@SuppressWarnings("unchecked")
	public ArrayList<Product> getNewProducts() {
		ArrayList<Product> result = (ArrayList<Product>)
				em.createNamedQuery("getProductsNew").getResultList();
		return result;
	}

	@Override
	@Transactional
	@SuppressWarnings("unchecked")
	public ArrayList<Product> getAllProducts() {
		ArrayList<Product> result = (ArrayList<Product>)
				em.createNamedQuery("getProducts").getResultList();
		return result;
	}

	@Override
	@Transactional
	@SuppressWarnings("unchecked")
	public ArrayList<Product> getProductsForCategory(long categoryId) {
		ArrayList<Product> result = (ArrayList<Product>)
				em.createNamedQuery("getProductsForCategory")
						.setParameter("categoryId", categoryId)
						.getResultList();
		return result;
	}

	@Override
	@Transactional
	@SuppressWarnings("unchecked")
	public ArrayList<ProductCategory> getAllCategories() {
		ArrayList<ProductCategory> result = (ArrayList<ProductCategory>)
				em.createNamedQuery("getProductCategories")
						.getResultList();
		return result;
	}

	@Override
	@Transactional
	@SuppressWarnings("unchecked")
	public void persistProduct(Product product) {
		em.persist(product);
	}
}
