package com.meitan.lubov;

import com.meitan.lubov.model.BoardType;
import com.meitan.lubov.model.persistent.Authority;
import com.meitan.lubov.model.persistent.BoardItem;
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

	private static final int EXPECTED_ABOUT_ITEMS_COUNT = 2;
	private static final int EXPECTED_CONSULTANT_ITEMS_COUNT = 3;

	@Override
		protected void setUpBeanNames() {
		beanNames.add("ent_AboutBoard");
		beanNames.add("ent_ConsultantsBoard");
	}

	@Override
	protected Dao<NewsBoard, Long> getDAO() {
		return testNewsBoardDao;
	}

	@Override
	protected void compareAdditionalProperties(NewsBoard beanFromSpring, NewsBoard beanFromDB) {
		assertEquals(beanFromSpring.getBoardType(), beanFromDB.getBoardType());
	}

	@Test
	public void testGetForBoard() {
		BoardType aboutType = BoardType.ABOUT;
		BoardType consultantsType = BoardType.FOR_CONSULTANTS;

		List<BoardItem> aboutItems = testNewsBoardDao.getForBoard(aboutType);
		assertEquals(EXPECTED_ABOUT_ITEMS_COUNT, aboutItems.size());

		List<BoardItem> consultantItems = testNewsBoardDao.getForBoard(consultantsType);
		assertEquals(EXPECTED_CONSULTANT_ITEMS_COUNT, consultantItems.size());
	}
}