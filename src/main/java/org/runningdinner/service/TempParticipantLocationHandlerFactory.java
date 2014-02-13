package org.runningdinner.service;

import org.apache.commons.lang3.StringUtils;
import org.runningdinner.service.impl.SerializeToDirectoryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * Factory class for instantiating a concrete implementation of TempParticipantLocationHandler
 * 
 * @author Clemens Stich
 * 
 */
public class TempParticipantLocationHandlerFactory {

	private static Logger LOGGER = LoggerFactory.getLogger(TempParticipantLocationHandlerFactory.class);

	@Value("${upload.application.tmpdir}")
	private String tmpUploadDirectory;

	public TempParticipantLocationHandler create() {
		LOGGER.info("Using {} as temprary upload directory", tmpUploadDirectory);
		if (StringUtils.isNotEmpty(tmpUploadDirectory)) {
			SerializeToDirectoryHandler result = new SerializeToDirectoryHandler();
			result.setTmpUploadDirectory(tmpUploadDirectory);
			return result;
		}
		else {
			throw new IllegalStateException("Currently there is only SerializeToDirectoryHandler supported as implementation");
		}
	}

	public String getTmpUploadDirectory() {
		return tmpUploadDirectory;
	}

	public void setTmpUploadDirectory(String tmpUploadDirectory) {
		this.tmpUploadDirectory = tmpUploadDirectory;
	}

}
