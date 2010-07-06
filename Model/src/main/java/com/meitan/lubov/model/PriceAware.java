package com.meitan.lubov.model;

import com.meitan.lubov.model.components.Price;

import java.math.BigDecimal;

/**
 * @author denis_k
 *         Date: 10.06.2010
 *         Time: 14:40:42
 */
public interface PriceAware extends NameAware, IdAware{
	Price getPrice();
}
