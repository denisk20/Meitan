package com.meitan.lubov.services.dao.jpa.impl;

import com.meitan.lubov.model.persistent.Client;
import com.meitan.lubov.model.persistent.Consultant;
import com.meitan.lubov.services.dao.ConsultantDao;
import com.meitan.lubov.services.dao.jpa.JpaDao;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Date: Mar 5, 2010
 * Time: 10:17:51 PM
 *
 * @author denisk
 */
@Repository
@Service("consultantDao")
public class JpaConsultantDao extends JpaDao<Consultant, Long> implements ConsultantDao{
	@Override
	@Transactional
	public Consultant newConsultant(Client prototype) {
		Consultant consultant = new Consultant(prototype);
		consultant.setRoles(prototype.getRoles());
		return consultant;
	}
}
