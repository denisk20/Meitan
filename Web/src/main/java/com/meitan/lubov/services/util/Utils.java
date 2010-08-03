package com.meitan.lubov.services.util;

import com.sun.facelets.el.TagMethodExpression;
import com.sun.facelets.tag.Location;
import com.sun.facelets.tag.TagAttribute;
import org.jboss.el.MethodExpressionLiteral;
import org.springframework.security.providers.encoding.Md5PasswordEncoder;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * @author denis_k
 *         Date: 19.05.2010
 *         Time: 15:39:14
 */
public class Utils {
	private static final int STRING_LIMIT = 30;
	private static final String DOTS = "...";

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

	// This method returns a buffered image with the contents of an image
	public BufferedImage toBufferedImage(Image image) {
		if (image instanceof BufferedImage) {
			return (BufferedImage) image;
		}

		// This code ensures that all the pixels in the image are loaded
		image = new ImageIcon(image).getImage();

		// Determine if the image has transparent pixels; for this method's
		// implementation, see Determining If an Image Has Transparent Pixels
		boolean hasAlpha = false;
		//boolean hasAlpha = hasAlpha(image);

		// Create a buffered image with a format that's compatible with the screen
		BufferedImage bimage = null;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		try {
			// Determine the type of transparency of the new buffered image
			int transparency = Transparency.OPAQUE;
			if (hasAlpha) {
				transparency = Transparency.BITMASK;
			}

			// Create the buffered image
			GraphicsDevice gs = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gs.getDefaultConfiguration();
			bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
		} catch (HeadlessException e) {
			// The system does not have a screen
		}

		if (bimage == null) {
			// Create a buffered image using the default color model
			int type = BufferedImage.TYPE_INT_RGB;
			if (hasAlpha) {
				type = BufferedImage.TYPE_INT_ARGB;
			}
			bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
		}

		// Copy image to buffered image
		Graphics g = bimage.createGraphics();

		// Paint the image onto the buffered image
		g.drawImage(image, 0, 0, null);
		g.dispose();

		return bimage;
	}
	// This method returns true if the specified image has transparent pixels

	public void uploadImage(MultipartFile file, File imageFile, int maxWidth, int maxHeight) throws IOException {
					BufferedImage sourceImage = ImageIO.read(file.getInputStream());
			int width = sourceImage.getWidth(null);
			int height = sourceImage.getHeight(null);
			//todo invent better algorithm
			if (width > maxWidth || height > maxHeight) {
				java.awt.Image scaledImage = sourceImage.getScaledInstance(maxWidth, maxHeight, java.awt.Image.SCALE_SMOOTH);
				sourceImage = toBufferedImage(scaledImage);
			}
			String contentType = file.getContentType();
			int slash = contentType.indexOf("/");
			String trimmedContentType = contentType.substring(slash + 1);
			ImageIO.write(sourceImage, trimmedContentType, imageFile);
	}

	public boolean hasAlpha(Image image) { // If buffered image, the color model is readily available
		if (image instanceof BufferedImage) {
			BufferedImage bimage = (BufferedImage) image;
			return bimage.getColorModel().hasAlpha();
		} // Use a pixel grabber to retrieve the image's color model; // grabbing a single pixel is usually sufficient
		PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
		} // Get the image's color model
		ColorModel cm = pg.getColorModel();
		return cm.hasAlpha();
	}
}
