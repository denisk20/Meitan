package com.meitan.lubov.services.dao.jpa;

import com.meitan.lubov.services.dao.Dao;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Date: Mar 1, 2010
 * Time: 1:16:07 PM
 *
 * @author denisk
 */
@Service("dao")
@Repository
public class JpaDao <T, ID extends Serializable> implements Dao<T, ID> {

	private Class<T> persistentClass;
	private EntityManager em;

	@SuppressWarnings("unchecked")
	protected JpaDao() {
		persistentClass = (Class<T>) ((ParameterizedType)
				getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	@PersistenceContext
	public void setEm(EntityManager em) {
		this.em = em;
	}

	@Override
	public T findById(ID id) {
		T result;
		result = em.find(persistentClass, id)
	}

	@Override
	public List<T> findAll() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public List<T> findByExample(T exampleInstance, String... excludeProperty) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public T makePersistent(T entity) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void makeTransient(T entity) {
		//To change body of implemented methods use File | Settings | File Templates.
	}
}
