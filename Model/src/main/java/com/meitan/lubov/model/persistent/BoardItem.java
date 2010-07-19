package com.meitan.lubov.model.persistent;

import com.meitan.lubov.model.util.PersistentOrderableImpl;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author denis_k
 *         Date: 14.07.2010
 *         Time: 22:49:42
 */
@Entity
@NamedQueries({
		@NamedQuery(name = "getAll", query = "from BoardItem order by postDate")
})
public class BoardItem extends PersistentOrderableImpl implements Serializable {
	private Long id;
	private Date postDate;
	private String content;
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

	@Lob
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof BoardItem)) return false;

		BoardItem boardItem = (BoardItem) o;

		if (content != null ? !content.equals(boardItem.content) : boardItem.content != null) return false;
		if (postDate != null ? !Long.valueOf(postDate.getTime()).equals(Long.valueOf(boardItem.postDate.getTime())) : boardItem.postDate != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = postDate != null ? postDate.hashCode() : 0;
		result = 31 * result + (content != null ? content.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "BoardItem{" +
				"id=" + id +
				", postDate=" + postDate +
				", content='" + content + '\'' +
				'}';
	}
}