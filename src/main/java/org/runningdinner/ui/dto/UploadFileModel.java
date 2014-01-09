package org.runningdinner.ui.dto;

import org.runningdinner.core.converter.config.ParsingConfiguration;
import org.springframework.web.multipart.MultipartFile;

public class UploadFileModel {

	private MultipartFile file;

	private ParsingConfiguration configuration;

	public UploadFileModel() {
		this.configuration = ParsingConfiguration.newDefaultConfiguration();
		this.configuration.setStartRow(1);
	}

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public ParsingConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(ParsingConfiguration configuration) {
		this.configuration = configuration;
	}

}
