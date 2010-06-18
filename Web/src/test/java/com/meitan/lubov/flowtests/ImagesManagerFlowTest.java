package com.meitan.lubov.flowtests;

import com.meitan.lubov.model.persistent.Category;
import com.meitan.lubov.services.FileUploadHandlerTest;
import com.meitan.lubov.services.MockFileUploadHandler;
import com.meitan.lubov.services.dao.CategoryDao;
import com.meitan.lubov.services.dao.ImageDao;
import com.meitan.lubov.services.util.DenisConversionService;
import com.meitan.lubov.services.util.ImageIdGenerationServiceImpl;
import com.meitan.lubov.services.util.Utils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.webflow.config.FlowDefinitionResource;
import org.springframework.webflow.config.FlowDefinitionResourceFactory;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowBuilderContext;

import java.io.IOException;

/**
 * @author denis_k
 *         Date: 18.06.2010
 *         Time: 18:24:46
 */
public class ImagesManagerFlowTest extends AbstractFlowIntegrationTest {
	@Autowired
	private ImageDao testImageDao;
	@Autowired
	private CategoryDao testCategoryDao;

	@Override
	protected FlowDefinitionResource getResource(FlowDefinitionResourceFactory resourceFactory) {
		return resourceFactory.createFileResource(rootPath + "/Web/src/main/webapp/WEB-INF/flows/imagesManager/imagesManager-flow.xml");
	}

	@Override
	protected void configureFlowBuilderContext(MockFlowBuilderContext builderContext) {
		builderContext.registerBean("imageDao", testImageDao);
		builderContext.registerBean("utils", new Utils());
		builderContext.registerBean("imageIdGenerationService", new ImageIdGenerationServiceImpl());
		builderContext.registerBean("fileUploadHandler", new MockFileUploadHandler());

		builderContext.getFlowBuilderServices().setConversionService(new DenisConversionService());
	}

	@Test
	public void testFlowStart() throws IOException {
		Category category = testCategoryDao.findAll().get(0);
		MutableAttributeMap input = new LocalAttributeMap();
		input.put("imageAware", category);


		MockExternalContext context = new MockExternalContext(FileUploadHandlerTest.getParameterMapForFileUpload());

		startFlow(input, context);

		context.setEventId("upload");
		resumeFlow(context);
		
	}
}
