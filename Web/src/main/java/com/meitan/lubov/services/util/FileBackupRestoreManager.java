package com.meitan.lubov.services.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Date: May 31, 2010
 * Time: 3:54:47 PM
 *
 * @author denisk
 */
public class FileBackupRestoreManager {
	private String basePath;
	private File tempFile;

	public FileBackupRestoreManager(String basePath) {
		this.basePath = basePath;
	}

	public String getBasePath() {
		return basePath;
	}

	public void backup() throws IOException {
		File file = new File(basePath);
		if (! file.exists()) {
			throw new IllegalStateException("No file for path " + basePath);
		}

		tempFile = File.createTempFile("meitan_", "_tempFile");
		transferTo(file, tempFile);
	}

	public void restore() throws IOException {
		if (tempFile == null) {
			throw new IllegalStateException("temp file was null");
		}
		if (!tempFile.exists()) {
			throw new IllegalStateException("temp file doesn't exist: " + tempFile.getAbsolutePath());
		}
		transferTo(tempFile, new File(basePath));
	}
	private void transferTo(File source, File dest) throws IOException {
		FileInputStream in = new FileInputStream(source);
		FileOutputStream out = new FileOutputStream(dest);

		byte[] buff = new byte[1024];

		try {
			while (in.read(buff) != -1) {
				out.write(buff);
			}
		} finally {
			in.close();
			out.close();
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof FileBackupRestoreManager)) {
			return false;
		}

		final FileBackupRestoreManager that = (FileBackupRestoreManager) o;

		if (basePath != null ? !basePath.equals(that.basePath) : that.basePath != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return basePath != null ? basePath.hashCode() : 0;
	}

	@Override
	public String toString() {
		return "FileBackupRestoreManager{" + "basePath='" + basePath + '\'' + '}';
	}
}
