package com.meitan.lubov.services.dao;

import com.meitan.lubov.model.BoardType;
import com.meitan.lubov.model.persistent.BoardItem;
import com.meitan.lubov.model.persistent.NewsBoard;
import com.meitan.lubov.services.dao.jpa.JpaDao;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author denis_k
 *         Date: 14.07.2010
 *         Time: 23:04:11
 */
public interface NewsBoardDao extends Dao<NewsBoard, Long> {
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	List<BoardItem> getAll();

	List<BoardItem> getForBoard(BoardType boardType);
}
