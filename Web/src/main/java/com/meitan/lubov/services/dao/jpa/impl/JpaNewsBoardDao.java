package com.meitan.lubov.services.dao.jpa.impl;

import com.meitan.lubov.model.BoardType;
import com.meitan.lubov.model.persistent.BoardItem;
import com.meitan.lubov.model.persistent.NewsBoard;
import com.meitan.lubov.services.dao.NewsBoardDao;
import com.meitan.lubov.services.dao.jpa.JpaDao;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author denis_k
 *         Date: 14.07.2010
 *         Time: 23:56:19
 */
@Repository
@Service("newsBoardDao")
public class JpaNewsBoardDao extends JpaDao<NewsBoard, Long> implements NewsBoardDao{
	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<BoardItem> getAll() {
		List<BoardItem> result = em.createNamedQuery("getAll").getResultList();
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	 public List<BoardItem> getForBoard(BoardType boardType) {
		List<BoardItem> result = em.createNamedQuery("getForBoard")
				.setParameter("boardType", boardType).getResultList();
		return result;

	 }

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public NewsBoard getForType(BoardType type) {
		NewsBoard board = (NewsBoard) em.createNamedQuery("getForType")
				.setParameter("type", type).getResultList().get(0);
		return board;
	}
}
