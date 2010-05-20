package com.meitan.lubov.services.util;

/**
 * @author denis_k
 *         Date: 20.05.2010
 *         Time: 13:28:06
 */
public class TempImage {
	private String tempFileName;
	private String destinationPath;

	public String getTempFileName() {
		return tempFileName;
	}

	public void setTempFileName(String tempFileName) {
		this.tempFileName = tempFileName;
	}

	public String getDestinationPath() {
		return destinationPath;
	}

	public void setDestinationPath(String destinationPath) {
		this.destinationPath = destinationPath;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof TempImage)) return false;

		TempImage tempImage = (TempImage) o;

		if (destinationPath != null ? !destinationPath.equals(tempImage.destinationPath) : tempImage.destinationPath != null)
			return false;
		if (tempFileName != null ? !tempFileName.equals(tempImage.tempFileName) : tempImage.tempFileName != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = tempFileName != null ? tempFileName.hashCode() : 0;
		result = 31 * result + (destinationPath != null ? destinationPath.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "TempImage{" +
				"tempFileName='" + tempFileName + '\'' +
				", destinationPath='" + destinationPath + '\'' +
				'}';
	}
}
