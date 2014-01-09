package org.runningdinner.service.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.runningdinner.core.CoreUtil;
import org.runningdinner.core.Participant;
import org.runningdinner.core.converter.ConversionException;
import org.runningdinner.core.converter.ConverterFactory;
import org.runningdinner.core.converter.ConverterFactory.INPUT_FILE_TYPE;
import org.runningdinner.core.converter.FileConverter;
import org.runningdinner.core.converter.config.ParsingConfiguration;
import org.runningdinner.service.TempParticipantLocationHandler;
import org.springframework.web.multipart.MultipartFile;

/**
 * Default {@link TempParticipantLocationHandler} implementation which copies the original uploaded file to a custom directory in which it
 * might be retrieved later.
 * 
 * @author i01002492
 * 
 */
public class DirectoryBasedParticipantLocationHandler implements TempParticipantLocationHandler {

	private String tmpUploadDirectory;

	@Override
	public String pushToTempLocation(MultipartFile file, String uniqueIdentifier) throws IOException {

		// TODO: Use serialization instead!

		final String filename = getUniqueFilename(file, uniqueIdentifier);
		final String filepath = tmpUploadDirectory + File.separator + filename;
		final File newFile = new File(filepath);

		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			if (!newFile.exists()) {
				newFile.createNewFile();
			}

			inputStream = file.getInputStream();
			outputStream = new FileOutputStream(newFile);
			IOUtils.copy(inputStream, outputStream);
			return filepath;
		}
		finally {
			CoreUtil.closeStream(inputStream);
			CoreUtil.closeStream(outputStream);
		}
	}

	protected String getUniqueFilename(MultipartFile file, String uniqueIdentifier) {
		return uniqueIdentifier + "_" + file.getOriginalFilename();
	}

	@Override
	public List<Participant> popFromTempLocation(final String location, final ParsingConfiguration parsingConfiguration)
			throws IOException, ConversionException {

		InputStream in = null;
		try {
			in = new BufferedInputStream(FileUtils.openInputStream(new File(location)));
			INPUT_FILE_TYPE fileType = ConverterFactory.determineFileType(location);
			FileConverter fileConverter = ConverterFactory.newConverter(parsingConfiguration, fileType);
			return fileConverter.parseParticipants(in);
		}
		finally {
			CoreUtil.closeStream(in);
		}
	}

	public String getTmpUploadDirectory() {
		return tmpUploadDirectory;
	}

	public void setTmpUploadDirectory(String tmpUploadDirectory) {
		this.tmpUploadDirectory = tmpUploadDirectory;
	}

}
