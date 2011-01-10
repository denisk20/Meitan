package com.meitan.lubov.services.util.sync;

import com.meitan.lubov.model.ImageAware;
import com.meitan.lubov.model.persistent.Category;
import com.meitan.lubov.model.persistent.Image;
import com.meitan.lubov.model.persistent.Product;
import com.meitan.lubov.services.dao.CategoryDao;
import com.meitan.lubov.services.dao.ImageDao;
import com.meitan.lubov.services.dao.ProductDao;
import com.meitan.lubov.services.media.ImageManager;
import com.meitan.lubov.services.util.FileUploadHandler;
import com.meitan.lubov.services.util.ImageIdGenerationService;
import com.meitan.lubov.services.util.StringWrap;
import com.meitan.lubov.services.util.Utils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.DOMAttrImpl;
import org.w3c.tidy.DOMTextImpl;
import org.w3c.tidy.Tidy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
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
	private final Log log = LogFactory.getLog(getClass());
	private final String MEITAN_URL = "http://meitan.ru";
	private final String MEITAN_PRODUCTS = "/catalog/products/";
	private final String GOODS_URL = MEITAN_URL + MEITAN_PRODUCTS + "main.php?SECTION_ID=144";
	private final String FILE_PREFIX = "file://";

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
	@Autowired
	protected ProductDao productDao;
	private static final XPathFactory FACTORY = XPathFactory.newInstance();
	private static final XPath X_PATH = FACTORY.newXPath();
	private String prefixUrl = MEITAN_URL;
	private String fullPrefixUrl = MEITAN_URL + MEITAN_PRODUCTS;
	private String productsUrl = MEITAN_PRODUCTS;
	private String filePrefix = "";
	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String getPrefixUrl() {
		return prefixUrl;
	}

	@Override
	public void setPrefixUrl(String prefixUrl) {
		this.prefixUrl = prefixUrl;
	}

	@Override
	public String getProductsUrl() {
		return productsUrl;
	}

	@Override
	public void setProductsUrl(String productsUrl) {
		this.productsUrl = productsUrl;
	}

	@Override
	public String getFilePrefix() {
		return filePrefix;
	}

	@Override
	public void setFilePrefix(String filePrefix) {
		this.filePrefix = filePrefix;
	}

	@Override
	public String getFullPrefixUrl() {
		return fullPrefixUrl;
	}

	@Override
	public void setFullPrefixUrl(String fullPrefixUrl) {
		this.fullPrefixUrl = fullPrefixUrl;
	}

	@Override
	@Transactional
	public void sync() throws IOException, XPathExpressionException {
		URL catalogUrl = new URL(url);

		Document document = parseHtml(catalogUrl);

		Set<ParsedCategory> categories = getCategories(document);

		for (ParsedCategory c : categories) {
			try {
				processCategory(c);
			} catch (StringIndexOutOfBoundsException e) {
				log.error("Cant't parse category " + c.getCategory().getName(), e);
			}
		}
	}

	private Document parseHtml(URL catalog) throws IOException {
		Tidy tidy = new Tidy();
		tidy.setInputEncoding("windows-1251");

		Document document = tidy.parseDOM(utils.getURLConnection(catalog).getInputStream(), null);
		return document;
	}

	private void processItems(ParsedCategory c) throws IOException, XPathExpressionException {
		try {
			Document page = parseHtml(c.getItemsUrl());
			Category category = c.getCategory();
			assembleCategory(page, category);
			//handle other pages
			final String paginatorXPath = "//div[@id='pagenator']/a";
			XPathExpression expression = X_PATH.compile(paginatorXPath);
			NodeList otherPages = (NodeList) expression.evaluate(page, XPathConstants.NODESET);
			//we don't need the last one, this is usually 'далее'
			for (int i = 0; i < otherPages.getLength() - 1; i++) {
				Node a = otherPages.item(i);
				String address = a.getAttributes().getNamedItem("href").getNodeValue();

				Document nextPage = parseHtml(new URL(prefixUrl + filePrefix + address));
				assembleCategory(nextPage, category);
			}
		} catch (IOException e) {
			log.error("can't process category " + c.getCategory(), e);
		}
	}

	private void assembleCategory(Document itemsPage, Category category) throws IOException, XPathExpressionException {
		final String xPathString = "//div[@class='catalog-section']/table/tr/td/table/tr[1]/td";
		XPathExpression expression = X_PATH.compile(xPathString);
		NodeList nodeList = (NodeList) expression.evaluate(itemsPage, XPathConstants.NODESET);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			//<a href...
			Node a = node.getChildNodes().item(0);
			String itemAddress = a.getAttributes().getNamedItem("href").getNodeValue();

			processProduct(category, fullPrefixUrl + itemAddress);
		}
	}

	private void processProduct(Category category, String itemAddress) throws IOException, XPathExpressionException {
		final String nameXPath = "//ul[@class='breadcrumb-navigation']/li[last()]";
		final String imageXPath = "//div[@class='catalog-element']/table/tr/td[1]/img";
		final String descriptionXPath = "//div[@class='catalog-element']/div[1]";

		Document page = null;
		try {
			page = parseHtml(new URL(filePrefix + itemAddress));
		} catch (IOException e) {
			log.error("Can't process product at " + itemAddress, e);
		}

		XPathExpression nameExpression = X_PATH.compile(nameXPath);
		XPathExpression imageExpression = X_PATH.compile(imageXPath);
		XPathExpression descriptionExpression = X_PATH.compile(descriptionXPath);

		Node nameNode = (Node) nameExpression.evaluate(page, XPathConstants.NODE);
		Node imageNode = (Node) imageExpression.evaluate(page, XPathConstants.NODE);
		Node descriptionNode = (Node) descriptionExpression.evaluate(page, XPathConstants.NODE);

		String name = nameNode.getFirstChild().getNodeValue();
		String imageAddress = prefixUrl + imageNode.getAttributes().getNamedItem("src").getNodeValue();
		String description = descriptionNode.getFirstChild().getNodeValue();

		if (!productExists(name)) {
			Product p = new Product(name);
			p.setDescription(description);

			productDao.makePersistent(p);
			addImageToEntity(new URL(filePrefix + imageAddress), p);
			category.getProducts().add(p);
			p.getCategories().add(category);
			final Set<Image> images = p.getImages();
			if (images.size() > 0) {
				p.setAvatar(images.iterator().next());
			}
		}
	}

	private void processCategory(ParsedCategory c) throws IOException, XPathExpressionException {
		if (!categoryExists(c.getCategory().getName())) {
			persistCategory(c);
		}
		processItems(c);
	}

	private void persistCategory(ParsedCategory c) throws IOException {
		Category category = c.getCategory();
		categoryDao.makePersistent(category);

		addImageToEntity(c.getImageUrl(), category);
	}

	private void addImageToEntity(URL imageUrl, ImageAware entity) throws IOException {
		StringWrap imageName = imageIdGenerationService.generateIdForNextImage(entity);
		File imageFile = new File(utils.getImageUploadDirectoryPath() + "/" + imageName.getWrapped());

		try {
			imageManager.uploadImage(imageUrl, imageFile, FileUploadHandler.MAX_WIDTH, FileUploadHandler.MAX_HEIGHT);

			Image image = new Image("/" + imageName.getWrapped());
			imageDao.makePersistent(image);
			imageDao.addImageToEntity(entity, image);
		} catch (FileNotFoundException e) {
			log.error("Can't find file " + imageUrl, e);
		}
	}

	private boolean categoryExists(String name) {
		return categoryDao.getCategoryByName(name) != null;
	}

	private boolean productExists(String name) {
		return productDao.getProductByName(name) != null;
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

				//todo can this be simpler?
				String imageUrlSuffix = node.getAttributes().item(0).getNodeValue();
				String imageUrl = prefixUrl + imageUrlSuffix;
				parsedCategory.setImageUrl(new URL(filePrefix + imageUrl));
			} else if (name.equals("a")) {
				String itemsUrlSuffix =
						node.getAttributes().getNamedItem("href").getNodeValue();
				String itemsUrl = prefixUrl + productsUrl + itemsUrlSuffix;
				parsedCategory.setItemsUrl(new URL(filePrefix + itemsUrl));
				String categoryName = node.getChildNodes().item(0).getNodeValue();
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
