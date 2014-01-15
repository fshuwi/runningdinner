package org.runningdinner.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.runningdinner.service.impl.DefaultUuidGenerator;

public class TestDefaultUuidGenerator {

	DefaultUuidGenerator generator = new DefaultUuidGenerator();

	@Test
	public void testValidUuids() {
		String newUuid = generator.generateNewUUID();
		assertEquals(true, generator.isValid(newUuid));

		newUuid = generator.generateNewUUID();
		assertEquals(true, generator.isValid(newUuid));
	}

	@Test
	public void testInvalidUuids() {
		assertEquals(false, generator.isValid(""));
		assertEquals(false, generator.isValid(null));
		assertEquals(
				false,
				generator.isValid("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor s"));
		assertEquals(false, generator.isValid("ZZ19bd7b+f54d-4268-a29c-3bd3ed99103e"));
	}
}
