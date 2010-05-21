package com.meitan.lubov.services.util;

import com.meitan.lubov.model.persistent.Image;
import com.meitan.lubov.services.dao.ImageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author denis_k
 *         Date: 20.05.2010
 *         Time: 13:33:11
 */
public class TempImagesService implements Serializable {
	//no one knows it!
	private static final String UPLOAD_DIR_NAME = "uploaded";
	//transient images that need to be persisted
	private HashSet<Image> toPersist = new HashSet<Image>();
	//transient images that need to be discarded
	private HashSet<Image> toDiscard = new HashSet<Image>();
	//persistent images that need to be deleted
	private HashSet<Image> toDelete = new HashSet<Image>();

	@Autowired
	private transient ImageDao imageDao;

	public void persistImages() throws IOException {
		persistImages(toPersist);
	}

	public void discardImages() {
		discardImages(toPersist);
	}

	public void deleteImages() {
		discardImages(toDelete);
	}
	public void addToPersistQueue(Image i) {
		toPersist.add(i);
	}

	public void addToDiscardQueue(Image i) {
		toDiscard.add(i);
		if (imageDao.isAttached(i)) {
			toDelete.add(i);
		}
	}

	public void removeFromPersistQueue(Image i) {
		toPersist.remove(i);

	}

	public void removeFromDiscardQueue(Image i) {
		toDiscard.remove(i);
	}

	public void addToDeleteQueue(Image i) {
		toDelete.add(i);
	}

	public void removeFromDeleteQueue(Image i) {
		toDelete.remove(i);
	}

	public void revertDeletedImages(Collection<Image> images) {
		
	}

	private void persistImages(Collection<Image> images) throws IOException {
		for (Image i : images) {
			String tempPath = i.getAbsolutePath();
			String persistentPath = convertToPersistentPath(tempPath);
			File tempImage = new File(tempPath);
			if (!tempImage.exists()) {
				throw new IllegalArgumentException("No corresponding file for image " + i);
			}
			FileInputStream in = null;
			FileOutputStream out = null;
			try {
				in = new FileInputStream(tempImage);
				out = new FileOutputStream(persistentPath);

				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
			} finally {
				in.close();
				out.close();
			}
			//delete image from tmp directory
			deleteImageFromDisk(i);
			i.setAbsolutePath(persistentPath);
			i.setUrl(convertToPersistentPath(i.getUrl()));
		}
	}

	private String convertToPersistentPath(String tempPath) {
		return tempPath.replace(FileUploadHandler.TEMP_DIR_NAME, UPLOAD_DIR_NAME);
	}

	private void discardImages(Collection<Image> images) {
		for (Image i : images) {
			if (imageDao.isAttached(i)) {
				imageDao.makeTransient(i);
			}
			deleteImageFromDisk(i);
		}
	}

	private void deleteImageFromDisk(Image i) {
		imageDao.deleteFromDisk(i);
	}
}
