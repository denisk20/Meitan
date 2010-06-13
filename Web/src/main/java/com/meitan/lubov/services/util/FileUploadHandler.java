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
	public static final String UPLOAD_DIR_NAME = "uploaded";

	private ServletContext servletContext;

	private static final String JPEG_TYPE = "image/jpeg";
	private static final String BMP_TYPE = "image/bmp";
	private static final String GIF_TYPE = "image/gif";
	private static final String PNG_TYPE = "image/png";

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public Image precessTempFile(ImageAware entity, RequestContext requestContext, StringWrap imageName) throws IOException {
		return processFile(entity, requestContext, UPLOAD_DIR_NAME, imageName.getWrapped());
	}

    //todo damn me if this is not to be covered with unit test
	private Image processFile(ImageAware entity, RequestContext requestContext, String uploadDirName, String imageName) throws IOException {
		MultipartFile file = requestContext.getRequestParameters().getMultipartFile(FILE_PARAM_NAME);

		if (file == null) {
			throw new IllegalArgumentException("File was null");
		}
		if (file.getSize() > 0) {
			if (! isFileValid(requestContext, file)) {
				throw new IllegalArgumentException("Not valid image type: " + file.getContentType());
			}
			File imageFile = getDestFile(file, entity, uploadDirName, imageName);
			file.transferTo(imageFile);

//			String imageRelativePath = "/" + UPLOAD_DIR_NAME + "/" + newName;
			String fullPath = imageFile.getPath();
			int directoryIndex = fullPath.indexOf(uploadDirName);
			String imageRelativePath = fullPath.substring(directoryIndex);
			imageRelativePath = imageRelativePath.replace("\\", "/");
			imageRelativePath = "/" + imageRelativePath;
			Image image = createImage(imageRelativePath, imageFile.getAbsolutePath());

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

	private File getDestFile(MultipartFile file, ImageAware entity, String uploadDirName, String imageName) {
		String uploadedFolderPath = servletContext.getRealPath(uploadDirName);
		File uploadDir = new File(uploadedFolderPath);
		if (!uploadDir.exists()) {
			throw new IllegalStateException("Upload directory doesn't exist: " + uploadDirName);
		}
		if (!uploadDir.isDirectory()) {
			throw new IllegalStateException("Upload directory is not a directory: " + uploadDirName);
		}
		//rename it
		File imageFile = new File(uploadDir, imageName);

		return imageFile;
	}

	private boolean isFileValid(RequestContext requestContext, MultipartFile file) {
		String contentType = file.getContentType();
		if (contentType.equals(JPEG_TYPE) || contentType.equals(BMP_TYPE)|| contentType.equals(GIF_TYPE)|| contentType.equals(PNG_TYPE)) {
			return true;
		}
		requestContext.getMessageContext()
				.addMessage(new MessageBuilder()
						.error()
						.defaultText("Wrong uploaded file type, should be either JPEG, BMP or GIF, but was " + contentType)
						.build());
		return false;
	}

	private Image createImage(String imageRelativePath, String imageAbsolutePath) {
		Image image = new Image(imageRelativePath);
		//image.setAbsolutePath(imageAbsolutePath);
		return image;
	}

	public void sayHello() {
		System.out.printf("Hello!");
	}

}