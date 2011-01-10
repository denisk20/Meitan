package com.meitan.lubov.flowtests;

import com.meitan.lubov.model.persistent.Image;
import com.meitan.lubov.model.persistent.Product;
import com.meitan.lubov.services.FileUploadHandlerTest;
import com.meitan.lubov.services.dao.ImageDao;
import com.meitan.lubov.services.util.DenisConversionService;
import com.meitan.lubov.services.util.FileBackupRestoreManager;
import com.meitan.lubov.services.util.FileUploadHandler;
import com.meitan.lubov.services.util.ImageIdGenerationServiceImpl;
import com.meitan.lubov.services.util.Utils;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.faces.model.OneSelectionTrackingListDataModel;
import org.springframework.webflow.config.FlowDefinitionResource;
import org.springframework.webflow.config.FlowDefinitionResourceFactory;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowBuilderContext;
import org.springframework.webflow.test.MockParameterMap;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.faces.model.DataModel;

import static org.junit.Assume.*;
import static org.hamcrest.CoreMatchers.*;
/**
 * @author denis_k
 *         Date: 18.06.2010
 *         Time: 18:24:46
 */
public class ImagesManagerFlowTest extends AbstractFlowIntegrationTest {
	@Autowired
	private ImageDao testImageDao;
	@Autowired
	private FileUploadHandler fileUploadHandler;
	@Autowired
	private Utils utils;

	private static final String URL_PARAM_NAME = "abc:url";
	private static final String MEITAN_HOST = "89.253.241.64";

	@Override
	protected FlowDefinitionResource getResource(FlowDefinitionResourceFactory resourceFactory) {
		return resourceFactory.createFileResource(rootPath + "/Web/src/main/webapp/WEB-INF/flows/imagesManager/imagesManager-flow.xml");
	}

	@Override
	protected void configureFlowBuilderContext(MockFlowBuilderContext builderContext) {
		super.configureFlowBuilderContext(builderContext);
		builderContext.registerBean("imageDao", testImageDao);
		builderContext.registerBean("utils", utils);
		builderContext.registerBean("imageIdGenerationService", new ImageIdGenerationServiceImpl());
		builderContext.registerBean("fileUploadHandler", fileUploadHandler);

		builderContext.getFlowBuilderServices().setConversionService(new DenisConversionService());
	}

	@Test(timeout = 10000L)
	public void testUpload() throws IOException {
		MockExternalContext context = new MockExternalContext(
				FileUploadHandlerTest.getParameterMapForFileUpload());
		final String uploadEventName = "upload";

		uploadAndAssert(context, uploadEventName);
	}

	@Test
	public void testUploadFromRemoteUrl() throws IOException {
		assumeThat(InetAddress.getByName(MEITAN_HOST).isReachable(3000), is(true));

		final String uploadEventName = "uploadFromUrl";
		final String remoteUrl = "http://" + MEITAN_HOST + "/bitrix/templates/catalog_template/images/foot.jpg";
		MockParameterMap parameterMap = new MockParameterMap();
		parameterMap.put(URL_PARAM_NAME, remoteUrl);

		uploadAndAssert(new MockExternalContext(parameterMap), uploadEventName);
	}

	@Test
	public void testUploadFromLocalUrl() throws IOException {
		final String localUrl = "file:///" + utils.getHomePath() + "/Web/src/test/resources/Toolbelt.jpg";
		final String uploadEventName = "uploadFromUrl";
		MockParameterMap parameterMap = new MockParameterMap();
		parameterMap.put(URL_PARAM_NAME, localUrl);

		uploadAndAssert(new MockExternalContext(parameterMap), uploadEventName);
	}
	@Test
	public void testDelete() throws IOException {
		Product product = testProductDao.findAll().get(0);
		Set<Image> startImages = (Set<Image>) new HashSet(product.getImages());

		int startImagesCount = startImages.size();
		MutableAttributeMap input = new LocalAttributeMap();
		input.put("imageAwareId", product.getId());
		input.put("className", product.getClass().getName());

		MockExternalContext context = new MockExternalContext();
		startFlow(input, context);

		OneSelectionTrackingListDataModel images = (OneSelectionTrackingListDataModel)
				getViewScope().getRequired("images", OneSelectionTrackingListDataModel.class);
		ArrayList<Image> startImagesArrayList = (ArrayList<Image>) images.getWrappedData();

		Image toDelete = startImagesArrayList.get(0);
		images.select(toDelete);

		String imagePath = utils.getImageUploadDirectoryPath() + toDelete.getUrl();
		FileBackupRestoreManager restoreManager = new FileBackupRestoreManager(imagePath);
		restoreManager.backup();

		context.setEventId("delete");
		try {
			resumeFlow(context);

			Product loaded = testProductDao.findById(product.getId());
			assertFalse("Image wasn't deleted properly", loaded.getImages().contains(toDelete));
			File imagePathFile = new File(imagePath);
			assertFalse("Image wasn't removed from disk properly", imagePathFile.exists());
			testProductDao.flush();
		} finally {
			restoreManager.restore();
		}
	}

	private void uploadAndAssert(MockExternalContext context, String uploadEventName) {
		Product product = testProductDao.findAll().get(0);
		Set<Image> startImages = (Set<Image>) new HashSet(product.getImages()).clone();

		int startImagesCount = startImages.size();
		MutableAttributeMap input = new LocalAttributeMap();
		input.put("imageAwareId", product.getId());
		input.put("className", product.getClass().getName());


		startFlow(input, context);

		assertCurrentStateEquals("imagesManager");

		DataModel viewScopeImages = (DataModel) getViewScope().getRequired("images");
		ArrayList<Image> viewScopeImageArrayList = (ArrayList<Image>) viewScopeImages.getWrappedData();
		assertEquals("Wrong images retrieved on flow startup", utils.asList(product.getImages()), viewScopeImageArrayList);

		context.setEventId(uploadEventName);

		Set<Image> images = null;
		try {
			resumeFlow(context);

			Product loaded = testProductDao.findById(product.getId());
			images = loaded.getImages();
			assertEquals("Wrong number of images", startImagesCount + 1, images.size());
		} finally {
			if (images != null) {
				images.removeAll(startImages);

				if (images.size() > 0) {
					Image addedImage = images.iterator().next();

					File addedImageFile = new File(utils.getImageUploadDirectoryPath() + addedImage.getUrl());
					assertTrue("Image file wasn't cleaned: " + addedImageFile, addedImageFile.delete());
				}
			}
		}
	}
}
