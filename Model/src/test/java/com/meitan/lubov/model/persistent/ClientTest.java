package com.meitan.lubov.model.persistent;

import com.meitan.lubov.model.components.Name;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Date: Jan 28, 2010
 * Time: 9:21:13 PM
 *
 * @author denisk
 */
public class ClientTest {
	private Client testable;
	@Before
	public void setUp() throws Exception {
		testable = new Client();
	}

	@Test
	public void testGetName() throws Exception {
		String firstName = "name";
		String secondName = "secondName";
		String patronymic = "parential";
		Name name = new Name();
		name.setFirstName(firstName);
		name.setSecondName(secondName);
		name.setPatronymic(patronymic);

		testable.setName(name);

		assertEquals(name, testable.getName());
	}

	@Test
	public void testGetPurchases() throws Exception {
		BuyingAct buy1 = new BuyingAct();
		Client client1 = new Client();
		buy1.setClient(client1);
		client1.setName(new Name());
		client1.setEmail("a@b.com");
		buy1.setDate(new Date());
		BuyingAct buy2 = new BuyingAct();
		buy2.setDate(new Date());
		Client client2 = new Client();
		buy2.setClient(client2);
		client2.setName(new Name());
		client2.setEmail("b@c.com");
		BuyingAct buy3 = new BuyingAct();
		Client client3 = new Client();
		buy3.setClient(client3);
		client3.setName(new Name());
		client3.setEmail("d@e.com");
		buy3.setDate(new Date());

		Set<BuyingAct> boughts = new HashSet<BuyingAct>();
		boughts.add(buy1);
		boughts.add(buy2);
		boughts.add(buy3);

		testable.getPurchases().addAll(boughts);
		assertEquals(boughts, testable.getPurchases());
	}

	@Test
	public void testGetLogin() {
		String login = "login";
		testable.setLogin(login);

		assertEquals(login, testable.getLogin());
	}

	@Test
	public void testGetPass() {
		String pass = "pass";
		testable.setPassword(pass);

		assertEquals(pass, testable.getPassword());
	}

	@Test
	public void testEnabled() {
		boolean enabled = true;
		testable.setEnabled(enabled);

		assertEquals(enabled, testable.isEnabled());
	}

	@Test
	public void testRole() {
		String role = "ROLE_CLIENT";
		testable.setRole(role);

		assertEquals(role, testable.getRole());
	}
}
