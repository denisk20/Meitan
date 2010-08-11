package com.meitan.lubov.services.media;

import com.meitan.lubov.services.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Date: Aug 11, 2010
 * Time: 11:57:19 AM
 *
 * @author denisk
 */
public class ImageServlet extends HttpServlet {
	@Autowired
	private Utils utils;
	@Autowired
	protected ImageManager imageManager;


	protected String imageFolderPath;

	@Override
	public void init() throws ServletException {
		ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
		ctx.getAutowireCapableBeanFactory().autowireBean(this);

		if (utils == null) {
			throw new IllegalStateException("Utils wasn't set properly");
		}
		imageFolderPath = utils.getImageUploadDirectoryPath();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String url = imageFolderPath + request.getPathInfo();
		imageManager.paintImage(response.getOutputStream(), url);
	}
}
