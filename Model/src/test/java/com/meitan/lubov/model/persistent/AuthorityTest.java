package com.meitan.lubov.model.persistent;

import com.meitan.lubov.model.components.Name;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * Date: Jul 9, 2010
 * Time: 10:46:53 AM
 *
 * @author denisk
 */
public class AuthorityTest {
	private Authority testable = new Authority();

	@Test
	public void testId() {
		Long id = new Long(1);
		testable.setId(id);

		assertEquals(id, testable.getId());
	}

	@Test
	public void testClient() {
		Client client = new Client(new Name("name", "second", "third"), "a@b.com");
		testable.setClient(client);

		assertEquals(client, testable.getClient());
	}

	@Test
	public void testRole() {
		final String role = "a role";
		testable.setRole(role);
		assertEquals(role, testable.getRole());
	}
}
