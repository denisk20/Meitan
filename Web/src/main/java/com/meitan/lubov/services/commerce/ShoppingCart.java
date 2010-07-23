package com.meitan.lubov.services.commerce;

import com.meitan.lubov.model.PriceAware;
import com.meitan.lubov.model.persistent.ShoppingCartItem;
import org.springframework.faces.model.OneSelectionTrackingListDataModel;

import java.math.BigDecimal;
import java.util.ArrayList;

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

	OneSelectionTrackingListDataModel getItemsDataModel();

	void emptyCart();

	int getCount();
}
