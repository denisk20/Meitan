package com.meitan.lubov.model.persistent;

import com.meitan.lubov.model.util.PersistentOrderable;
import com.meitan.lubov.model.util.PersistentOrderableImpl;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author denis_k
 *         Date: 14.07.2010
 *         Time: 22:48:06
 */
@Entity
public class NewsBoard extends PersistentOrderableImpl implements Serializable {
	private Long id;
	private List<BoardItem> items = new ArrayList<BoardItem>();

	public NewsBoard() {
	}

	public NewsBoard(List<BoardItem> items) {
		this.items = items;
	}

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@OneToMany
	public List<BoardItem> getItems() {
		return items;
	}

	public void setItems(List<BoardItem> items) {
		this.items = items;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof NewsBoard)) return false;

		NewsBoard newsBoard = (NewsBoard) o;

		if (items != null ? !new ArrayList(items).equals(newsBoard.items) : newsBoard.items != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return items != null ? items.hashCode() : 0;
	}

	@Override
	public String toString() {
		return "NewsBoard{" +
				"id=" + id +
				", items=" + items +
				'}';
	}
}
