package com.meitan.lubov.model.persistent;

import com.meitan.lubov.model.Passport;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Date;

/**
 * Date: Jan 28, 2010
 * Time: 9:39:44 PM
 *
 * @author denisk
 */
public class ConsultantTest {
	private Consultant testable;

	@Before
	public void before() {
		testable = new Consultant();
	}
	@Test
	public void testGetPassport() throws Exception {
		final Passport passport = new Passport();
		passport.setNumber("blah");
		passport.setSeries("bleh");
		testable.setPassport(passport);

		assertEquals(passport, testable.getPassport());
	}

	@Test
	public void testGetJoinDate() throws Exception {
		final Date joinDate = new Date();
		testable.setJoinDate(joinDate);

		assertEquals(joinDate, testable.getJoinDate());
	}
}
