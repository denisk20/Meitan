package com.meitan.lubov.services.commerce;

import com.meitan.lubov.model.PriceAware;
import com.meitan.lubov.model.components.Price;
import com.meitan.lubov.model.persistent.ShoppingCartItem;
import org.springframework.faces.model.OneSelectionTrackingListDataModel;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author denis_k
 *         Date: 10.06.2010
 *         Time: 14:42:29
 */
@Service("cart")
public class ShoppingCartImpl implements ShoppingCart{
	protected ArrayList<ShoppingCartItem> items = new ArrayList<ShoppingCartItem>();

	private OneSelectionTrackingListDataModel itemsDataModel = new OneSelectionTrackingListDataModel(items);
	@Override
	public ArrayList<ShoppingCartItem> getItems() {
		return items;
	}

	@Override
	public ArrayList<PriceAware> getPriceAwares() {
		ArrayList<PriceAware> result = new ArrayList<PriceAware>();
		for (ShoppingCartItem i : items) {
			result.add(i.getItem());
		}
		return result;
	}

	@Override
	public int getQuantity(PriceAware item) {
		ShoppingCartItem found = fetchItem(item);
		if (found == null) {
			return 0;
		}
		return found.getQuantity();
	}

	@Override
	public BigDecimal getTotalPrice() {
		BigDecimal result = new BigDecimal(0);

		for (ShoppingCartItem i : items) {
			Price price = i.getItem().getPrice();
			if (price != null) {
				Integer val = i.getQuantity();
				BigDecimal amount = price.getAmount();
				BigDecimal priceForItems = amount.multiply(new BigDecimal(val));
				result = result.add(priceForItems);
			} else {
				throw new IllegalArgumentException("No price for product " + i);
			}
		}

		return result;
	}

	@Override
	public void addItem(PriceAware item) {
		ShoppingCartItem found = fetchItem(item);

		if (found != null) {
			int quantity = found.getQuantity();
			found.setQuantity(quantity + 1);
		} else {
			found = new ShoppingCartItem(item, 1);
			found.setDate(new Date());
			items.add(found);
		}
	}

	@Override
	public int getTypesCount() {
		return items.size();
	}

	private ShoppingCartItem fetchItem(PriceAware item) {
		ShoppingCartItem found = null;
		for (ShoppingCartItem i : items) {
			if (i.getItem().equals(item)) {
				if (found != null) {
					throw new IllegalStateException("Duplicate entities with item " + item);
				}
				found = i;
			}
		}
		return found;
	}

	@Override
	public boolean deleteItem(ShoppingCartItem item) {
		boolean result = items.remove(item);
		return result;
	}

	@Override
	public OneSelectionTrackingListDataModel getItemsDataModel() {
		return itemsDataModel;
	}

	@Override
	public void emptyCart() {
		items.clear();
	}

	@Override
	public int getCount() {
		int result = 0;
		for (ShoppingCartItem it : items) {
			result += it.getQuantity();
		}
		return result;
	}

	@Override
	 public void sayHello() {
		System.out.println("Hello!");
	}

	@Override
	public boolean isEmpty() {
		return items.isEmpty();
	}
}

