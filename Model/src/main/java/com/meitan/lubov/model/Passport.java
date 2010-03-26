package com.meitan.lubov.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Date: Jan 27, 2010
 * Time: 8:17:58 PM
 *
 * @author denisk
 */
@Embeddable
public class Passport {
	private String series;
	private String number;

	public Passport() {
	}

	@Column(name = "passport_series", nullable = false)
	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	@Column(name = "passport_number", nullable = false)
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Passport)) {
			return false;
		}

		final Passport passport = (Passport) o;

		if (!number.equals(passport.number)) {
			return false;
		}
		if (!series.equals(passport.series)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = series.hashCode();
		result = 31 * result + number.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "Passport{" + "series='" + series + '\'' + ", number='" + number + '\'' + '}';
	}
}
