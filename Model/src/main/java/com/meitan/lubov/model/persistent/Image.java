package com.meitan.lubov.model.persistent;

import com.meitan.lubov.model.util.PersistentOrderableImpl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Date: Jan 27, 2010
 * Time: 7:16:57 PM
 *
 * @author denisk
 */
@Entity
public class Image extends PersistentOrderableImpl implements Serializable {
	private long id;
    //with trailing slash
	private String url;

	public Image() {
	}

	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Image(String url) {
		this.url = url;
	}

	@Column(name="image_url")
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Image)) {
			return false;
		}

		final Image image = (Image) o;

		if (url != null ? !url.equals(image.url) : image.url != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return url != null ? url.hashCode() : 0;
	}

	@Override
	public String toString() {
		return "Image{" +
				"id=" + id +
				", url='" + url + '\'' +
				'}';
	}
}
