package com.meitan.lubov.model.persistent;

import com.meitan.lubov.model.Image;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

/**
 * Date: Jan 27, 2010
 * Time: 7:16:21 PM
 *
 * @author denisk
 */
@Entity
@NamedQueries({
		@NamedQuery(name = "getProductCategories", query = "from ProductCategory c")
})
public class ProductCategory {
	private long id;
	private String name;
	private Image image;

	private Set<Product> products = new HashSet<Product>();

	public ProductCategory() {
	}

	public ProductCategory(String name) {
		this.name = name;
	}

	@Id
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	//todo add DB unique not null constraint
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

	@OneToOne
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
		if (!(o instanceof ProductCategory)) {
			return false;
		}

		final ProductCategory that = (ProductCategory) o;

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
		return "ProductCategory{" + "id=" + id + ", name='" + name + '\'' + ", products=" + products + '}';
	}
}
