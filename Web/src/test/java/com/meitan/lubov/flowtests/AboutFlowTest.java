package com.meitan.lubov.flowtests;

import com.meitan.lubov.model.persistent.BoardItem;
import com.meitan.lubov.model.persistent.NewsBoard;
import com.meitan.lubov.model.persistent.Product;
import com.meitan.lubov.services.dao.BoardItemDao;
import com.meitan.lubov.services.dao.NewsBoardDao;
import com.meitan.lubov.services.dao.ProductDao;
import com.meitan.lubov.services.util.DenisConversionService;
import com.meitan.lubov.services.util.FileUploadHandler;
import com.meitan.lubov.services.util.Utils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.faces.model.OneSelectionTrackingListDataModel;
import org.springframework.webflow.config.FlowDefinitionResource;
import org.springframework.webflow.config.FlowDefinitionResourceFactory;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowBuilderContext;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author denis_k
 *         Date: 16.06.2010
 *         Time: 17:59:34
 */
public class AboutFlowTest extends AbstractFlowIntegrationTest {
	@Autowired
	private ProductDao testProductDao;

	@Autowired
	private NewsBoardDao testNewsBoardDao;
	@Autowired
	private BoardItemDao testBoardItemDao;
	
	private FileUploadHandler fileUploadHandler = new FileUploadHandler();

	@Override
	protected FlowDefinitionResource getResource(FlowDefinitionResourceFactory resourceFactory) {
		return resourceFactory.createFileResource(rootPath + "/Web/src/main/webapp/WEB-INF/flows/about/about-flow.xml");
	}

	@Override
	protected FlowDefinitionResource[] getModelResources(FlowDefinitionResourceFactory resourceFactory) {
		FlowDefinitionResource[] superResources = super.getModelResources(resourceFactory);

		ArrayList<FlowDefinitionResource> result = new ArrayList<FlowDefinitionResource>();

		result.addAll(Arrays.asList(superResources));

		Resource localFlowResource1 = new FileSystemResource(new File(rootPath + "/Web/src/main/webapp/WEB-INF/flows/abstractBoard/abstractBoard-flow.xml"));
		Resource localFlowResource2 = new FileSystemResource(new File(rootPath + "/Web/src/main/webapp/WEB-INF/flows/baseGood/baseGood-flow.xml"));
		Resource localFlowResource3 = new FileSystemResource(new File(rootPath + "/Web/src/main/webapp/WEB-INF/flows/abstractEditable/abstractEditable-flow.xml"));
		result.add(new FlowDefinitionResource("abstractBoard", localFlowResource1, null));
		result.add(new FlowDefinitionResource("baseGood", localFlowResource2, null));
		result.add(new FlowDefinitionResource("abstractEditable", localFlowResource3, null));

		return result.toArray(new FlowDefinitionResource[0]);
	}

	@Override
	protected void configureFlowBuilderContext(MockFlowBuilderContext builderContext) {
		super.configureFlowBuilderContext(builderContext);
		builderContext.registerBean("productDao", testProductDao);
		builderContext.registerBean("newsBoardDao", testNewsBoardDao);
		builderContext.registerBean("fileUploadHandler", fileUploadHandler);
		builderContext.registerBean("boardItemDao", testBoardItemDao);
		builderContext.registerBean("utils", new Utils());
		builderContext.getFlowBuilderServices().setConversionService(new DenisConversionService());
	}

	@Test
	public void testStartAboutFlow() {
		MockExternalContext context = new MockExternalContext();
		startFlow(context);

		assertCurrentStateEquals("aboutUs");
		ArrayList<Product> newProducts = (ArrayList<Product>) getRequiredViewAttribute("newProducts");
		assertEquals("Wrong number of products", 1, newProducts.size());
	}

	@Test
	public void testNavigateToGood() {
		MockExternalContext context = new MockExternalContext();
		startFlow(context);
		assertCurrentStateEquals("aboutUs");
		context.setEventId("select");
		Product p = new Product("some product");
		p.setId(1L);
		ArrayList<Product> list = new ArrayList<Product>();
		list.add(p);
		OneSelectionTrackingListDataModel trackingListDataModel = new OneSelectionTrackingListDataModel(list);
		trackingListDataModel.select(p);
		getFlowScope().put("newProducts", trackingListDataModel);

		resumeFlow(context);

		assertFlowExecutionEnded();
		assertFlowExecutionOutcomeEquals("selectFlow");
	}

	/*Here goes tests for abstractBoard. I wonder if they can be cleanly moved to
		separate test class
	*/

	@Test
	public void testBoardItems() {
		MockExternalContext context = new MockExternalContext();
		startFlow(context);

		final int expectedNewsCount = 2;

		NewsBoard board = (NewsBoard) getFlowAttribute("board");
		assertEquals(expectedNewsCount, board.getItems().size());

		BoardItem newItem = (BoardItem) getViewAttribute("newNews");
		assertNotNull(newItem);
		OneSelectionTrackingListDataModel boardItems =
				(OneSelectionTrackingListDataModel) getViewAttribute("boardItems");
		final Object data = boardItems.getWrappedData();
		List<BoardItem> items = (List<BoardItem>) data;
		assertEquals(expectedNewsCount, items.size());
	}

	@Test
	public void testModifyNews() {
		MockExternalContext context = new MockExternalContext();
		startFlow(context);

		OneSelectionTrackingListDataModel boardItems =
				(OneSelectionTrackingListDataModel) getViewAttribute("boardItems");
		final Object data = boardItems.getWrappedData();
		List<BoardItem> items = (List<BoardItem>) data;
		BoardItem item = items.get(0);
		boardItems.select(item);

		final String content = "this is test context";
		item.setContent(content);

		context.setEventId("saveNews");
		resumeFlow(context);

		BoardItem loadedItem = testBoardItemDao.findById(item.getId());
		assertEquals(content, loadedItem.getContent());
	}

	@Test
	public void testCreateNews() {
		MockExternalContext context = new MockExternalContext();
		startFlow(context);

		OneSelectionTrackingListDataModel boardItems =
				(OneSelectionTrackingListDataModel) getViewAttribute("boardItems");
		final Object data = boardItems.getWrappedData();
		List<BoardItem> items = (List<BoardItem>) data;
		final int initialNewsCount = items.size();


		BoardItem newItem = (BoardItem) getViewAttribute("newNews");
		final String content = "hello";
		newItem.setContent(content);

		context.setEventId("createNews");
		resumeFlow(context);

		NewsBoard board = (NewsBoard) getFlowAttribute("board");
		List<BoardItem> newItems = testNewsBoardDao.getItemsForBoard(board.getBoardType());
		
		assertEquals(initialNewsCount + 1, newItems.size());

		newItems.removeAll(items);
		assertEquals(content, newItems.get(0).getContent());
	}

	@Test
	public void testDeleteNews() {
		MockExternalContext context = new MockExternalContext();
		startFlow(context);

		OneSelectionTrackingListDataModel boardItems =
				(OneSelectionTrackingListDataModel) getViewAttribute("boardItems");
		final Object data = boardItems.getWrappedData();
		List<BoardItem> items = (List<BoardItem>) data;
		BoardItem item = items.get(0);
		final int initialNewsCount = items.size();

		boardItems.select(item);

		context.setEventId("deleteNews");
		resumeFlow(context);
		
		NewsBoard board = (NewsBoard) getFlowAttribute("board");
		List<BoardItem> newItems = testNewsBoardDao.getItemsForBoard(board.getBoardType());

		assertEquals(initialNewsCount - 1, newItems.size());

		assertFalse(newItems.contains(item));
	}
}
