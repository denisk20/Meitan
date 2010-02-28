package com.meitan.lubov.model.persistent;

import com.meitan.lubov.model.Image;
import com.meitan.lubov.model.Price;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

/**
 * Date: Jan 27, 2010
 * Time: 7:14:18 PM
 *
 * @author denisk
 */
@Entity
@NamedQueries({
		@NamedQuery(name = "getProducts", query = "from Product p"),
		@NamedQuery(name = "getProductsNew", query = "from Product p where p.new=true"),
		@NamedQuery(name = "getProductsForCategory", query = "select c.products from ProductCategory c where c.id=:categoryId")
})
public class Product {
	private long id;
	private String name;
	private String description;
	private boolean isNew;
	private Set<ProductCategory> categories = new HashSet<ProductCategory>();

	private Set<BuyingAct> purchases = new HashSet<BuyingAct>();
	private Set<Image> images = new HashSet<Image>();
	private Price price;

	public Product() {
	}

	public Product(String name) {
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

	//todo add DB unique not null constraint
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

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean aNew) {
		isNew = aNew;
	}

	@ManyToMany(mappedBy = "products")
	public Set<ProductCategory> getCategories() {
		return categories;
	}

	public void setCategories(Set<ProductCategory> categories) {
		this.categories = categories;
	}

	@ManyToMany
	public Set<BuyingAct> getPurchases() {
		return purchases;
	}

	public void setPurchases(Set<BuyingAct> purchases) {
		this.purchases = purchases;
	}

	@OneToMany
	public Set<Image> getImages() {
		return images;
	}

	public void setImages(Set<Image> images) {
		this.images = images;
	}

	public Price getPrice() {
		return price;
	}

	public void setPrice(Price price) {
		this.price = price;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Product)) {
			return false;
		}

		final Product product = (Product) o;

		if (!name.equals(product.name)) {
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
		return "Product{" + "id=" + id + ", name='" + name + '\'' + ", description='" + description + '\'' + ", isNew=" + isNew + ", categories=" + categories + ", images=" + images + ", price=" + price + '}';
	}
}
