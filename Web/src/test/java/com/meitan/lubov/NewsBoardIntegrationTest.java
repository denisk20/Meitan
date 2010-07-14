package com.meitan.lubov;

import com.meitan.lubov.model.persistent.Authority;
import com.meitan.lubov.model.persistent.Client;
import com.meitan.lubov.model.persistent.NewsBoard;
import com.meitan.lubov.services.dao.AuthorityDao;
import com.meitan.lubov.services.dao.ClientDao;
import com.meitan.lubov.services.dao.Dao;
import com.meitan.lubov.services.dao.NewsBoardDao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Date: Jul 9, 2010
 * Time: 10:58:37 AM
 *
 * @author denisk
 */
public class NewsBoardIntegrationTest extends GenericIntegrationTest <NewsBoard>{
	@Autowired
	private NewsBoardDao testNewsBoardDao;

	@Autowired
	private ClientDao testClientDao;

	@Override
		protected void setUpBeanNames() {
		beanNames.add("ent_board");
	}

	@Override
	protected Dao<NewsBoard, Long> getDAO() {
		return testNewsBoardDao;
	}
}