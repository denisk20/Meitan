package com.meitan.lubov.services.util;

import com.meitan.lubov.model.ImageAware;
import com.meitan.lubov.model.persistent.Image;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author denis_k
 *         Date: 21.05.2010
 *         Time: 15:44:48
 */
@Repository
@Service("imageIdGenerationService")
public class ImageIdGenerationServiceImpl implements ImageIdGenerationService{
	private static final String IMAGE_PREFIX = "img";
	private static final String DELIM = "_";

	@Override
	public StringWrap generateIdForNextImage(ImageAware i) {
		Set<Image> images = i.getImages();
		if (images == null) {
			throw new IllegalArgumentException("No images for imageAware " + i);
		}
		int imagesCount = images.size();
		int imageIndex = imagesCount + 1;
		String newName = IMAGE_PREFIX + DELIM + i.getClass().getSimpleName() + DELIM + i.getId() + DELIM + imageIndex + System.currentTimeMillis();

		return new StringWrap(newName);
	}
}
