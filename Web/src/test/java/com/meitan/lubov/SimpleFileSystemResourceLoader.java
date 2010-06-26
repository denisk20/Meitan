package com.meitan.lubov;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * Date: Jun 27, 2010
 * Time: 12:29:12 AM
 *
 * @author denisk
 */
public class SimpleFileSystemResourceLoader implements ResourceLoader {
	@Override
	public Resource getResource(String location) {
		return new FileSystemResource(location);
	}

	@Override
	public ClassLoader getClassLoader() {
		return getClass().getClassLoader();
	}
}
