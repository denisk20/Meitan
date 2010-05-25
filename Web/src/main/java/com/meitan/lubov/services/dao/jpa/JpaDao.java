package com.meitan.lubov.services.dao.jpa;

import com.meitan.lubov.services.dao.Dao;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

/**
 * Date: Mar 1, 2010
 * Time: 1:16:07 PM
 *
 * @author denisk
 */
public abstract class JpaDao <T, ID extends Serializable> implements Dao<T, ID>, Serializable {

	private Class<T> persistentClass;
	protected EntityManager em;

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
	@Transactional
	public T findById(ID id) {
		T result;
		result = em.find(persistentClass, id);
		return result;
	}

	@Override
    @Transactional
	public List<T> findAll() {
		return findByCriteria();
	}

    @SuppressWarnings("unchecked")
	@Transactional
    protected List<T> findByCriteria(Criterion... criterions) {
        Criteria criteria = ((Session) em.getDelegate()).createCriteria(persistentClass);
        for (Criterion c : criterions) {
            criteria.add(c);
        }
        return criteria.list();
    }

	@Override
	@Transactional
	public List<T> findByExample(T exampleInstance, String... excludeProperty) {
        Criteria criteria = ((Session) em.getDelegate()).createCriteria(persistentClass);
        Example example = Example.create(exampleInstance);
        for (String exclude : excludeProperty) {
            example.excludeProperty(exclude);
        }
        criteria.add(example);
        return criteria.list();
	}

	@Override
	@Transactional
	public void makePersistent(T entity) {
        em.persist(entity);
    }

	@Override
	@Transactional
	public void makeTransient(T entity) {
		em.remove(entity);
	}

	@Override
	@Transactional
	public void deleteById(ID id) {
		T entity = findById(id);
		makeTransient(entity);
	}

	@Transactional
	public void flush() {
		em.flush();
	}
}
