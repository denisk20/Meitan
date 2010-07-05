package com.meitan.lubov.services.commerce;

import com.meitan.lubov.model.PriceAware;

import java.math.BigDecimal;

public class ShoppingCartItem {
	private PriceAware item;
	private Integer quantity;

	public ShoppingCartItem(PriceAware item, Integer quantity) {
		this.item = item;
		this.quantity = quantity;
	}

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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ShoppingCartItem)) return false;

		ShoppingCartItem that = (ShoppingCartItem) o;

		if (item != null ? !item.equals(that.item) : that.item != null) return false;
		if (quantity != null ? !quantity.equals(that.quantity) : that.quantity != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = item != null ? item.hashCode() : 0;
		result = 31 * result + (quantity != null ? quantity.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "ShoppingCartItem{" +
				"item=" + item +
				", price=" + quantity +
				'}';
	}
}
