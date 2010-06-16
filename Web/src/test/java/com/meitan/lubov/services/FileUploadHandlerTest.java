package com.meitan.lubov.services;

import com.meitan.lubov.model.ImageAware;
import com.meitan.lubov.model.persistent.Category;
import com.meitan.lubov.model.persistent.Image;
import com.meitan.lubov.services.dao.CategoryDao;
import com.meitan.lubov.services.util.FileUploadHandler;
import com.meitan.lubov.services.util.StringWrap;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.test.MockParameterMap;
import org.springframework.webflow.test.MockRequestContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import static org.junit.Assert.*;

/**
 * @author denis_k
 *         Date: 14.06.2010
 *         Time: 15:43:27
 */
public class FileUploadHandlerTest {

	private static final String FILE_NAME = "Toolbelt.jpg";
	private static final String BASE_PATH = System.getenv("MEITAN_HOME");
	private static final String FULL_TESTUPLOAD_DIRECTORY_PATH = BASE_PATH + "/" + MockFileUploadHandler.TEST_UPLOAD_DIRECTORY;
	private final static String PATH_TO_FILE = BASE_PATH + "/" + "Web/src/test/resources/" +FILE_NAME;
	
	private FileUploadHandler testable = new MockFileUploadHandler();

	@Test
	public void testProcessFile() throws IOException {
		File file = new File(PATH_TO_FILE);
		assertTrue("File does not exist: " + PATH_TO_FILE, file.exists());

		InputStream is = new FileInputStream(file);
		MultipartFile multipartFile = new MockMultipartFile("myFile",FILE_NAME, "image/jpeg", is);
		MockParameterMap parameterMap = new MockParameterMap();
		parameterMap.put(FileUploadHandler.FILE_PARAM_NAME, multipartFile);
		RequestContext requestContext = new MockRequestContext(parameterMap);

		File imageFile;

		String imageName = "myImage";
		Image image = testable.precessTempFile(requestContext, new StringWrap(imageName));

		assertNotNull("No image was created", image);

		String imageFilePath = BASE_PATH + "/" + image.getUrl();

		imageFile = new File(imageFilePath);
		imageFile.deleteOnExit();
		
		assertEquals("Wrong image name", imageName, imageFile.getName());
		assertTrue("Image file doesn't exist: " + imageFile, imageFile.exists());

		assertEquals("Wrong image path", (FULL_TESTUPLOAD_DIRECTORY_PATH + "/" + imageName).replaceAll("\\\\", "/"), imageFile.getPath().replaceAll("\\\\", "/"));

		
	}

	
}
