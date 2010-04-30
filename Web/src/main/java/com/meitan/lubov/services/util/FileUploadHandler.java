package com.meitan.lubov.services.util;

import org.springframework.binding.message.MessageContext;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Date: Apr 19, 2010
 * Time: 3:50:32 PM
 *
 * @author denisk
 */
public class FileUploadHandler implements Serializable, ServletContextAware {
	private final static String FILE_PARAM_NAME = "file";
	private static final String UPLOAD_DIR_NAME = "uploaded";
	private static final String IMAGE_PREFIX = "img";
	private static final String DELIM = "_";

	private ServletContext servletContext;

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public void processFile(String entityName, String id, RequestContext requestContext) throws IOException {
		MultipartFile file = requestContext.getRequestParameters().getMultipartFile(FILE_PARAM_NAME);
    	file.getBytes();

		if (file == null) {
			throw new IllegalArgumentException("File was null");
		}
		if (file.getSize() > 0) {
			String uploadedFolderPath = servletContext.getRealPath(UPLOAD_DIR_NAME);
			File uploadDir = new File(uploadedFolderPath);
			if (!uploadDir.exists()) {
				throw new IllegalStateException("Upload directory doesn't exist: " + UPLOAD_DIR_NAME);
			}
			if (!uploadDir.isDirectory()) {
				throw new IllegalStateException("Upload directory is not a directory: " + UPLOAD_DIR_NAME);

			}
			//rename it
			String newName = IMAGE_PREFIX + DELIM + entityName + DELIM + id;
			file.transferTo(new File(uploadDir, newName));
		}
	}

}