package com.meitan.lubov.services.util;

import com.meitan.lubov.model.NameAware;
import com.sun.facelets.el.TagMethodExpression;
import com.sun.facelets.tag.Location;
import com.sun.facelets.tag.TagAttribute;
import org.jboss.el.MethodExpressionLiteral;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.web.context.ServletContextAware;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;

/**
 * @author denis_k
 *         Date: 19.05.2010
 *         Time: 15:39:14
 */
//todo remove ServletContextAware
public class Utils implements ServletContextAware {
	private static final int STRING_LIMIT = 30;
	private static final String DOTS = "...";

	private ServletContext servletContext;

	public ServletContext getServletContext() {
		return servletContext;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public ArrayList asList(Set s) {
		if (s != null) {
			return new ArrayList(s);
		} else {
			return null;
		}
	}

	//todo move this into SecurityService 

	public String getMD5(String source) {
		PasswordEncoder passwordEncoder = new Md5PasswordEncoder();
		String pass = passwordEncoder.encodePassword(source, null);
		return pass;
	}

	public TagMethodExpression getMethodExpression(String expression) {
		Location location = new Location("myPath", 1, 1);
		TagAttribute attribute = new TagAttribute(location, "meitan", "custom", "custom", "no value");
		TagMethodExpression result = new TagMethodExpression(attribute, new MethodExpressionLiteral(expression, String.class, new Class[0]));

		return result;
	}

	public String getShortName(String longName) {
		String result = "";
		if (longName.length() > STRING_LIMIT) {
			result = longName.substring(0, STRING_LIMIT) + DOTS;
		} else {
			result = longName + DOTS;
		}
		return result;
	}

	public File getDestFile(String uploadDirName, String imageName) {
		String uploadedFolderPath = getServletContext().getRealPath(uploadDirName);
		File uploadDir = new File(uploadedFolderPath);
		if (!uploadDir.exists()) {
			throw new IllegalStateException("Upload directory doesn't exist: " + uploadDirName);
		}
		if (!uploadDir.isDirectory()) {
			throw new IllegalStateException("Upload directory is not a directory: " + uploadDirName);
		}
		//rename it
		File imageFile = new File(uploadDir, imageName);

		return imageFile;
	}

	public ArrayList<SelectItem> getSelectItems(List<NameAware> source) {
		if (source == null) {
			throw new IllegalArgumentException("Source for SelectItems was null");
		}
		ArrayList<SelectItem> result = new ArrayList<SelectItem>();
		for (NameAware nameAware : source) {
			SelectItem item = new SelectItem();
			item.setLabel(nameAware.getName());
			item.setValue(nameAware.getId());
			result.add(item);
		}
		return result;
	}
	public StringWrap createStringWrap() {
		return new StringWrap("Hello world");
	}
}
