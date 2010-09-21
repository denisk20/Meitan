package com.meitan.lubov.services.util;

import com.meitan.lubov.model.NameAware;
import com.sun.facelets.el.TagMethodExpression;
import com.sun.facelets.tag.Location;
import com.sun.facelets.tag.TagAttribute;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.el.MethodExpressionLiteral;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.web.context.ServletContextAware;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
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

	private final Log log = LogFactory.getLog(getClass());
	private static final String MEITAN_PROPS = "MEITAN_PROPS";
	private static final String COMMON_PROPERTIES = "common.properties";
	private static final String MEITAN_UPLOAD_FOLDER = "meitan.upload_folder";
	private static final String MEITAN_HOME = "MEITAN_HOME";

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

	public ArrayList<SelectItem> getSelectItems(List<NameAware> source, Long selectedId) {
		if (source == null) {
			throw new IllegalArgumentException("Source for SelectItems was null");
		}
		ArrayList<SelectItem> result = new ArrayList<SelectItem>();
		for (NameAware nameAware : source) {
			SelectItem item = new SelectItem();
			item.setLabel(nameAware.getName());
			Long id = nameAware.getId();
			item.setValue(id);
			if (selectedId!=null && id.equals(selectedId)) {
				item.setDisabled(false);
			}
			result.add(item);
		}
		return result;
	}

	public String getMeitanProperty(String name) {
		File pathToHibernateProperties = new File(System.getenv(MEITAN_PROPS) + "/" + COMMON_PROPERTIES);
		String property = null;
		FileInputStream in = null;
		try {
			in = new FileInputStream(pathToHibernateProperties);
		} catch (FileNotFoundException e) {
			log.warn("Can't find common.properties!");
		}
		Properties meitanProps = new Properties();
		try {
			meitanProps.load(in);
			property = (String) meitanProps.get(name);
		} catch (IOException e) {
			log.warn("Can't load common.properties!");
		}

		return property;

	}

	public String getImageUploadDirectoryPath() {
		return getMeitanProperty(MEITAN_UPLOAD_FOLDER);
	}
	public StringWrap createStringWrap() {
		return new StringWrap("Hello world");
	}

	//todo replace all MEITAN_HOME usages with this one
	public String getHomePath() {
		return System.getenv(MEITAN_HOME);
	}
}
