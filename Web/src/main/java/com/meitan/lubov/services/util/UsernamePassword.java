package com.meitan.lubov.services.util;

import java.io.Serializable;

/**
 * @author denis_k
 *         Date: 10.07.2010
 *         Time: 22:59:09
 */
public class UsernamePassword implements Serializable {
	private String username;
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof UsernamePassword)) return false;

		UsernamePassword that = (UsernamePassword) o;

		if (password != null ? !password.equals(that.password) : that.password != null) return false;
		if (username != null ? !username.equals(that.username) : that.username != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = username != null ? username.hashCode() : 0;
		result = 31 * result + (password != null ? password.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "UsernamePassword{" +
				"username='" + username + '\'' +
				", password='" + password + '\'' +
				'}';
	}
}
