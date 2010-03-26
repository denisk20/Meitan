package com.meitan.lubov.model.persistent;

import com.meitan.lubov.model.persistent.Image;
import com.meitan.lubov.model.util.PersistentOrderableImpl;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

/**
 * Date: Jan 27, 2010
 * Time: 7:16:21 PM
 *
 * @author denisk
 */
@Entity
public class Category extends PersistentOrderableImpl {
	private long id;
	private String name;
	private Image image;

	private Set<Product> products = new HashSet<Product>();

	public Category() {
	}

	public Category(String name) {
		this.name = name;
	}

    @Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(nullable = false, unique = true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ManyToMany
	public Set<Product> getProducts() {
		return products;
	}

	public void setProducts(Set<Product> products) {
		this.products = products;
	}

	@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
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
		return name.hashCode();
	}

	@Override
	public String toString() {
		return "Category{" + "id=" + id + ", name='" + name + '\'' + '}';
	}
}
