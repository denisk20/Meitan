package com.meitan.lubov;

import com.meitan.lubov.model.persistent.Image;
import com.meitan.lubov.model.persistent.Product;
import com.meitan.lubov.services.dao.Dao;
import com.meitan.lubov.services.dao.ImageDao;
import com.meitan.lubov.services.dao.ProductDao;
import com.meitan.lubov.services.util.FileBackupRestoreManager;
import com.meitan.lubov.services.util.Utils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @author denis_k
 *         Date: 13.06.2010
 *         Time: 20:40:39
 */
public class ImageIntegrationTest extends GenericIntegrationTest<Image>{
    @Autowired
    private ImageDao testImageDao;
    @Autowired
    private ProductDao testProductDao;
	@Autowired
	private Utils utils;

	@Override
    protected void setUpBeanNames() {
        beanNames.add("ent_creamsImage");
        beanNames.add("ent_megaCreamImageFront");
        beanNames.add("ent_megaCreamImageProfile");
        beanNames.add("ent_gigaCreamImageFront");
        beanNames.add("ent_gigaCreamImageProfile");
    }

    @Override
    protected Dao<Image, Long> getDAO() {
        return testImageDao;
    }

    @Test
    public void testDeleteFromDisk() throws IOException {
        Image i = beansFromXml.get(0);
        final String imagePath = utils.getImageUploadDirectoryPath() + i.getUrl();
        FileBackupRestoreManager restoreManager =
                new FileBackupRestoreManager(
                        imagePath);

        restoreManager.backup();

        File imageFile = null;
        try {
            testImageDao.deleteFromDisk(i);

            imageFile = new File(imagePath);
            assertFalse("file wasn't deleted properly for image " + i, imageFile.exists());
        } finally {
            restoreManager.restore();
            assertTrue("Failed to restore image " + restoreManager.getBasePath(), imageFile.exists());
        }
    }

    @Test
    public void testAddImageToEntity() {
        Product megaCream = (Product) applicationContext.getBean("ent_megaCream");

        List<Product> products = testProductDao.findByExample(megaCream);
        assertEquals("Wrong number of products fetched", 1, products.size());
        Product dbMegaCream = products.get(0);
        int startImagesCount = dbMegaCream.getImages().size();

        Image creamImage = (Image) applicationContext.getBean("ent_creamsImage");
        testImageDao.addImageToEntity(dbMegaCream, creamImage);

        //refresh
        dbMegaCream = testProductDao.findById(dbMegaCream.getId());

        Set<Image> newImages = dbMegaCream.getImages();
        assertEquals("Image wasn't added properly. Wrong number of images",
                startImagesCount + 1, newImages.size());

        boolean found = false;
        for (Image i : newImages) {
            if (i.equals(creamImage)) {
                found = true;
                break;
            }
        }
        assertTrue("Image not found", found);
    }

    @Test
    public void testRemoveFromEntity() throws IOException {
        Product megaCream = (Product) applicationContext.getBean("ent_megaCream");

        List<Product> products = testProductDao.findByExample(megaCream);
        assertEquals("Wrong number of products fetched", 1, products.size());
        Product dbMegaCream = products.get(0);

        int startImagesCount = dbMegaCream.getImages().size();

        Image image = dbMegaCream.getImages().iterator().next();
        String imagePath = utils.getImageUploadDirectoryPath() + image.getUrl();
        FileBackupRestoreManager restoreManager = new FileBackupRestoreManager(imagePath);

        restoreManager.backup();
        try {
            testImageDao.removeImageFromEntity(dbMegaCream, image);
            dbMegaCream = testProductDao.findById(dbMegaCream.getId());

            Set<Image> newImages = dbMegaCream.getImages();
            assertEquals("Image wasn't removed properly", startImagesCount - 1, newImages.size());
            File imageFile = new File(imagePath);
            assertFalse("Image file wasn't deleted properly for image " + image, imageFile.exists());

            boolean found = false;
            for (Image i : newImages) {
                if (i.equals(image)) {
                    found = true;
                    break;
                }
            }

            assertFalse("Image was removed from disk, but still exists inside entity: " + image, found);

            Image loadedImage = testImageDao.findById(image.getId());
            assertNull("Image still exists in DB. It was removed from disk and from entity: " + image, loadedImage);
        } finally {
            restoreManager.restore();
        }
    }
}
