package com.meitan.lubov.flowtests;

import com.meitan.lubov.model.persistent.Category;
import com.meitan.lubov.model.persistent.Product;
import com.meitan.lubov.services.dao.CategoryDao;
import com.meitan.lubov.services.util.DenisConversionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.webflow.config.FlowDefinitionResource;
import org.springframework.webflow.config.FlowDefinitionResourceFactory;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowBuilderContext;
import org.springframework.webflow.test.execution.AbstractXmlFlowExecutionTests;

import javax.faces.model.DataModel;
import java.io.File;
import java.util.ArrayList;

/**
 * @author denis_k
 *         Date: 18.06.2010
 *         Time: 10:54:25
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testsSetup.xml"})
@TestExecutionListeners({TransactionalTestExecutionListener.class, DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class})
@Transactional
public class AddCategoryFlowTest extends AbstractXmlFlowExecutionTests {

	@Autowired
	private CategoryDao testCategoryDao;

	@Override
	protected FlowDefinitionResource getResource(FlowDefinitionResourceFactory resourceFactory) {
		return resourceFactory.createFileResource(System.getenv("MEITAN_HOME") + "/Web/src/main/webapp/WEB-INF/flows/addCategory/addCategory-flow.xml");
	}

	@Override
	protected FlowDefinitionResource[] getModelResources(FlowDefinitionResourceFactory resourceFactory) {
		Resource globalFlowResource = new FileSystemResource(new File(System.getenv("MEITAN_HOME") + "/Web/src/main/webapp/WEB-INF/flows/global/global-flow.xml"));
		return new FlowDefinitionResource[] {
				new FlowDefinitionResource("global", globalFlowResource, null),
		};
	}

	@Override
	protected void configureFlowBuilderContext(MockFlowBuilderContext builderContext) {
		builderContext.registerBean("categoryDao", testCategoryDao);
//		builderContext.getFlowBuilderServices().setConversionService(new DenisConversionService());
	}

	@Test
	public void testFlowStart() {
		MockExternalContext context = new MockExternalContext();
		startFlow(context);

		assertCurrentStateEquals("addCategory");
		Category newCategory = (Category) getRequiredFlowAttribute("newCategory", Category.class);

		final String name = "myName";

		newCategory.setName(name);

		assertEquals("Wrong transient category ID :", new Long(0), newCategory.getId());

		context.setEventId("create");

		resumeFlow(context);

		assertFlowExecutionEnded();
		assertFlowExecutionOutcomeEquals("categoryCreated");
		String outputAttributeName = "createdCategoryName";
		String flowOutput = (String) getFlowExecutionOutcome().getOutput().get(outputAttributeName, String.class);
		assertNotNull("Flow output was null", flowOutput);
		assertEquals("Wrong flow output", "myName", flowOutput);
		
		assertNotNull(newCategory.getId());

		Category created = testCategoryDao.findById(newCategory.getId());
		assertNotNull("Category wasn't created", created);
		assertEquals(name, created.getName());
	}
}
