package com.meitan.lubov.services.util;

/**
 * @author denis_k
 *         Date: 24.05.2010
 *         Time: 17:45:49
 */
//todo any chance of getting rid of this?
public class StringWrap {
	private String wrapped;

	public StringWrap(String wrapped) {
		this.wrapped = wrapped;
	}

	public String getWrapped() {
		return wrapped;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof StringWrap)) return false;

		StringWrap that = (StringWrap) o;

		if (wrapped != null ? !wrapped.equals(that.wrapped) : that.wrapped != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return wrapped != null ? wrapped.hashCode() : 0;
	}

	@Override
	public String toString() {
		return "StringWrap{" +
				"wrapped='" + wrapped + '\'' +
				'}';
	}
}
