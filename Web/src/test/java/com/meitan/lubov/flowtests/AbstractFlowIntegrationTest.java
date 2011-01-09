package com.meitan.lubov.flowtests;

import com.meitan.lubov.services.dao.ProductDao;
import com.meitan.lubov.services.util.MenuBackgroundService;
import com.meitan.lubov.services.util.Utils;
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
import java.util.ArrayList;

/**
 * @author denis_k
 *         Date: 18.06.2010
 *         Time: 12:14:01
 */
@SuppressWarnings("unchecked")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testsSetup.xml"})
@TestExecutionListeners({TransactionalTestExecutionListener.class, DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class})
@Transactional
public abstract class AbstractFlowIntegrationTest extends AbstractXmlFlowExecutionTests {

	@Autowired
	protected Utils utils;
	
	@Autowired
	protected ProductDao testProductDao;

	protected String rootPath = System.getenv("MEITAN_HOME");

	@Override
	protected void configureFlowBuilderContext(MockFlowBuilderContext builderContext) {
		builderContext.registerBean("menuBackgroundService", new MenuBackgroundService());
		builderContext.registerBean("productDao", testProductDao);
	}

	@Override
	protected FlowDefinitionResource[] getModelResources(FlowDefinitionResourceFactory resourceFactory) {
		ArrayList<FlowDefinitionResource> result = new ArrayList<FlowDefinitionResource>();

		Resource localFlowResource3 = new FileSystemResource(new File(rootPath + "/Web/src/main/webapp/WEB-INF/flows/global/global-flow.xml"));
		Resource localFlowResource1 = new FileSystemResource(new File(rootPath + "/Web/src/main/webapp/WEB-INF/flows/baseGood/baseGood-flow.xml"));
		Resource localFlowResource2 = new FileSystemResource(new File(rootPath + "/Web/src/main/webapp/WEB-INF/flows/abstractEditable/abstractEditable-flow.xml"));
		result.add(new FlowDefinitionResource("baseGood", localFlowResource1, null));
		result.add(new FlowDefinitionResource("abstractEditable", localFlowResource2, null));
		result.add(new FlowDefinitionResource("global", localFlowResource3, null));

		return result.toArray(new FlowDefinitionResource[0]);

	}
}
