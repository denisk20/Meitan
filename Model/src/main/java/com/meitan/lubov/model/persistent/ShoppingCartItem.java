package com.meitan.lubov.model.persistent;

import com.meitan.lubov.model.IdAware;
import com.meitan.lubov.model.PriceAware;
import com.meitan.lubov.model.util.PersistentOrderableImpl;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@NamedQueries({
		@NamedQuery(name = "findByProduct", query = "from ShoppingCartItem where item.id = :productId")
})
@Entity
public class ShoppingCartItem extends PersistentOrderableImpl implements Serializable, IdAware {
	private Long id;
	private PriceAware item;
	private Integer quantity;
	private Date date;

	public ShoppingCartItem() {
	}

	public ShoppingCartItem(PriceAware item, Integer quantity) {
		this.item = item;
		this.quantity = quantity;
	}

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(targetEntity = Product.class)
	public PriceAware getItem() {
		return item;
	}

	public void setItem(PriceAware item) {
		this.item = item;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	@Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Transient
	public BigDecimal getPrice() {
		BigDecimal amount = item.getPrice().getAmount();
		if (amount == null) {
			throw new IllegalStateException("No price for item " + item);
		}
		BigDecimal result = amount.multiply(new BigDecimal(quantity));

		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ShoppingCartItem)) return false;

		ShoppingCartItem that = (ShoppingCartItem) o;

		if (date != null ? !Long.valueOf(date.getTime()).equals(Long.valueOf(that.date.getTime())) : that.date != null) return false;
		if (item != null ? !item.equals(that.item) : that.item != null) return false;
		if (quantity != null ? !quantity.equals(that.quantity) : that.quantity != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = item != null ? item.hashCode() : 0;
		result = 31 * result + (quantity != null ? quantity.hashCode() : 0);
		result = 31 * result + (date != null ? date.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "ShoppingCartItem{" +
				"id=" + id +
				", item=" + item +
				", quantity=" + quantity +
				", date=" + date +
				'}';
	}
}
