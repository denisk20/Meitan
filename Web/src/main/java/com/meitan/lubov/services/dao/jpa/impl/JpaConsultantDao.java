package com.meitan.lubov.services.dao.jpa.impl;

import com.meitan.lubov.model.persistent.Consultant;
import com.meitan.lubov.services.dao.ConsultantDao;
import com.meitan.lubov.services.dao.jpa.JpaDao;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

/**
 * Date: Mar 5, 2010
 * Time: 10:17:51 PM
 *
 * @author denisk
 */
@Repository
@Service("consultantDao")
public class JpaConsultantDao extends JpaDao<Consultant, Long> implements ConsultantDao{
}
