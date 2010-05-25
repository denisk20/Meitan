package com.meitan.lubov.model.persistent;

import com.meitan.lubov.model.ImageAware;
import com.meitan.lubov.model.NameAware;
import com.meitan.lubov.model.components.Price;
import com.meitan.lubov.model.util.PersistentOrderableImpl;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * Date: Jan 27, 2010
 * Time: 7:14:18 PM
 *
 * @author denisk
 */
@Entity
@NamedQueries({
		@NamedQuery(name = "getProductsNew", query = "from Product p where p.new=true"),
		@NamedQuery(name = "getProductsForCategory", query = "select c.products from Category c where c.id=:categoryId")
})
public class Product extends PersistentOrderableImpl implements NameAware, ImageAware, Serializable {
	private long id;
	private String name;
	private String description;
	private boolean isNew;
	private Set<Category> categories = new HashSet<Category>();
    private Object[] categoriesIdArray;

    @Transient
    public Object[] getCategoriesIdArray() {
        return categoriesIdArray;
    }

    public void setCategoriesIdArray(Object[] categoriesArray) {
        this.categoriesIdArray = categoriesArray;
    }

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

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean aNew) {
		isNew = aNew;
	}

	@ManyToMany(mappedBy = "products")
	public Set<Category> getCategories() {
		return categories;
	}

	public void setCategories(Set<Category> categories) {
		this.categories = categories;
	}

	@ManyToMany
	public Set<BuyingAct> getPurchases() {
		return purchases;
	}

	public void setPurchases(Set<BuyingAct> purchases) {
		this.purchases = purchases;
	}

	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
	public Set<Image> getImages() {
		return images;
	}

	public void setImages(Set<Image> images) {
		this.images = images;
	}

	@Override
	public void addImage(Image image) {
		images.add(image);
	}

	@Override
	public void removeImage(Image image) {
		images.remove(image);
	}

	public Price getPrice() {
		return price;
	}

	public void setPrice(Price price) {
		this.price = price;
	}

	@Override
	@Transient
	public boolean isAllowedToAdd() {
		return true;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;

        Product product = (Product) o;

        if (description != null ? !description.equals(product.description) : product.description != null) return false;
        if (!name.equals(product.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    @Override
	public String toString() {
		return "Product{" + "id=" + id + ", name='" + name + '\'' + ", description='" + description + '\'' + ", isNew=" + isNew  + ", price=" + price + '}';
	}
}
