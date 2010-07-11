package com.meitan.lubov.services.dao;

import com.meitan.lubov.model.persistent.Client;

/**
 * Date: Mar 5, 2010
 * Time: 10:15:02 PM
 *
 * @author denisk
 */
public interface ClientDao extends Dao<Client, Long>{
	Client getByLogin(String login);
}
