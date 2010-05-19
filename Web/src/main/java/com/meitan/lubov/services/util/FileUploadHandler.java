package com.meitan.lubov.services.util;

import com.meitan.lubov.model.ImageAware;
import com.meitan.lubov.model.persistent.Image;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.webflow.execution.RequestContext;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

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

	private static final String JPEG_TYPE = "image/jpeg";
	private static final String BMP_TYPE = "image/bmp";
	private static final String GIF_TYPE = "image/gif";

	private static final String ERROR_RESULT = "error";
	private static final String OK_RESULT = "ok";

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public Image processFile(ImageAware entity, RequestContext requestContext) throws IOException {
		MultipartFile file = requestContext.getRequestParameters().getMultipartFile(FILE_PARAM_NAME);

		if (file == null) {
			throw new IllegalArgumentException("File was null");
		}
		if (file.getSize() > 0) {
			String contentType = file.getContentType();
			if (! (contentType.equals(JPEG_TYPE) || contentType.equals(BMP_TYPE)|| contentType.equals(GIF_TYPE))) {
				requestContext.getMessageContext()
						.addMessage(new MessageBuilder()
								.error()
								.defaultText("Wrong uploaded file type, should be either JPEG, BMP or GIF, but was " + contentType)
								.build());
				return null;
			}
			String uploadedFolderPath = servletContext.getRealPath(UPLOAD_DIR_NAME);
			File uploadDir = new File(uploadedFolderPath);
			if (!uploadDir.exists()) {
				throw new IllegalStateException("Upload directory doesn't exist: " + UPLOAD_DIR_NAME);
			}
			if (!uploadDir.isDirectory()) {
				throw new IllegalStateException("Upload directory is not a directory: " + UPLOAD_DIR_NAME);
			}
			//rename it
			String fileName = file.getOriginalFilename();
			int dotIndex = fileName.lastIndexOf(".");
			String originalExtension = fileName.substring(dotIndex + 1);
			String newName = IMAGE_PREFIX + DELIM + entity.getClass().getSimpleName() + DELIM + entity.getId() + "." + originalExtension;
			File imageFile = new File(uploadDir, newName);
			file.transferTo(imageFile);

			String imageRelativePath = "/" + UPLOAD_DIR_NAME + "/" + newName;
			Image image = createImage(imageRelativePath, imageFile.getAbsolutePath());

			addImageToEntity(entity, image);

			return image;
		} else {
			requestContext.getMessageContext()
					.addMessage(new MessageBuilder()
							.error()
							.defaultText("File was empty: " + file.getOriginalFilename())
							.build());
			return null;
		}
	}

	private void addImageToEntity(ImageAware entity, Image image) {
		entity.addImage(image);
	}

	private Image createImage(String imageRelativePath, String imageAbsolutePath) {
		Image image = new Image(imageRelativePath);
		image.setAbsolutePath(imageAbsolutePath);
		return image;
	}

	public void sayHello() {
		System.out.printf("Hello!");
	}

}