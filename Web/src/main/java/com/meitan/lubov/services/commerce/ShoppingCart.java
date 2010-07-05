package com.meitan.lubov.services.commerce;

import com.meitan.lubov.model.PriceAware;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Set;

/**
 * @author denis_k
 *         Date: 10.06.2010
 *         Time: 14:43:09
 */
public interface ShoppingCart {
	ArrayList<ShoppingCartItem> getItems();

	ArrayList<PriceAware> getPriceAwares();
	
	BigDecimal getTotalPrice();

	void addItem(PriceAware item);

	int getTypesCount();

	int getQuantity(PriceAware item);

	boolean deleteItem(ShoppingCartItem item);

	void sayHello();
}
