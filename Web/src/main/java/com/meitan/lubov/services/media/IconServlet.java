package com.meitan.lubov.services.media;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Date: Aug 5, 2010
 * Time: 12:24:01 AM
 *
 * @author denisk
 */
public class IconServlet extends ImageServlet {

/*
	@Override
	public void init() throws ServletException {
		ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
		ctx.getAutowireCapableBeanFactory().autowireBean(this);
	}
*/

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String url = imageFolderPath + req.getPathInfo();
		imageManager.paintIcon(resp.getOutputStream(), url);
	}
}
