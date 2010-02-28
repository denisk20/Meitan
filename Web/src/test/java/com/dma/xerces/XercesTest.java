package com.dma.xerces;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Date: Feb 8, 2010
 * Time: 8:26:46 PM
 *
 * @author denisk
 */
public class XercesTest {
	public static void main(String[] args) throws ParserConfigurationException, TransformerException {
		/////////////////////////////
		//Creating an empty XML Document

		//We need a Document
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document doc = docBuilder.newDocument();

		////////////////////////
		//Creating the XML tree

		//create the root element and add it to the document
		Element root = doc.createElement("root");
		doc.appendChild(root);

		//create a comment and put it in the root element
		//create child element, add an attribute, and add to root
		Element child = doc.createElement("child");
		child.setAttribute("b", "value");
		child.setAttribute("a", "value");
		root.appendChild(child);


		/////////////////
		//Output the XML

		//set up a transformer
		TransformerFactory transfac = TransformerFactory.newInstance();
		Transformer trans = transfac.newTransformer();
		trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		trans.setOutputProperty(OutputKeys.INDENT, "yes");

		//create string from xml tree
		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		DOMSource source = new DOMSource(doc);
		trans.transform(source, result);
		String xmlString = sw.toString();

		System.out.println(xmlString);
	}
}
