package com.meitan.lubov.services.util.sync;

import com.meitan.lubov.model.persistent.Category;
import com.meitan.lubov.model.persistent.Image;
import com.meitan.lubov.services.dao.CategoryDao;
import com.meitan.lubov.services.dao.ImageDao;
import com.meitan.lubov.services.media.ImageManager;
import com.meitan.lubov.services.util.FileUploadHandler;
import com.meitan.lubov.services.util.ImageIdGenerationService;
import com.meitan.lubov.services.util.StringWrap;
import com.meitan.lubov.services.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.DOMAttrImpl;
import org.w3c.tidy.DOMTextImpl;
import org.w3c.tidy.Tidy;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * Date: Jan 8, 2011
 * Time: 4:20:04 PM
 *
 * @author denisk
 */
@Service("meitanSyncronizer")
public class MeitanSyncronizerImpl implements MeitanSyncronizer {
	private final String MEITAN_URL = "http://meitan.ru";
	private final String MEITAN_PRODUCTS = "/catalog/products";
	private final String GOODS_URL = MEITAN_URL + MEITAN_PRODUCTS + "/main.php?SECTION_ID=144";

	private static final String CATALOG_XPATH = "/html/body/div[2]/div/table/tr/td[2]/div/div[2]/div";

	private String url = GOODS_URL;

	@Autowired
	private CategoryDao categoryDao;
	@Autowired
	private Utils utils;
	@Autowired
	private ImageIdGenerationService imageIdGenerationService;
	@Autowired
	protected ImageManager imageManager;
	@Autowired
	protected ImageDao imageDao;
	private static final XPathFactory FACTORY = XPathFactory.newInstance();
	private static final XPath X_PATH = FACTORY.newXPath();

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	@Transactional
	public void sync() throws IOException, XPathExpressionException {
		URL catalogUrl = new URL(url);

		Document document = parseHtml(catalogUrl);

		Set<ParsedCategory> categories = getCategories(document);

		for (ParsedCategory c : categories) {
			processCategory(c);
		}
	}

	private Document parseHtml(URL catalog) throws IOException {
		Tidy tidy = new Tidy();
		tidy.setInputEncoding("windows-1251");

		Document document = tidy.parseDOM(catalog.openStream(), null);
		return document;
	}

	private void processItems(ParsedCategory c) throws IOException, XPathExpressionException {
		Document itemsPage = parseHtml(c.getItemsUrl());
		final String xPathString = "//div[@class='catalog-section']/table/tr/td/table/tr[1]/td";
		XPathExpression expression = X_PATH.compile(xPathString);
		NodeList nodeList = (NodeList) expression.evaluate(itemsPage, XPathConstants.NODESET);
	}

	private void processCategory(ParsedCategory c) throws IOException, XPathExpressionException {
		if (!categoryExists(c)) {
			persistCategory(c);
		}
		processItems(c);
	}

	private void persistCategory(ParsedCategory c) throws IOException {
		Category category = c.getCategory();
		categoryDao.makePersistent(category);

		StringWrap imageName = imageIdGenerationService.generateIdForNextImage(category);
		File imageFile = new File(utils.getImageUploadDirectoryPath() + "/" + imageName.getWrapped());

		imageManager.uploadImage(c.getImageUrl(), imageFile, FileUploadHandler.MAX_WIDTH, FileUploadHandler.MAX_HEIGHT);

		Image image = new Image("/" + imageName.getWrapped());
		imageDao.addImageToEntity(category, image);
	}

	private boolean categoryExists(ParsedCategory c) {
		List<Category> fromDB = categoryDao.findByExample(c.getCategory(), "id", "description", "image");
		return fromDB.size() > 0;
	}

	protected Set<ParsedCategory> getCategories(Document document) throws XPathExpressionException, MalformedURLException, UnsupportedEncodingException {
		XPathExpression xPathExpression =
				X_PATH.compile(CATALOG_XPATH);
		Node categories = (Node) xPathExpression.evaluate(document, XPathConstants.NODE);

		NodeList childNodes = categories.getChildNodes();
		ParsedCategory parsedCategory = null;
		Category category = null;
		Set<ParsedCategory> parsedCategorySet = new HashSet<ParsedCategory>(childNodes.getLength());
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			String name = node.getLocalName();
			if (name.equals("img")) {
				if (parsedCategory != null) {
					parsedCategorySet.add(parsedCategory);
				}
				parsedCategory = new ParsedCategory();
				category = new Category();
				parsedCategory.setCategory(category);

				String imageUrlSuffix = ((DOMAttrImpl) node.getAttributes().item(0)).getValue();
				String imageUrl = MEITAN_URL + imageUrlSuffix;
				parsedCategory.setImageUrl(new URL(imageUrl));
			} else if (name.equals("a")) {
				String itemsUrlSuffix =
						((DOMAttrImpl) node.getAttributes().getNamedItem("href")).getValue();
				String itemsUrl = MEITAN_URL + MEITAN_PRODUCTS + "/" + itemsUrlSuffix;
				parsedCategory.setItemsUrl(new URL(itemsUrl));
				String categoryName = ((DOMTextImpl) node.getChildNodes().item(0)).getData();
				category.setName(categoryName);
			} else if(node.getLocalName().equals("#text")) {
				String description = node.getNodeValue();
				if (description.length() > 1) {
					category.setDescription(description);
				}
			}
		}
		parsedCategorySet.add(parsedCategory);

		return parsedCategorySet;
	}

	@Override
	public CategoryDao getCategoryDao() {
		return categoryDao;
	}

	@Override
	public void setCategoryDao(CategoryDao categoryDao) {
		this.categoryDao = categoryDao;
	}

	@Override
	public Utils getUtils() {
		return utils;
	}

	@Override
	public void setUtils(Utils utils) {
		this.utils = utils;
	}

	@Override
	public ImageIdGenerationService getImageIdGenerationService() {
		return imageIdGenerationService;
	}

	@Override
	public void setImageIdGenerationService(ImageIdGenerationService imageIdGenerationService) {
		this.imageIdGenerationService = imageIdGenerationService;
	}

	@Override
	public ImageManager getImageManager() {
		return imageManager;
	}

	@Override
	public void setImageManager(ImageManager imageManager) {
		this.imageManager = imageManager;
	}

	public ImageDao getImageDao() {
		return imageDao;
	}

	public void setImageDao(ImageDao imageDao) {
		this.imageDao = imageDao;
	}
}
