package com.meitan.lubov.services;

import com.meitan.lubov.SimpleFileSystemResourceLoader;
import com.meitan.lubov.model.persistent.Image;
import com.meitan.lubov.services.media.ImageManager;
import com.meitan.lubov.services.util.FileUploadHandler;
import com.meitan.lubov.services.util.Utils;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.execution.RequestContext;

import javax.servlet.ServletContext;
import java.io.IOException;

/**
 * @author denis_k
 *         Date: 14.06.2010
 *         Time: 15:44:22
 */
public class MockFileUploadHandler extends FileUploadHandler {
	public static final String TEST_UPLOAD_DIRECTORY = "uploaded";

	public MockFileUploadHandler() {
		//todo this is not the case
		ServletContext servletContext = new MockServletContext(
				System.getenv("MEITAN_HOME"), new SimpleFileSystemResourceLoader());
		utils = new Utils();
		imageManager = new ImageManager();
		utils.setServletContext(servletContext);
	}

	@Override
	protected Image processFile(RequestContext requestContext, String uploadDirName, String imageName) throws IOException {
		return super.processFile(requestContext, TEST_UPLOAD_DIRECTORY, imageName);
	}


}
