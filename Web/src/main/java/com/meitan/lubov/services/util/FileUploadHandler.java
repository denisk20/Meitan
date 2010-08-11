package com.meitan.lubov.services.util;

import com.meitan.lubov.model.ImageAware;
import com.meitan.lubov.model.persistent.Image;
import com.meitan.lubov.services.media.ImageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.webflow.execution.RequestContext;
import sun.awt.image.ToolkitImage;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 * Date: Apr 19, 2010
 * Time: 3:50:32 PM
 *
 * @author denisk
 */
public class FileUploadHandler implements Serializable {
	public final static String FILE_PARAM_NAME = "file";
	public static final String UPLOAD_DIR_NAME = "uploaded";

	private static final String JPEG_TYPE = "image/jpeg";
	private static final String BMP_TYPE = "image/bmp";
	private static final String GIF_TYPE = "image/gif";
	private static final String PNG_TYPE = "image/png";

	private static final int MAX_WIDTH = 475;
	private static final int MAX_HEIGHT = 475;

	@Autowired
	protected ImageManager imageManager;

	private String uploadPath;

	public String getUploadPath() {
		return uploadPath;
	}

	public void setUploadPath(String uploadPath) {
		this.uploadPath = uploadPath;
	}

	public Image precessTempFile(RequestContext requestContext, StringWrap imageName) throws IOException {
		return processFile(requestContext, imageName.getWrapped());
	}

	protected Image processFile(RequestContext requestContext, String imageName) throws IOException {
		MultipartFile file = requestContext.getRequestParameters().getMultipartFile(FILE_PARAM_NAME);

		if (file == null) {
			throw new IllegalArgumentException("File was null");
		}
		if (file.getSize() > 0) {
			if (! isFileValid(requestContext, file)) {
				throw new IllegalArgumentException("Not valid image type: " + file.getContentType());
			}
			File imageFile = new File(uploadPath + "/" + imageName);

			imageManager.uploadImage(file, imageFile, MAX_WIDTH, MAX_HEIGHT);
			//			String imageRelativePath = "/" + UPLOAD_DIR_NAME + "/" + newName;
			Image image = createImage("/" + imageName);

			return image;
		} else {
			requestContext.getMessageContext()
					.addMessage(new MessageBuilder()
							.error()
							.defaultText("Пожалуйста, выберите файл")
							.build());
			throw new IllegalArgumentException("File was null: " + file);
		}
	}

	private boolean isFileValid(RequestContext requestContext, MultipartFile file) {
		String contentType = file.getContentType();
		if (contentType == null) {
			throw new IllegalArgumentException("No extension in file " + file);
		}
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

	private Image createImage(String imageRelativePath) {
		Image image = new Image(imageRelativePath);
		return image;
	}

	public void sayHello() {
		System.out.printf("Hello!");
	}

}