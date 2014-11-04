package org.runningdinner.ui.mail;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.Cookie;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.runningdinner.service.email.MailServerSettings;
import org.runningdinner.service.email.MailServerSettingsImpl;
import org.runningdinner.ui.dto.BaseSendMailsModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Clemens
 *
 */
@Component
public class MailServerSettingsTransformer {

	public static final String MAILSERVER_SETTINGS_COOKIE_NAME = "mailServerSettings";
	public static final int MAILSERVER_SETTINGS_COOKIE_AGE_SECONDS = 3944700; // 1,5 months in seconds

	private static Logger LOGGER = LoggerFactory.getLogger(MailServerSettingsTransformer.class);

	public void enrichModelWithMailServerSettings(String dinnerUuid, BaseSendMailsModel sendMailsModel, String mailServerSettingsStr) {

		MailServerSettingsDinnerListTO mailServerSettingsDinnerListTO = readFromJsonString(mailServerSettingsStr);
		if (mailServerSettingsDinnerListTO.isEmpty()) {
			sendMailsModel.setUseCustomMailServer(false);
			return;
		}

		MailServerSettings foundMailServerSettings = null;

		List<MailServerSettingsDinnerTO> mailServerSettingsListTO = mailServerSettingsDinnerListTO.getMailServerSettingsListTO();
		for (MailServerSettingsDinnerTO mailServerSettingsTO : mailServerSettingsListTO) {
			if (StringUtils.equalsIgnoreCase(mailServerSettingsTO.getDinnerUuid(), dinnerUuid)) {
				foundMailServerSettings = mailServerSettingsTO.getMailServerSettings();
				break;
			}
		}

		if (foundMailServerSettings != null) {
			sendMailsModel.setMailServerSettings((MailServerSettingsImpl)foundMailServerSettings);
		}
	}

	public Cookie transformToCookie(String dinnerUuid, MailServerSettings mailServerSettings, Cookie[] cookies) throws IOException {

		Cookie mailServerSettingsCookie = getOrCreateMailserverSettingsCookie(cookies);
		String existingMailServerSettings = mailServerSettingsCookie.getValue();

		MailServerSettingsDinnerListTO mailServerSettingsList = readFromJsonString(existingMailServerSettings);

		MailServerSettingsDinnerTO mailServerSettingsTO = new MailServerSettingsDinnerTO();
		mailServerSettingsTO.setMailServerSettings(mailServerSettings);
		mailServerSettingsTO.setDinnerUuid(dinnerUuid);

		// TODO: Theoretically this should be synchronized if a user executes this in parallel in several tabs

		mailServerSettingsList.addOrReplace(mailServerSettingsTO);

		ObjectMapper jsonMapper = new ObjectMapper();
		try {
			String mailServerSettingsJson = jsonMapper.writeValueAsString(mailServerSettingsList);
			mailServerSettingsCookie.setValue(mailServerSettingsJson);
		}
		catch (IOException e) {
			throw e;
		}

		return mailServerSettingsCookie;
	}

	protected Cookie getOrCreateMailserverSettingsCookie(Cookie[] cookies) {
		if (cookies != null && cookies.length > 0) {
			for (Cookie cookie : cookies) {
				if (StringUtils.equalsIgnoreCase(MAILSERVER_SETTINGS_COOKIE_NAME, cookie.getName())) {
					return cookie;
				}
			}
		}

		Cookie cookie = new Cookie(MAILSERVER_SETTINGS_COOKIE_NAME, StringUtils.EMPTY);
		cookie.setMaxAge(MAILSERVER_SETTINGS_COOKIE_AGE_SECONDS);
		return cookie;
	}

	protected MailServerSettingsDinnerListTO readFromJsonString(String mailServerSettingsStr) {

		if (StringUtils.isNotEmpty(mailServerSettingsStr)) {

			ObjectMapper jsonMapper = new ObjectMapper();
			try {
				MailServerSettingsDinnerListTO mailServerSettingsDinnerListTO = jsonMapper.readValue(mailServerSettingsStr.getBytes(),
						MailServerSettingsDinnerListTO.class);
				return mailServerSettingsDinnerListTO;
			}
			catch (IOException e) {
				LOGGER.error("Could not transform json string {} to TO-object", mailServerSettingsStr, e);
			}
		}

		return new MailServerSettingsDinnerListTO();
	}
}
