package com.meitan.lubov;

import com.meitan.lubov.model.persistent.Image;
import com.meitan.lubov.services.dao.Dao;
import com.meitan.lubov.services.dao.ImageDao;
import com.meitan.lubov.services.util.FileBackupRestoreManager;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

/**
 * @author denis_k
 *         Date: 13.06.2010
 *         Time: 20:40:39
 */
public class ImageIntegrationTest extends GenericIntegrationTest<Image>{
    @Autowired
    private ImageDao testImageDao;

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
        final String imagePath = testImageDao.getPathPrefix() + i.getUrl();
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
}
