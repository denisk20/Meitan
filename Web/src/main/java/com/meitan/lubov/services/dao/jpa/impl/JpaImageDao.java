package com.meitan.lubov.services.dao.jpa.impl;

import com.meitan.lubov.model.ImageAware;
import com.meitan.lubov.model.persistent.Image;
import com.meitan.lubov.services.dao.ImageDao;
import com.meitan.lubov.services.dao.jpa.JpaDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Set;

/**
 * @author denis_k
 *         Date: 19.05.2010
 *         Time: 15:59:37
 */
@Service("imageDao")
@Repository
public class JpaImageDao extends JpaDao<Image, Long> implements ImageDao {
	private final Log log = LogFactory.getLog(getClass());

	@Override
	public void deleteFromDisk(Image i) {
		String path = i.getAbsolutePath();

		if (path == null) {
			log.error("Path was null for image " + i);
		} else {
			File file = new File(path);
			if (!file.exists()) {
				//can't throw exception here because of spring's AOP...
				log.error("No corresponding image file for image " + i);
			} else {
				boolean deleted = file.delete();
				if (!deleted) {
					throw new IllegalArgumentException("Can't delete image. The file must be busy: " + file);
				}
			}
		}
	}

	/**
	 * This method does 1 assumption - ImageAware is persistent entity
	 */
	@Override
	@Transactional
	public void addImageToEntity(ImageAware entity, Image i) {
		entity = em.find(entity.getClass(), entity.getId());
		entity.addImage(i);
	}

	@Override
	@Transactional
	public void removeImageFromEntity(ImageAware entity, Image i) {
		if (i == null) {
			throw new IllegalArgumentException("null image was passed to the method alongside with entity " + entity);
		}

		entity.removeImage(i);
		//remove from disk
		deleteFromDisk(i);
		i = findById(i.getId());
		if (i != null) {
			makeTransient(i);
		}
		//em.merge(entity);
	}
}
