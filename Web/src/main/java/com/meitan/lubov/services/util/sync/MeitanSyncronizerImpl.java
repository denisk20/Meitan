package com.meitan.lubov.services.util.sync;

import com.meitan.lubov.model.persistent.Category;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.DOMAttrImpl;
import org.w3c.tidy.DOMTextImpl;
import org.w3c.tidy.Tidy;
import org.xml.sax.InputSource;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
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
public class MeitanSyncronizerImpl {
	private final String MEITAN_URL = "http://meitan.ru";
	private final String MEITAN_PRODUCTS = "/catalog/products";
	private final String GOODS_URL = MEITAN_URL + MEITAN_PRODUCTS + "/main.php?SECTION_ID=144";

	private static final String CATALOG_XPATH = "/html/body/div[2]/div/table/tr/td[2]/div/div[2]/div";

	private String url = GOODS_URL;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void sync() throws IOException, XPathExpressionException {
		URL catalog = new URL(url);

        URLConnection urlConnection = catalog.openConnection();

		Tidy tidy = new Tidy();
		tidy.setInputEncoding("windows-1251");

		Document document = tidy.parseDOM(urlConnection.getInputStream(), null);

		Set<ParsedCategory> newCategories = getNewCategories(document);

		for (ParsedCategory c : newCategories) {
			createNewCategory(c);
			processItems(c);
		}
	}

	private void processItems(ParsedCategory c) {
	}

	private void createNewCategory(ParsedCategory c) {

	}

	protected Set<ParsedCategory> getNewCategories(Document document) throws XPathExpressionException, MalformedURLException, UnsupportedEncodingException {
		XPathFactory factory= XPathFactory.newInstance();
		XPath xPath=factory.newXPath();
		XPathExpression xPathExpression =
				xPath.compile(CATALOG_XPATH);
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
}
