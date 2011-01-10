package com.meitan.lubov.services.util.sync;

import com.meitan.lubov.services.dao.CategoryDao;
import com.meitan.lubov.services.media.ImageManager;
import com.meitan.lubov.services.util.ImageIdGenerationService;
import com.meitan.lubov.services.util.Utils;

import java.io.IOException;
import javax.xml.xpath.XPathExpressionException;

/**
 * Date: Jan 9, 2011
 * Time: 1:28:32 PM
 *
 * @author denisk
 */
public interface MeitanSyncronizer {
	String getUrl();

	void setUrl(String url);

	void sync() throws IOException, XPathExpressionException;

	CategoryDao getCategoryDao();

	void setCategoryDao(CategoryDao categoryDao);

	Utils getUtils();

	void setUtils(Utils utils);

	ImageIdGenerationService getImageIdGenerationService();

	void setImageIdGenerationService(ImageIdGenerationService imageIdGenerationService);

	ImageManager getImageManager();

	void setImageManager(ImageManager imageManager);

	String getPrefixUrl();

	void setPrefixUrl(String prefixUrl);

	String getProductsUrl();

	void setProductsUrl(String productsUrl);

	String getFilePrefix();

	void setFilePrefix(String filePrefix);
}
