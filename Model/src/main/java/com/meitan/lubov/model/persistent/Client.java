package com.meitan.lubov.model.persistent;

import com.meitan.lubov.model.NameAware;
import com.meitan.lubov.model.components.Name;
import com.meitan.lubov.model.util.PersistentOrderableImpl;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.OneToMany;

/**
 * Date: Jan 27, 2010
 * Time: 8:14:09 PM
 *
 * @author denisk
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Client extends PersistentOrderableImpl implements Serializable {
	private long id;
	private Name name = new Name();
	private String email;
	private String notes;
	private Set<BuyingAct> purchases = new HashSet<BuyingAct>();


	public Client() {

	}

	public Client(Name name, String email) {
		this.name = name;
		this.email = email;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Embedded
	public Name getName() {
		return name;
	}

	public void setName(Name name) {
		this.name = name;
	}

	@OneToMany(mappedBy = "client")
	public Set<BuyingAct> getPurchases() {
		return purchases;
	}

	public void setPurchases(Set<BuyingAct> purchases) {
		this.purchases = purchases;
	}

	@Column(nullable = false, unique = true)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Lob
	public String getNotes() {

		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Client)) {
			return false;
		}

		final Client client = (Client) o;

		if (!email.equals(client.email)) {
			return false;
		}
		if (!name.equals(client.name)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = name.hashCode();
		result = 31 * result + email.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "Client{" + "id=" + id + ", name=" + name + ", email='" + email + '\'' + '}';
	}
}
