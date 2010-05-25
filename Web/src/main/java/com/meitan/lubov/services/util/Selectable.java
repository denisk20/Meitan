package com.meitan.lubov.services.util;

/**
 * @author denis_k
 *         Date: 25.05.2010
 *         Time: 14:23:48
 */
public interface Selectable <T> {
	boolean isSelected();

	void setSelected(boolean selected);

	void select();

	void deselect();

	T getItem();

	String getLabel();
}
