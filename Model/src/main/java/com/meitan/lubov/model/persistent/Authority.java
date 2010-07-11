package com.meitan.lubov.model.persistent;

import com.meitan.lubov.model.util.PersistentOrderableImpl;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Date: Jul 9, 2010
 * Time: 10:42:03 AM
 *
 * This class is not used currently
 * @author denisk
 */
@Entity
public class Authority extends PersistentOrderableImpl implements Serializable {
	private Long id;
	private Client client;
	private String role;

	public Authority() {
	}

	public Authority(Client client, String role) {
		this.client = client;
		this.role = role;
	}

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne
	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Authority)) return false;

		Authority authority = (Authority) o;

		if (!client.equals(authority.client)) return false;
		if (!role.equals(authority.role)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = client.hashCode();
		result = 31 * result + role.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "Authority{" +
				"id=" + id +
				", client=" + client +
				", role='" + role + '\'' +
				'}';
	}
}
