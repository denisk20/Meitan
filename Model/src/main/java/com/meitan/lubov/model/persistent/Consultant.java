package com.meitan.lubov.model.persistent;

import com.meitan.lubov.model.components.Name;
import com.meitan.lubov.model.components.Passport;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

/**
 * Date: Jan 28, 2010
 * Time: 10:20:31 AM
 *
 * @author denisk
 */
//@Entity
//@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"passport_series", "passport_number"})})
public class Consultant extends Client {
	private Passport passport=new Passport();
	private Date joinDate;

	public Consultant() {
	}

	public Consultant(Name name, String email, Passport passport, Date joinDate) {
		super(name, email);
		this.passport = passport;
		this.joinDate = joinDate;
	}

	public Consultant(Client c) {
		super(c.getName(), c.getEmail());
		setLogin(c.getLogin());
		setPassword(c.getPassword());
		setNotes(c.getNotes());
		getRoles().addAll(c.getRoles());
		this.joinDate = new Date();
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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Consultant)) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}

		final Consultant that = (Consultant) o;

		if (!Long.valueOf(joinDate.getTime()).equals(that.joinDate.getTime())) {
			return false;
		}
		if (!passport.equals(that.passport)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + passport.hashCode();
		result = 31 * result + joinDate.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return super.toString() + "Consultant{" + "passport=" + passport + ", joinDate=" + joinDate + '}';
	}
}
