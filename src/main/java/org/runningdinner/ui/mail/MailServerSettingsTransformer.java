package org.runningdinner.ui.mail;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.Cookie;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.runningdinner.core.util.Encryptor;
import org.runningdinner.model.RunningDinner;
import org.runningdinner.model.RunningDinnerPreferences;
import org.runningdinner.service.RunningDinnerService;
import org.runningdinner.service.email.MailServerSettings;
import org.runningdinner.service.email.MailServerSettingsImpl;
import org.runningdinner.ui.dto.BaseSendMailsModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Optional;

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

	private RunningDinnerService runningDinnerService;
	
	private Encryptor encryptor;

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

		RunningDinner dinner = runningDinnerService.loadDinnerWithBasicDetails(dinnerUuid);
		RunningDinnerPreferences dinnerPreferences = runningDinnerService.loadPreferences(dinner);
		Optional<Boolean> useCustomMailServer = dinnerPreferences.getBooleanValue(RunningDinnerPreferences.USE_CUSTOM_MAILSERVER);
		if (useCustomMailServer.isPresent() && useCustomMailServer.get().booleanValue() == true) {
			sendMailsModel.setUseCustomMailServer(true);
		}
		else {
			sendMailsModel.setUseCustomMailServer(false);
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
			mailServerSettingsJson = encryptSafe(mailServerSettingsJson);
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

			mailServerSettingsStr = decryptSafe(mailServerSettingsStr);
			
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

	protected String encryptSafe(String text) {
		try {
			return encryptor.encrypt(text);
		} catch (Exception ex) {
			LOGGER.error("Failed to encrypt mail server settings cookie string", ex);
			return text;
		}
	}
	
	protected String decryptSafe(String text) {
		try {
			return encryptor.decrypt(text);
		} catch (Exception ex) {
			LOGGER.error("Failed to decrypt mail server settings cookie string", ex);
			return text;
		}
	}
	
	@Autowired
	public void setRunningDinnerService(RunningDinnerService runningDinnerService) {
		this.runningDinnerService = runningDinnerService;
	}

	@Autowired
	public void setEncryptor(Encryptor encryptor) {
		this.encryptor = encryptor;
	}
	
}
