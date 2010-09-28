package com.meitan.lubov.model.persistent;

import com.meitan.lubov.model.NameAware;
import com.meitan.lubov.model.components.Name;
import com.meitan.lubov.model.components.Passport;
import com.meitan.lubov.model.util.PersistentOrderableImpl;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * Date: Jan 27, 2010
 * Time: 8:14:09 PM
 *
 * @author denisk
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries({
		@NamedQuery(name = "getClientByLogin", query = "from Client c where c.login = :login"),
		@NamedQuery(name = "getClientByEmail", query = "from Client c where c.email = :email")
})
//@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"passport_series", "passport_number"})})
//todo rename to User
public class Client extends PersistentOrderableImpl implements Serializable {
	private long id;
	private Name name = new Name();
	private String email;
	private String notes;
	private Set<BuyingAct> purchases = new HashSet<BuyingAct>();

	private String login;
	private String password;
	private Set<Authority> roles = new HashSet<Authority>();
	private boolean enabled;

	private Passport passport = new Passport();
	//todo
	private Date joinDate = new Date();
	private String conformedPassword;
	//todo unit test
	private String phone;

	private static final long serialVersionUID = -1634311520960706521L;

	public Client() {

	}

	public Client(Name name, String email) {
		this.name = name;
		this.email = email;
	}
	public Client(Name name, String email, Passport passport, Date joinDate) {
		this(name, email);
		this.passport = passport;
		this.joinDate = joinDate;
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

	@Column(nullable = false, unique = true)
	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@OneToMany(mappedBy = "client")
	public Set<Authority> getRoles() {
		return roles;
	}

	public void setRoles(Set<Authority> roles) {
		this.roles = roles;
	}

	@Embedded
	public Passport getPassport() {
		return passport;
	}

	public void setPassport(Passport passport) {
		this.passport = passport;
	}

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getJoinDate() {
		return joinDate;
	}

	public void setJoinDate(Date joinDate) {
		this.joinDate = joinDate;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	@Transient
	public String getConformedPassword() {
		return conformedPassword;
	}

	public void setConformedPassword(String conformedPassword) {
		this.conformedPassword = conformedPassword;
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

	private void readObject(java.io.ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		if (name == null) {
			name = new Name("hacked name readObject", "", "");
		}
		if (email == null) {
			email = "hacked email readObject";
		}
		in.defaultReadObject();
	}
}
