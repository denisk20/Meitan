package com.meitan.lubov.services.util.selectors;

import com.meitan.lubov.services.util.Selectable;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author denis_k
 *         Date: 25.05.2010
 *         Time: 13:50:08
 */
public interface ItemsSelector<T> {
	boolean isItemSelected(T item);

	void addItem(T item, boolean selected);

	void selectItem(T item);

	void deselectItem(T item);

	void addItems(Collection<T> items);

	ArrayList<Selectable<T>> getItems();

	void selectAppropriateItems(Collection<T> items);

	void clear();
}
