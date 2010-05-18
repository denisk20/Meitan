package com.meitan.lubov.services.backing;

import javax.faces.component.html.HtmlSelectManyListbox;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author denis_k
 *         Date: 17.05.2010
 *         Time: 11:46:34
 */
public class AddGoodBackingBean implements Serializable {

	private ArrayList<SelectItem> availableCategories;
	private ArrayList<SelectItem> selectedCategories = new ArrayList<SelectItem>();

	private transient HtmlSelectManyListbox availableCategoriesListbox;
	private transient HtmlSelectManyListbox selectedCategoriesListbox;

	public ArrayList<SelectItem> getAvailableCategories() {
		return availableCategories;
	}

	public void setAvailableCategories(ArrayList<SelectItem> availableCategories) {
		this.availableCategories = availableCategories;
	}

	public ArrayList<SelectItem> getSelectedCategories() {
		return selectedCategories;
	}

	public void setSelectedCategories(ArrayList<SelectItem> selectedCategories) {
		this.selectedCategories = selectedCategories;
	}

	public HtmlSelectManyListbox getAvailableCategoriesListbox() {
		return availableCategoriesListbox;
	}

	public void setAvailableCategoriesListbox(HtmlSelectManyListbox availableCategoriesListbox) {
		this.availableCategoriesListbox = availableCategoriesListbox;
	}

	public HtmlSelectManyListbox getSelectedCategoriesListbox() {
		return selectedCategoriesListbox;
	}

	public void setSelectedCategoriesListbox(HtmlSelectManyListbox selectedCategoriesListbox) {
		this.selectedCategoriesListbox = selectedCategoriesListbox;
	}

	public void categoriesAdded(ValueChangeEvent e) {

	}

	public void categoriesRemoved(ValueChangeEvent e) {

	}

	public void selectedCategoriesChanged(ValueChangeEvent e) {
		int i = 0;
	}

	public void submit(ActionEvent e) {
		int i=0;
		FacesContext.getCurrentInstance().renderResponse();
	}
}
