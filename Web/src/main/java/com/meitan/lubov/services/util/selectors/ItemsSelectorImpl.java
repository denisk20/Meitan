package com.meitan.lubov.services.util.selectors;

import com.meitan.lubov.model.NameAware;
import com.meitan.lubov.services.util.Selectable;
import com.meitan.lubov.services.util.SelectableImpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * @author denis_k
 *         Date: 25.05.2010
 *         Time: 13:47:15
 */
public abstract class ItemsSelectorImpl<T extends NameAware> implements ItemsSelector<T>, Serializable {
	private ArrayList<Selectable<T>> items = new ArrayList<Selectable<T>>();

	@Override
	public boolean isItemSelected(T item) {
		return getSelectableForItem(item).isSelected();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addItem(T item, boolean selected) {
		Selectable<T> selectable = new SelectableImpl<T>(item, selected);
		items.add(selectable);
	}

	@Override
	public void selectItem(T item) {
		getSelectableForItem(item).select();
	}

	@Override
	public void deselectItem(T item) {
		getSelectableForItem(item).deselect();
	}

	@Override
	public void addItems(Collection<T> items) {
		for (T item : items) {
			addItem(item, false);
		}
	}

	@Override
	public ArrayList<Selectable<T>> getItems() {
		return items;
	}

	@Override
	public void selectAppropriateItems(Collection<T> items) {
		for (T item : items) {
			Selectable<T> selectable = getSelectableForItem(item);
			if (selectable != null) {
				selectable.select();
			}
		}
	}

	private Selectable<T> getSelectableForItem(T item) {
		for (Selectable<T> selectable : items) {
			if (selectable.getItem().equals(item)) {
				return selectable;
			}
		}
		return null;
	}

	@Override
	public void clear() {
		items.clear();
	}
}
