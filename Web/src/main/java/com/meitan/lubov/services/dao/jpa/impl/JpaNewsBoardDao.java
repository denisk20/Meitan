package com.meitan.lubov.services.dao.jpa.impl;

import com.meitan.lubov.model.persistent.NewsBoard;
import com.meitan.lubov.services.dao.NewsBoardDao;
import com.meitan.lubov.services.dao.jpa.JpaDao;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

/**
 * @author denis_k
 *         Date: 14.07.2010
 *         Time: 23:56:19
 */
@Repository
@Service("newsBoardDao")
public class JpaNewsBoardDao extends JpaDao<NewsBoard, Long> implements NewsBoardDao{
}
