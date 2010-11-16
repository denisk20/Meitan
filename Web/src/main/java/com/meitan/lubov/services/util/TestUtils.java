package com.meitan.lubov.services.util;

import com.meitan.lubov.services.util.Utils;

import java.io.File;

/**
 * Date: Nov 16, 2010
 * Time: 11:58:18 AM
 *
 * @author denisk
 */
public class TestUtils extends Utils {
	protected final String TEST_UPLOAD_DIR = "uploaded";
	@Override
	public String getImageUploadDirectoryPath() {
		return getHomePath() + File.separator + TEST_UPLOAD_DIR;
	}
}
