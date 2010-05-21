package com.meitan.lubov.services.util;

import com.meitan.lubov.model.ImageAware;

/**
 * @author denis_k
 *         Date: 21.05.2010
 *         Time: 15:43:51
 */
public interface ImageIdGenerationService {
	String generateIdForNextImage(ImageAware i);
}
