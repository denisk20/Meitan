package com.meitan.lubov.services.dao.jpa.impl;

import com.meitan.lubov.model.ImageAware;
import com.meitan.lubov.model.persistent.Image;
import com.meitan.lubov.services.dao.ImageDao;
import com.meitan.lubov.services.dao.jpa.JpaDao;
import com.meitan.lubov.services.util.FileUploadHandler;
import com.meitan.lubov.services.util.Utils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.io.File;

/**
 * @author denis_k
 *         Date: 19.05.2010
 *         Time: 15:59:37
 */
@Service("imageDao")
@Repository
public class JpaImageDao extends JpaDao<Image, Long> implements ImageDao{
	private final Log log = LogFactory.getLog(getClass());
	@Autowired
	private Utils utils;
//    private ServletContext context;

    private String customPathPrefix;

    @Override
    public String getCustomPathPrefix() {
        return customPathPrefix;
    }

    @Override
    public void setCustomPathPrefix(String customPathPrefix) {
        this.customPathPrefix = customPathPrefix;
    }

    @Override
	public void deleteFromDisk(Image i) {
        String path = utils.getImageUploadDirectoryPath() + i.getUrl();

        if (path.equals("")) {
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
//		entity = em.find(entity.getClass(), entity.getId());
		entity.addImage(i);
	}

	@Override
	@Transactional
	public void removeImageFromEntity(ImageAware entity, Image i) {
		if (i == null) {
			throw new IllegalArgumentException("null image was passed to the method alongside with entity " + entity);
		}

		entity.removeImage(i);
		if (entity.getAvatar() != null) {
			if (entity.getAvatar().equals(i)) {
				//todo stub image
				entity.setAvatar(null);
			}
		}
		//remove from disk
		deleteFromDisk(i);
		i = findById(i.getId());
		if (i != null) {
			makeTransient(i);
		}
		//em.merge(entity);
	}

}
