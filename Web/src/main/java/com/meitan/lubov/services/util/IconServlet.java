package com.meitan.lubov.services.util;

import com.meitan.lubov.services.media.ImageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Date: Aug 5, 2010
 * Time: 12:24:01 AM
 *
 * @author denisk
 */
public class IconServlet extends HttpServlet {
	private static final int ICON_WIDTH = 100;
	private static final int ICON_HEIGHT = 100;

	@Autowired
	private ImageManager imageManager;

	@Override
	public void init() throws ServletException {
		ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
		ctx.getAutowireCapableBeanFactory().autowireBean(this);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String url = req.getPathInfo();
		imageManager.paint(resp.getOutputStream(), url);
	}
}
