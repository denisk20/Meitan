package com.meitan.lubov.model.persistent;

import com.meitan.lubov.model.ImageAware;
import com.meitan.lubov.model.NameAware;
import com.meitan.lubov.model.persistent.Image;
import com.meitan.lubov.model.util.PersistentOrderableImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

/**
 * Date: Jan 27, 2010
 * Time: 7:16:21 PM
 *
 * @author denisk
 */
@Entity
@NamedQueries({
		@NamedQuery(name = "getCategoryByName", query = "from Category p where p.name=:name")
})
public class Category extends PersistentOrderableImpl implements NameAware, ImageAware, Serializable {
	private final Log log = LogFactory.getLog(getClass());

	private long id;
	private String name;
	private String description;
	private Image image;

	private Set<Product> products = new HashSet<Product>();

	public Category() {
	}

	public Category(String name) {
		this.name = name;
	}

    @Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(nullable = false, unique = true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Lob
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@ManyToMany
	public Set<Product> getProducts() {
		return products;
	}

	public void setProducts(Set<Product> products) {
		this.products = products;
	}

	@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE})
	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	@Override
	@Transient
	public HashSet<Image> getImages() {
		HashSet<Image> images = new HashSet<Image>();
		if (image != null) {
			images.add(image);
		}
		return images;
	}

	@Override
	public void addImage(Image image) {
		setImage(image);
	}

	@Override
	public void removeImage(Image image) {
		if (this.image.equals(image)) {
			this.image = null;
		} else {
			log.error("Can't remove image " + image + " from category " + this);
		}
	}

	@Override
	@Transient
	public boolean isAllowedToAdd() {
		return image == null;
	}

	@Override
	@Transient
	public Image getAvatar() {
		return image;
	}

	@Override
	public void setAvatar(Image image) {
		//do nothing
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Category)) {
			return false;
		}

		final Category that = (Category) o;

		if (!name.equals(that.name)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		if (name == null) {
			log.error("Name was null for entity " + this);
			return 0;
		}
		return name.hashCode();
	}

	@Override
	public String toString() {
		return "Category{" + "id=" + id + ", name='" + name + '\'' + '}';
	}
}
