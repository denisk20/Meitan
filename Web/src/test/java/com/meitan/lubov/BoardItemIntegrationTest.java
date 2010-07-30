package com.meitan.lubov;

import com.meitan.lubov.model.BoardType;
import com.meitan.lubov.model.persistent.BoardItem;
import com.meitan.lubov.model.persistent.NewsBoard;
import com.meitan.lubov.services.dao.BoardItemDao;
import com.meitan.lubov.services.dao.ClientDao;
import com.meitan.lubov.services.dao.Dao;
import com.meitan.lubov.services.dao.NewsBoardDao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Date: Jul 9, 2010
 * Time: 10:58:37 AM
 *
 * @author denisk
 */
public class BoardItemIntegrationTest extends GenericIntegrationTest <BoardItem>{
	@Autowired
	private NewsBoardDao testNewsBoardDao;

//	todo make this test
	@Autowired
	private BoardItemDao boardItemDao;

	@Override
		protected void setUpBeanNames() {
		beanNames.add("ent_aboutPost1");
		beanNames.add("ent_aboutPost2");
		beanNames.add("ent_consPost1");
		beanNames.add("ent_consPost2");
		beanNames.add("ent_consPost3");
	}

	@Override
	protected Dao<BoardItem, Long> getDAO() {
		return boardItemDao;
	}

	@Override
	protected void compareAdditionalProperties(BoardItem beanFromSpring, BoardItem beanFromDB) {
		assertEquals(beanFromSpring.getBoards().size(), beanFromDB.getBoards().size());
		Iterator<NewsBoard> xmlIter = beanFromSpring.getBoards().iterator();
		Iterator<NewsBoard> dbIter = beanFromDB.getBoards().iterator();
		while (xmlIter.hasNext()) {
			NewsBoard xmlBoard = xmlIter.next();
			NewsBoard dbBoard = dbIter.next();
			assertEquals(xmlBoard,dbBoard);
		}
	}

	@Test
	public void testMakeTransient() {
		BoardItem item = beansFromDb.get(0);
		Set<NewsBoard> boards = item.getBoards();
		assertTrue(boards.size() > 0);

		for (NewsBoard b : boards) {
			assertTrue(b.getItems().contains(item));
		}

		boardItemDao.makeTransient(item);
		boardItemDao.flush();

		for (NewsBoard b : boards) {
			assertFalse(b.getItems().contains(item));
		}

		BoardItem loaded = boardItemDao.findById(item.getId());
		assertNull(loaded);

	}
}