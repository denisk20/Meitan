package com.meitan.lubov.services.util;

import com.meitan.lubov.model.NameAware;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author denis_k
 *         Date: 25.05.2010
 *         Time: 14:24:42
 */
public class SelectableImpl<T extends NameAware> implements Selectable, Serializable {
	private T item;
	private Boolean selected;

	public SelectableImpl(T item, Boolean selected) {
		this.item = item;
		this.selected = selected;
	}

	public SelectableImpl() {
	}

	public T getItem() {
		return item;
	}

	public void setItem(T item) {
		this.item = item;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void select() {
		selected = true;
	}

	@Override
	public void deselect() {
		selected = false;
	}

	@Override
	public String getLabel() {
		return item.getName();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SelectableImpl)) return false;

		SelectableImpl that = (SelectableImpl) o;

		if (item != null ? !item.equals(that.item) : that.item != null) return false;
		if (selected != null ? !selected.equals(that.selected) : that.selected != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = item != null ? item.hashCode() : 0;
		result = 31 * result + (selected != null ? selected.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "SelectableImpl{" +
				"item=" + item +
				", selected=" + selected +
				'}';
	}
}
