package com.meitan.lubov.flowtests;

import com.meitan.lubov.model.persistent.BuyingAct;
import com.meitan.lubov.model.persistent.Client;
import com.meitan.lubov.services.dao.BuyingActDao;
import com.meitan.lubov.services.dao.ClientDao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.webflow.config.FlowDefinitionResource;
import org.springframework.webflow.config.FlowDefinitionResourceFactory;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowBuilderContext;

import java.util.HashSet;
import java.util.List;

/**
 * @author denis_k
 *         Date: 21.06.2010
 *         Time: 11:37:08
 */
public class ItemsListFlowTest extends AbstractFlowIntegrationTest{

	@Autowired
	private ClientDao testClientDao;

	@Autowired
	private BuyingActDao testBuyingActDao;

	@Override
	protected FlowDefinitionResource getResource(FlowDefinitionResourceFactory resourceFactory) {
		return resourceFactory.createFileResource(rootPath + "/Web/src/main/webapp/WEB-INF/flows/itemsList/itemsList-flow.xml");
	}

	@Override
	protected void configureFlowBuilderContext(MockFlowBuilderContext builderContext) {
		super.configureFlowBuilderContext(builderContext);

		builderContext.registerBean("utils", utils);
		builderContext.registerBean("clientDao", testClientDao);
		builderContext.registerBean("buyingActDao", testBuyingActDao);
	}

	@Test
	public void testGetItemsList() {
		List<Client> clientList = testClientDao.findAll();
		for (Client c : clientList) {
			assertForClient(c);
		}
	}

	private void assertForClient(Client c) {

		List<BuyingAct> acts = testBuyingActDao.findForLogin(c.getLogin());


		MockExternalContext context = new MockExternalContext();

		context.setCurrentUser(c.getLogin());
		startFlow(context);

		assertCurrentStateEquals("itemsList");

		List<BuyingAct> actualBoughts = (List<BuyingAct>) getViewScope().get("boughts");

		assertEquals(new HashSet(acts), new HashSet(actualBoughts));
	}
}