package com.meitan.lubov.services.dao.jpa.impl;

import com.meitan.lubov.model.persistent.Category;
import com.meitan.lubov.services.dao.CategoryDao;
import com.meitan.lubov.services.dao.jpa.JpaDao;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Date: Mar 5, 2010
 * Time: 10:04:30 PM
 *
 * @author denisk
 */
@Service("categoryDao")
@Repository
public class JpaCategoryDao extends JpaDao<Category, Long> implements CategoryDao {
}
