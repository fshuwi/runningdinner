package org.runningdinner.ui.mail;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.servlet.http.Cookie;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.runningdinner.service.email.MailServerSettings;
import org.runningdinner.service.email.MailServerSettingsImpl;
import org.runningdinner.test.util.TestUtil;
import org.runningdinner.ui.dto.BaseSendMailsModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { TestUtil.APP_CONTEXT, TestUtil.MAIL_CONTEXT })
@ActiveProfiles("junit")
public class TestMailTransformer {

	@Autowired
	protected MailServerSettingsTransformer mailServerSettingsTransformer;

	@Test
	public void testEnrichSendMailsModel() throws JsonGenerationException, JsonMappingException, IOException {

		String dinnerUuid = "uuid";

		MailServerSettings mailServerSettings = generateTestableMailServerSettings();
		MailServerSettingsDinnerTO mailServerSettingsTO = new MailServerSettingsDinnerTO(dinnerUuid, mailServerSettings);
		MailServerSettingsDinnerListTO mailServerSettingsDinnerListTO = new MailServerSettingsDinnerListTO();
		mailServerSettingsDinnerListTO.addOrReplace(mailServerSettingsTO);

		BaseSendMailsModel sendMailsModel = new BaseSendMailsModel();
		String mailServerSettingsStr = toJson(mailServerSettingsDinnerListTO);
		System.out.println(mailServerSettingsStr);

		mailServerSettingsTransformer.enrichModelWithMailServerSettings(dinnerUuid, sendMailsModel, mailServerSettingsStr);

		verifyMailServerSettingValues(sendMailsModel);
	}

	@Test
	public void testTransformToCookie() throws IOException {

		// Test initial creation of cookie
		MailServerSettings mailServerSettings = generateTestableMailServerSettings();
		String dinnerUuid = "uuid";
		Cookie[] cookies = new Cookie[] { new Cookie("foo", "bar") };
		verifyTransformToCookie(dinnerUuid, mailServerSettings, cookies);

		// Test edit of already existing cookie
		MailServerSettingsImpl oldMailServerSettings = generateTestableMailServerSettings();
		oldMailServerSettings.setMailServer("old");
		oldMailServerSettings.setUsername("username_old");
		MailServerSettingsDinnerTO oldMailServerSettingsTO = new MailServerSettingsDinnerTO(dinnerUuid, oldMailServerSettings);
		MailServerSettingsDinnerListTO oldMailServerSettingsDinnerListTO = new MailServerSettingsDinnerListTO();
		oldMailServerSettingsDinnerListTO.addOrReplace(oldMailServerSettingsTO);
		cookies = new Cookie[] { new Cookie("foo", "bar"),
				new Cookie(MailServerSettingsTransformer.MAILSERVER_SETTINGS_COOKIE_NAME, toJson(oldMailServerSettingsDinnerListTO)) };
		verifyTransformToCookie(dinnerUuid, mailServerSettings, cookies);
	}

	protected void verifyTransformToCookie(String dinnerUuid, MailServerSettings mailServerSettings, Cookie[] cookies) throws IOException {
		Cookie mailServerSettingsCookie = mailServerSettingsTransformer.transformToCookie(dinnerUuid, mailServerSettings, cookies);
		assertEquals(MailServerSettingsTransformer.MAILSERVER_SETTINGS_COOKIE_NAME, mailServerSettingsCookie.getName());

		MailServerSettingsDinnerListTO mailServerSettingsListTO = fromJson(mailServerSettingsCookie.getValue());
		assertEquals(1, mailServerSettingsListTO.getMailServerSettingsListTO().size());
		verifyMailServerSettingValues(mailServerSettingsListTO.getMailServerSettingsListTO().get(0).getMailServerSettings());
		assertEquals("uuid", mailServerSettingsListTO.getMailServerSettingsListTO().get(0).getDinnerUuid());
	}

	protected void verifyMailServerSettingValues(MailServerSettings mailServerSettingsUnderTest) {
		assertEquals("smtp.gmx.de", mailServerSettingsUnderTest.getMailServer());
		assertEquals("username", mailServerSettingsUnderTest.getUsername());
		assertEquals("password", mailServerSettingsUnderTest.getPassword());
		assertEquals("from@from.de", mailServerSettingsUnderTest.getFrom());
		assertEquals(587, mailServerSettingsUnderTest.getMailServerPort());
	}

	protected MailServerSettingsImpl generateTestableMailServerSettings() {
		MailServerSettingsImpl result = new MailServerSettingsImpl();
		result.setMailServer("smtp.gmx.de");
		result.setMailServerPort(587);
		result.setFrom("from@from.de");
		result.setPassword("password");
		result.setUsername("username");
		result.setUseTls(true);
		return result;
	}

	protected String toJson(Object object) throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper jsonMapper = new ObjectMapper();
		return jsonMapper.writeValueAsString(object);
	}

	protected MailServerSettingsDinnerListTO fromJson(final String jsonStr) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper jsonMapper = new ObjectMapper();
		return jsonMapper.readValue(jsonStr.getBytes(), MailServerSettingsDinnerListTO.class);
	}
}
