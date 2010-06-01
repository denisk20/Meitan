package com.meitan.lubov.services.dao;

import com.meitan.lubov.model.IdAware;

import java.io.Serializable;
import java.util.List;

/**
 * Date: Mar 1, 2010
 * Time: 1:01:38 PM
 *
 * @author denisk
 */
public interface Dao<T, ID extends Serializable> {
	T findById(ID id);

    List<T> findAll();

    List<T> findByExample(T exampleInstance, String... excludeProperty);

    void makePersistent(T entity);

	void deleteById(ID id);
	
    void makeTransient(T entity);

    void flush();

	//generic method that can be used from every DAO
	Object getPersistentObject(IdAware entity);

}
