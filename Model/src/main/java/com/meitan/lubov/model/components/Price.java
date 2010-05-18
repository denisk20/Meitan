package com.meitan.lubov.model.components;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Date: Jan 27, 2010
 * Time: 7:17:48 PM
 *
 * @author denisk
 */
@Embeddable
public class Price implements Serializable {
	private BigDecimal amount;

	public Price() {
	}

	public Price(BigDecimal amount) {
		this.amount = amount;
	}

	@Column(name="total_price")
	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Price)) {
			return false;
		}

		final Price price = (Price) o;

		if (amount != null ? !amount.equals(price.amount) : price.amount != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return amount != null ? amount.hashCode() : 0;
	}

	@Override
	public String toString() {
		return "Price{" + "amount=" + amount + '}';
	}
}
