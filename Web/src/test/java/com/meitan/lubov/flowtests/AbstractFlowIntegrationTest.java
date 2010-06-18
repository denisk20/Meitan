package com.meitan.lubov.flowtests;

import org.junit.runner.RunWith;
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
import org.springframework.webflow.test.execution.AbstractXmlFlowExecutionTests;

import java.io.File;

/**
 * @author denis_k
 *         Date: 18.06.2010
 *         Time: 12:14:01
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testsSetup.xml"})
@TestExecutionListeners({TransactionalTestExecutionListener.class, DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class})
@Transactional
public abstract class AbstractFlowIntegrationTest extends AbstractXmlFlowExecutionTests {

	@Override
	protected FlowDefinitionResource[] getModelResources(FlowDefinitionResourceFactory resourceFactory) {
		Resource globalFlowResource = new FileSystemResource(new File(System.getenv("MEITAN_HOME") + "/Web/src/main/webapp/WEB-INF/flows/global/global-flow.xml"));
		return new FlowDefinitionResource[] {
				new FlowDefinitionResource("global", globalFlowResource, null),
		};
	}
}
