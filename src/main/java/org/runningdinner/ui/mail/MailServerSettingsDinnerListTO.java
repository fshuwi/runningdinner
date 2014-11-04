package org.runningdinner.ui.mail;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonRootName;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName("mailServerSettingsList")
public class MailServerSettingsDinnerListTO {

	private List<MailServerSettingsDinnerTO> mailServerSettingsListTO = new ArrayList<MailServerSettingsDinnerTO>();

	public List<MailServerSettingsDinnerTO> getMailServerSettingsListTO() {
		return mailServerSettingsListTO;
	}

	public void setMailServerSettingsListTO(List<MailServerSettingsDinnerTO> mailServerSettingsListTO) {
		this.mailServerSettingsListTO = mailServerSettingsListTO;
	}

	public void addOrReplace(MailServerSettingsDinnerTO mailServerSettingsTO) {
		// Remove mailserver-settings instance if it already exists (identified by dinner uuid):
		mailServerSettingsListTO.remove(mailServerSettingsTO);
		// Add (and/or replace if already existing instance has been removed above) passed object
		this.mailServerSettingsListTO.add(mailServerSettingsTO);
	}

	public boolean isEmpty() {
		return mailServerSettingsListTO == null || mailServerSettingsListTO.size() == 0;
	}

}
