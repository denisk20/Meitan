package com.meitan.lubov.services.media;

import com.meitan.lubov.services.util.FileUploadHandler;
import com.meitan.lubov.services.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * Date: Aug 3, 2010
 * Time: 9:14:26 AM
 *
 * @author denisk
 */
@Service("imageManager")
@Repository
public class ImageManager {
	private static final int ICON_WIDTH = 100;
	private static final int ICON_HEIGHT = 100;

	@Autowired
	private Utils utils;
	//todo unit test
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

	//todo unit test

	public void uploadImage(MultipartFile file, File imageFile, int maxWidth, int maxHeight) throws IOException {
		uploadImageInternal(file.getInputStream(), file.getContentType(), imageFile, maxWidth, maxHeight);
	}

	public void uploadImage(URL url, File imageFile, int maxWidth, int maxHeight) throws IOException {
		URLConnection urlConnection;
		if (utils.shouldUseProxy()) {
			String hostname = utils.getProxyHost();
			int port = utils.getProxyPort();
			InetSocketAddress inetSocketAddress = new InetSocketAddress(hostname, port);
			Proxy proxy = new Proxy(Proxy.Type.HTTP, inetSocketAddress);
			urlConnection = url.openConnection(proxy);
		} else {
			urlConnection = url.openConnection();
		}
		String contentType = urlConnection.getContentType();
		uploadImageInternal(urlConnection.getInputStream(), contentType, imageFile, maxWidth, maxHeight);
	}

	private void uploadImageInternal(InputStream is, String contentType, File imageFile, int maxWidth, int maxHeight) throws IOException {
		BufferedImage sourceImage = ImageIO.read(is);
		int width = sourceImage.getWidth(null);
		int height = sourceImage.getHeight(null);
		//todo unit test
		if (resizeNeeded(width, height, maxWidth, maxHeight)) {
			boolean xPriority = isXPriority(width, height, maxWidth, maxHeight);
			double coef;
			if (xPriority) {
				coef = maxWidth / (width + 0.0);
			} else {
				coef = maxHeight / (height + 0.0);
			}
			int newWidth = (int) (width * coef);
			int newHeight = (int) (height * coef);

			java.awt.Image scaledImage = sourceImage
					.getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH);
			sourceImage = toBufferedImage(scaledImage);
		}

		int slash = contentType.indexOf("/");
		String trimmedContentType = contentType.substring(slash + 1);
		ImageIO.write(sourceImage, trimmedContentType, imageFile);

	}

	private boolean isXPriority(int width, int height, int maxWidth, int maxHeight) {
		return width - maxWidth > height - maxHeight;
	}

	private boolean resizeNeeded(int width, int height, int maxWidth, int maxHeight) {
		return width > maxWidth || height > maxHeight;
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

	public void paintIcon(OutputStream outputStream, String url) throws IOException {
		File f = new File(url);

		if (!f.exists()) {
			throw new IllegalStateException("Image doesn't exist: " + f);
		}
		BufferedImage bi = ImageIO.read(f);
		Image scaledImage = bi.getScaledInstance(ICON_WIDTH, ICON_HEIGHT, Image.SCALE_SMOOTH);

		ImageIO.write(toBufferedImage(scaledImage), "jpeg", outputStream);
	}

	public void paintImage(OutputStream outputStream, String url) throws IOException {
		File f = new File(url);

		if (!f.exists()) {
			throw new IllegalStateException("Image doesn't exist: " + f);
		}
		BufferedImage bi = ImageIO.read(f);
//		bi.coerceData(true);
		ImageIO.write(bi, "jpg", outputStream);
	}
}
