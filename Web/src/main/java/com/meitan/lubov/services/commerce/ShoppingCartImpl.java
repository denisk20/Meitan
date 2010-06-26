package com.meitan.lubov.services.commerce;

import com.meitan.lubov.model.PriceAware;
import com.meitan.lubov.model.components.Price;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author denis_k
 *         Date: 10.06.2010
 *         Time: 14:42:29
 */
@Service("cart")
public class ShoppingCartImpl implements ShoppingCart{
	private HashMap<PriceAware, Integer> items = new HashMap<PriceAware, Integer>();

	@Override
	public Set<PriceAware> getItems() {
		return items.keySet();
	}

	@Override
	public int getQuantity(PriceAware item) {
		return items.get(item);
	}

	@Override
	public BigDecimal getTotalPrice() {
		BigDecimal result = new BigDecimal(0);

		for (Map.Entry <PriceAware, Integer> entry : items.entrySet()) {
			Price price = entry.getKey().getPrice();
			if (price != null) {
				Integer val = entry.getValue();
				BigDecimal amount = price.getAmount();
				BigDecimal priceForItems = amount.multiply(new BigDecimal(val));
				result = result.add(priceForItems);
			} else {
				throw new IllegalArgumentException("No price for product " + entry);
			}
		}

		return result;
	}

	@Override
	public void addItem(PriceAware item) {
		int quantity = 0;
		if (items.containsKey(item)) {
			quantity = items.get(item);
		}
		items.put(item, quantity + 1);
	}

	@Override
	public int getTypesCount() {
		return items.size();
	}
}
