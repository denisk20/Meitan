package com.meitan.lubov.model;

import com.meitan.lubov.model.persistent.Image;

import java.util.Set;

/**
 * @author denis_k
 *         Date: 05.05.2010
 *         Time: 15:14:58
 */
public interface ImageAware extends IdAware {
	Set<Image> getImages();

	void addImage(Image image);

	void removeImage(Image image);

	boolean isAllowedToAdd();

	Image getAvatar();
//todo unit test
	void setAvatar(Image image);
}
