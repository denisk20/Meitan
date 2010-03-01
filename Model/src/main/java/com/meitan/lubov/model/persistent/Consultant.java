package com.meitan.lubov.model.persistent;

import com.meitan.lubov.model.Name;
import com.meitan.lubov.model.Passport;

import java.util.Date;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Date: Jan 28, 2010
 * Time: 10:20:31 AM
 *
 * @author denisk
 */
@Entity
public class Consultant extends Client {
	private Passport passport;
	private Date joinDate;

	public Consultant() {
	}

	public Consultant(Name name, String email, Passport passport, Date joinDate) {
		super(name, email);
		this.passport = passport;
		this.joinDate = joinDate;
	}

	//todo add DB unique not null constraint
	@Embedded
	public Passport getPassport() {
		return passport;
	}

	public void setPassport(Passport passport) {
		this.passport = passport;
	}

	//todo add DB not null constraint
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

		if (!joinDate.equals(that.joinDate)) {
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
