package com.meitan.lubov.services.commerce;

import com.meitan.lubov.model.PriceAware;
import com.meitan.lubov.model.components.Price;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * @author denis_k
 *         Date: 10.06.2010
 *         Time: 14:42:29
 */
@Service("cart")
public class ShoppingCartImpl implements ShoppingCart{
	private ArrayList<PriceAware> items = new ArrayList<PriceAware>();

	@Override
	public ArrayList<PriceAware> getItems() {
		return items;
	}

	@Override
	public BigDecimal getTotalPrice() {
		BigDecimal result = new BigDecimal(0);

		for (PriceAware item : items) {
			Price price = item.getPrice();
			if (price != null) {
				result = result.add(price.getAmount());
			} else {
				throw new IllegalArgumentException("No price for product " + item);
			}
		}

		return result;
	}

	@Override
	public void addItem(PriceAware item) {
		items.add(item);
	}

	@Override
	public int getCount() {
		return items.size();
	}
}
