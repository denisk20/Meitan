package com.meitan.lubov.model.components;

import java.io.Serializable;
import javax.persistence.Embeddable;

/**
 * Date: Jan 27, 2010
 * Time: 8:15:10 PM
 *
 * @author denisk
 */
@Embeddable
public class Name implements Serializable {
	private String firstName;
	private String patronymic;
	private String secondName;

	public Name() {
	}

	public Name(String firstName, String patronymic, String secondName) {
		this.firstName = firstName;
		this.patronymic = patronymic;
		this.secondName = secondName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getPatronymic() {
		return patronymic;
	}

	public void setPatronymic(String patronymic) {
		this.patronymic = patronymic;
	}

	public String getSecondName() {
		return secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Name)) {
			return false;
		}

		final Name name = (Name) o;

		if (firstName != null ? !firstName.equals(name.firstName) : name.firstName != null) {
			return false;
		}
		if (patronymic != null ? !patronymic.equals(name.patronymic) : name.patronymic != null) {
			return false;
		}
		if (secondName != null ? !secondName.equals(name.secondName) : name.secondName != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = firstName != null ? firstName.hashCode() : 0;
		result = 31 * result + (patronymic != null ? patronymic.hashCode() : 0);
		result = 31 * result + (secondName != null ? secondName.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Name{" + "firstName='" + firstName + '\'' + ", patronymic='" + patronymic + '\'' + ", secondName='" + secondName + '\'' + '}';
	}
}
