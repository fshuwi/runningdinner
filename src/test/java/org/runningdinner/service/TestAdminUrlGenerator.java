package org.runningdinner.service;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.runningdinner.service.impl.UrlGenerator;
import org.runningdinner.test.util.TestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { TestUtil.APP_CONTEXT, TestUtil.MAIL_CONTEXT })
@ActiveProfiles("junit")
public class TestAdminUrlGenerator {

	@Autowired
	UrlGenerator urlGenerator;

	@Before
	public void setUp() {
		urlGenerator.setHostUrlContext("http://localhost:8080/runningdinner");
	}

	@Test
	public void testAdminUrlGeneration() {
		String uuid = "myuuid";
		String url = urlGenerator.constructAdministrationUrl(uuid, null);
		assertEquals("http://localhost:8080/runningdinner/event/myuuid/admin", url);
	}
}
