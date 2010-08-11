package com.meitan.lubov.services.dao;

import com.meitan.lubov.model.ImageAware;
import com.meitan.lubov.model.persistent.Image;

import javax.servlet.ServletContext;

/**
 * @author denis_k
 *         Date: 19.05.2010
 *         Time: 11:55:24
 */
//todo Unit test for this one
public interface ImageDao extends Dao<Image, Long>{
	void deleteFromDisk(Image image);

	void addImageToEntity(ImageAware entity, Image i);

	void removeImageFromEntity(ImageAware entity, Image i);

    String getCustomPathPrefix();

    void setCustomPathPrefix(String pathPrefix);
}
