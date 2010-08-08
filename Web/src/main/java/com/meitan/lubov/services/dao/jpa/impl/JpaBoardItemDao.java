package com.meitan.lubov.services.dao.jpa.impl;

import com.meitan.lubov.model.persistent.BoardItem;
import com.meitan.lubov.model.persistent.NewsBoard;
import com.meitan.lubov.services.dao.BoardItemDao;
import com.meitan.lubov.services.dao.NewsBoardDao;
import com.meitan.lubov.services.dao.jpa.JpaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Date: Jul 30, 2010
 * Time: 12:15:11 PM
 *
 * @author denisk
 */
@Service("boardItemDao")
@Repository
public class JpaBoardItemDao extends JpaDao<BoardItem,Long> implements BoardItemDao{

	@Override
	@Transactional
	public void makePersistent(BoardItem entity) {
		entity=em.merge(entity);
		super.makePersistent(entity);	//To change body of overridden methods use File | Settings | File Templates.
	}

	@Override
	@Transactional
	public void makeTransient(BoardItem entity) {
		entity=em.merge(entity);
		for (NewsBoard b : entity.getBoards()) {
			b.getItems().remove(entity);
		}
		super.makeTransient(entity);
	}

	@Override
	public BoardItem newInstance() throws IllegalAccessException, InstantiationException {
		BoardItem result = super.newInstance();
		result.setPostDate(new Date());
		return result;
	}
}
