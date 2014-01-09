package org.runningdinner.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.runningdinner.core.CoreUtil;
import org.runningdinner.core.GeneratedTeamsResult;
import org.runningdinner.core.NoPossibleRunningDinnerException;
import org.runningdinner.core.Participant;
import org.runningdinner.core.RunningDinnerCalculator;
import org.runningdinner.core.RunningDinnerConfig;
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

public class RunningDinnerServiceImpl {

	@Value("${host.context.url}")
	private String hostUrlContext;

	private TempParticipantLocationHandler tempParticipantLocationHandler;

	private UuidGenerator uuidGenerator;

	private RunningDinnerCalculator runningDinnerCalculator = new RunningDinnerCalculator();

	/**
	 * Parses participant list out from uploaded file
	 * 
	 * @param file
	 * @param parsingConfiguration
	 * @return
	 * @throws IOException
	 * @throws ConversionException
	 */
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

	/**
	 * Checks whether all participants can assigned into teams with the provided configuration.<br>
	 * If not, the resulting list contain all participants that cannot be assigned.<br>
	 * If the resulting list is empty, then all participants can successfully be assigned into teams.<br>
	 * <br>
	 * Callers must also check the NoPossibleRunningDinnerException which is thrown if there are e.g. too few participants.
	 * 
	 * @param runningDinnerConfig
	 * @param participants
	 * @return
	 * @throws NoPossibleRunningDinnerException Thrown when it is not possible to construct a running dinner with the provided configuration
	 *             and the provided participants. This can happen if there are too few participants.
	 */
	public List<Participant> calculateNotAssignableParticipants(RunningDinnerConfig runningDinnerConfig, List<Participant> participants)
			throws NoPossibleRunningDinnerException {

		GeneratedTeamsResult generatedTeamsResult = runningDinnerCalculator.generateTeams(runningDinnerConfig, participants);
		if (generatedTeamsResult.hasNotAssignedParticipants()) {
			return generatedTeamsResult.getNotAssignedParticipants();
		}
		return Collections.emptyList();
	}

	/**
	 * Tries to determine the file-type of the uploaded file.<br>
	 * If file type cannot be determined and/or it is not supported the result is INPUT_FILE_TYPE.UNKNOWN
	 * 
	 * @param file
	 * @return
	 */
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

	public List<Participant> getParticipantsFromTempLocation(final String location, final ParsingConfiguration parsingConfiguration)
			throws IOException, ConversionException {
		return tempParticipantLocationHandler.popFromTempLocation(location, parsingConfiguration);
	}

	/**
	 * Generate a new UUID which can e.g. be used for a new running dinner
	 * 
	 * @return
	 */
	public String generateNewUUID() {
		return uuidGenerator.generateNewUUID();
	}

	/**
	 * Builds a valid URL for administrating the running dinner identified by the passed uuid.<br>
	 * Typically the URL is constructed out of a configured property which identifies host on which this app is running. If this property
	 * does not exist then it is tried to construct the host out of the passed request.
	 * 
	 * @param uuid
	 * @param request (Optional) Used when there is no configured host property for constructing the resulting URL
	 * @return
	 */
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
