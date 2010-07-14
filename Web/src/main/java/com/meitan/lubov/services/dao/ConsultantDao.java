package com.meitan.lubov.services.dao;

import com.meitan.lubov.model.persistent.Client;
import com.meitan.lubov.model.persistent.Consultant;

/**
 * Date: Mar 5, 2010
 * Time: 10:15:41 PM
 *
 * @author denisk
 */
public interface ConsultantDao extends Dao<Consultant, Long>{
	Consultant newConsultant(Client prototype);
}
