package com.meitan.lubov.flowtests;

import com.meitan.lubov.model.persistent.Category;
import com.meitan.lubov.model.persistent.Image;
import com.meitan.lubov.model.persistent.Product;
import com.meitan.lubov.services.FileUploadHandlerTest;
import com.meitan.lubov.services.MockFileUploadHandler;
import com.meitan.lubov.services.dao.CategoryDao;
import com.meitan.lubov.services.dao.ImageDao;
import com.meitan.lubov.services.dao.ProductDao;
import com.meitan.lubov.services.util.DenisConversionService;
import com.meitan.lubov.services.util.FileBackupRestoreManager;
import com.meitan.lubov.services.util.ImageIdGenerationServiceImpl;
import com.meitan.lubov.services.util.Utils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.faces.model.OneSelectionTrackingListDataModel;
import org.springframework.webflow.config.FlowDefinitionResource;
import org.springframework.webflow.config.FlowDefinitionResourceFactory;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowBuilderContext;

import javax.faces.model.DataModel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * @author denis_k
 *         Date: 18.06.2010
 *         Time: 18:24:46
 */
public class ImagesManagerFlowTest extends AbstractFlowIntegrationTest {
	@Autowired
	private ImageDao testImageDao;
	@Autowired
	private ProductDao testProductDao;
	private Utils utils = new Utils();

	@Override
	protected FlowDefinitionResource getResource(FlowDefinitionResourceFactory resourceFactory) {
		return resourceFactory.createFileResource(rootPath + "/Web/src/main/webapp/WEB-INF/flows/imagesManager/imagesManager-flow.xml");
	}

	@Override
	protected void configureFlowBuilderContext(MockFlowBuilderContext builderContext) {
		builderContext.registerBean("imageDao", testImageDao);
		builderContext.registerBean("utils", utils);
		builderContext.registerBean("imageIdGenerationService", new ImageIdGenerationServiceImpl());
		builderContext.registerBean("fileUploadHandler", new MockFileUploadHandler());

		builderContext.getFlowBuilderServices().setConversionService(new DenisConversionService());
	}

	@Test(timeout = 10000L)
	public void testFlowStart() throws IOException {
		Product product = testProductDao.findAll().get(0);
		Set<Image> startImages = (Set<Image>) new HashSet(product.getImages()).clone();

		int startImagesCount = startImages.size();
		MutableAttributeMap input = new LocalAttributeMap();
		input.put("imageAwareId", product.getId());
		input.put("className", product.getClass().getName());


		MockExternalContext context = new MockExternalContext(
				FileUploadHandlerTest.getParameterMapForFileUpload());

		startFlow(input, context);

		assertCurrentStateEquals("imagesManager");

		DataModel viewScopeImages = (DataModel) getViewScope().getRequired("images");
		ArrayList<Image> viewScopeImageArrayList = (ArrayList<Image>) viewScopeImages.getWrappedData();
		assertEquals("Wrong images retrieved on flow startup", utils.asList(product.getImages()), viewScopeImageArrayList);
		
		context.setEventId("upload");

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

					File addedImageFile = new File(rootPath + addedImage.getUrl());
					assertTrue("Image file wasn't cleaned: " + addedImageFile, addedImageFile.delete());
				}
			}
		}
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

		String imagePath = rootPath + toDelete.getUrl();
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

}
