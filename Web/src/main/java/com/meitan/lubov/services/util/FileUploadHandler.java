package com.meitan.lubov.services.util;

import org.springframework.binding.message.MessageContext;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import java.io.IOException;
import java.io.Serializable;

/**
 * Date: Apr 19, 2010
 * Time: 3:50:32 PM
 *
 * @author denisk
 */
public class FileUploadHandler implements Serializable {
	private final static String FILE_PARAM_NAME = "file";

	public void processFile(MessageContext messageContext, RequestContext requestContext) throws IOException {
		MultipartFile file = requestContext.getRequestParameters().getMultipartFile(FILE_PARAM_NAME);
    	file.getBytes();

		if (file == null) {
			throw new IllegalArgumentException("File was null");
		}
		if (file.getSize() > 0) {
			// data was uploaded
			requestContext.getFlashScope().put("file", new String(file.getBytes()));
		} 
	}
}