package com.meitan.lubov.services;

import com.meitan.lubov.model.persistent.Image;
import com.meitan.lubov.services.util.FileUploadHandler;
import com.meitan.lubov.services.util.StringWrap;
import com.meitan.lubov.services.util.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.test.MockParameterMap;
import org.springframework.webflow.test.MockRequestContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import static org.junit.Assert.*;

/**
 * @author denis_k
 *         Date: 14.06.2010
 *         Time: 15:43:27
 */
@ContextConfiguration(locations = {"classpath:testsSetup.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class FileUploadHandlerTest {

	public static final String FILE_NAME = "Toolbelt.jpg";
	private static final String BASE_PATH = System.getenv("MEITAN_HOME");
	public final static String PATH_TO_FILE = BASE_PATH + "/" + "Web/src/test/resources/" +FILE_NAME;

	@Autowired
	private FileUploadHandler fileUploadHandler;
	@Autowired
	protected Utils utils;

	@Test
	public void testProcessFile() throws IOException {
		MockParameterMap parameterMap = getParameterMapForFileUpload();
		RequestContext requestContext = new MockRequestContext(parameterMap);

		File imageFile;

		String imageName = "myImage";
		Image image = fileUploadHandler.precessTempFile(requestContext, new StringWrap(imageName));

		assertNotNull("No image was created", image);

		String imageFilePath = utils.getImageUploadDirectoryPath() + "/" + image.getUrl();

		imageFile = new File(imageFilePath);
		imageFile.deleteOnExit();
		
		assertEquals("Wrong image name", imageName, imageFile.getName());
		assertTrue("Image file doesn't exist: " + imageFile, imageFile.exists());

//		assertEquals("Wrong image path", (FULL_TESTUPLOAD_DIRECTORY_PATH + "/" + imageName).replaceAll("\\\\", "/"), imageFile.getPath().replaceAll("\\\\", "/"));
	}

	public static MockParameterMap getParameterMapForFileUpload() throws IOException {
		File file = new File(PATH_TO_FILE);
		assertTrue("File does not exist: " + PATH_TO_FILE, file.exists());

		InputStream is = new FileInputStream(file);
		MultipartFile multipartFile = new MockMultipartFile("myFile",FILE_NAME, "image/jpeg", is);
		MockParameterMap parameterMap = new MockParameterMap();
		parameterMap.put(FileUploadHandler.FILE_PARAM_NAME, multipartFile);
		return parameterMap;
	}
}
