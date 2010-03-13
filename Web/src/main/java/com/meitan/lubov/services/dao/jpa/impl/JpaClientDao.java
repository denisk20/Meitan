package com.meitan.lubov.services.dao.jpa.impl;

import com.meitan.lubov.model.persistent.Client;
import com.meitan.lubov.services.dao.ClientDao;
import com.meitan.lubov.services.dao.jpa.JpaDao;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

/**
 * Date: Mar 5, 2010
 * Time: 10:16:28 PM
 *
 * @author denisk
 */
@Repository
@Service("clientDao")
public class JpaClientDao extends JpaDao<Client, Long> implements ClientDao {
}
