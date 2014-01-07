package org.runningdinner.service;

import org.apache.commons.lang3.StringUtils;
import org.runningdinner.service.impl.DirectoryBasedParticipantLocationHandler;
import org.springframework.beans.factory.annotation.Value;

public class TempParticipantLocationHandlerFactory {

	@Value("${upload.application.tmpdir}")
	private String tmpUploadDirectory;

	public TempParticipantLocationHandler create() {
		if (StringUtils.isNotEmpty(tmpUploadDirectory)) {
			DirectoryBasedParticipantLocationHandler result = new DirectoryBasedParticipantLocationHandler();
			result.setTmpUploadDirectory(tmpUploadDirectory);
			return result;
		}
		else {
			throw new IllegalStateException("Currently there is only DirectoryBasedParticipantLocationHandler supported as implementation");
		}
	}

	public String getTmpUploadDirectory() {
		return tmpUploadDirectory;
	}

	public void setTmpUploadDirectory(String tmpUploadDirectory) {
		this.tmpUploadDirectory = tmpUploadDirectory;
	}

}
