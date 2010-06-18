package com.meitan.lubov.flowtests;

import com.meitan.lubov.services.dao.CategoryDao;
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
import org.springframework.webflow.test.MockFlowBuilderContext;
import org.springframework.webflow.test.execution.AbstractXmlFlowExecutionTests;

import java.io.File;

/**
 * @author denis_k
 *         Date: 18.06.2010
 *         Time: 12:09:01
 */
public class CategoriesFlowTest extends AbstractFlowIntegrationTest {
	@Autowired
	private CategoryDao testCategoryDao;

	@Override
	protected FlowDefinitionResource getResource(FlowDefinitionResourceFactory resourceFactory) {
		return resourceFactory.createFileResource(System.getenv("MEITAN_HOME") + "/Web/src/main/webapp/WEB-INF/flows/categories/categories-flow.xml");
	}

	@Override
	protected void configureFlowBuilderContext(MockFlowBuilderContext builderContext) {
		builderContext.registerBean("categoryDao", testCategoryDao);
	}

	@Test
	public void testFlowStart() {

	}
}
