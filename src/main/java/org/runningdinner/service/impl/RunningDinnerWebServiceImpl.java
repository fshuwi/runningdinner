package org.runningdinner.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.runningdinner.core.CoreUtil;
import org.runningdinner.core.Participant;
import org.runningdinner.core.converter.ConversionException;
import org.runningdinner.core.converter.ConverterFactory;
import org.runningdinner.core.converter.ConverterFactory.INPUT_FILE_TYPE;
import org.runningdinner.core.converter.FileConverter;
import org.runningdinner.core.converter.config.ParsingConfiguration;
import org.runningdinner.service.TempParticipantLocationHandler;
import org.runningdinner.service.UuidGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

public class RunningDinnerWebServiceImpl {

	private TempParticipantLocationHandler tempParticipantLocationHandler;

	private UuidGenerator uuidGenerator;

	@Value("${host.context.url}")
	private String hostUrlContext;

	public List<Participant> parseParticipants(final MultipartFile file, final ParsingConfiguration parsingConfiguration)
			throws IOException, ConversionException {

		final INPUT_FILE_TYPE fileType = determineFileType(file);

		InputStream inputStream = null;
		try {
			inputStream = file.getInputStream();
			FileConverter fileConverter = ConverterFactory.newConverter(parsingConfiguration, fileType);
			return fileConverter.parseParticipants(inputStream);
		}
		finally {
			CoreUtil.closeStream(inputStream);
		}
	}

	public INPUT_FILE_TYPE determineFileType(final MultipartFile file) {
		if (file != null) {
			INPUT_FILE_TYPE fileType = ConverterFactory.determineFileType(file.getOriginalFilename());
			if (INPUT_FILE_TYPE.UNKNOWN != fileType) {
				return fileType;
			}

			fileType = ConverterFactory.determineFileType(file.getContentType());
			if (INPUT_FILE_TYPE.UNKNOWN != fileType) {
				return fileType;
			}
		}
		return INPUT_FILE_TYPE.UNKNOWN;
	}

	public String copyParticipantFileToTempLocation(final MultipartFile file, final String uniqueIdentifier) throws IOException {
		return tempParticipantLocationHandler.pushToTempLocation(file, uniqueIdentifier);
	}

	public List<Participant> getParticipantsFromTempLocation(final String uniqueIdentifier, final ParsingConfiguration parsingConfiguration)
			throws IOException, ConversionException {
		return tempParticipantLocationHandler.popFromTempLocation(uniqueIdentifier, parsingConfiguration);
	}

	public String generateNewUUID() {
		return uuidGenerator.generateNewUUID();
	}

	public String constructAdministrationUrl(final String uuid, final HttpServletRequest request) {
		String hostUrlContextToUse = this.hostUrlContext;
		if (StringUtils.isEmpty(hostUrlContext)) {
			hostUrlContextToUse = generateHostContextFromRequest(request);
		}

		if (StringUtils.isEmpty(hostUrlContextToUse)) {
			throw new IllegalStateException("Host URL of current server could not be retrieved");
		}

		if (!hostUrlContextToUse.endsWith("/")) {
			hostUrlContextToUse += "/";
		}
		return hostUrlContextToUse + "event/" + uuid + "/admin";
	}

	protected String generateHostContextFromRequest(final HttpServletRequest request) {
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Autowired
	public void setTempParticipantLocationHandler(TempParticipantLocationHandler tempParticipantLocationHandler) {
		this.tempParticipantLocationHandler = tempParticipantLocationHandler;
	}

	@Autowired
	public void setUuidGenerator(UuidGenerator uuidGenerator) {
		this.uuidGenerator = uuidGenerator;
	}

	public void setHostUrlContext(String hostUrlContext) {
		this.hostUrlContext = hostUrlContext;
	}

}
