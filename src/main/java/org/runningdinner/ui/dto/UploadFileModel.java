package org.runningdinner.ui.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.commons.lang.StringUtils;
import org.runningdinner.core.converter.config.AbstractColumnConfig;
import org.runningdinner.core.converter.config.AddressColumnConfig;
import org.runningdinner.core.converter.config.AddressColumnConfig.AddressColumnConfigBuilder;
import org.runningdinner.core.converter.config.AddressColumnConfig.CompositeAddressColumnConfigBuilder;
import org.runningdinner.core.converter.config.AddressColumnConfig.SingleAddressColumnConfigBuilder;
import org.runningdinner.core.converter.config.EmailColumnConfig;
import org.runningdinner.core.converter.config.MobileNumberColumnConfig;
import org.runningdinner.core.converter.config.NameColumnConfig;
import org.runningdinner.core.converter.config.NumberOfSeatsColumnConfig;
import org.runningdinner.core.converter.config.ParsingConfiguration;
import org.runningdinner.core.converter.config.SequenceColumnConfig;
import org.springframework.web.multipart.MultipartFile;

public class UploadFileModel {

	private MultipartFile file;

	private Map<Integer, String> columnMappings;

	private int startRow;

	protected UploadFileModel() {
	}

	public static UploadFileModel newFromParsingConfiguration(final ParsingConfiguration parsingConfiguration) {

		UploadFileModel result = new UploadFileModel();
		result.columnMappings = new LinkedHashMap<Integer, String>(16);
		result.startRow = parsingConfiguration.getStartRow() + 1; // Rows and Columns are 0-index-based, but we display it 1-index-based!
		result.initColumnMappingsFromConfiguration(parsingConfiguration);

		return result;
	}

	/**
	 * Prepares the view-ouput for displaying the column rendering information.
	 * 
	 * @param parsingConfiguration The current parsing configuration which shall be displayed to the user for editing
	 */
	private void initColumnMappingsFromConfiguration(final ParsingConfiguration parsingConfiguration) {
		// We are just interested in the number of available columns
		int numColumnMappingOptions = ColumnMappingOption.getNumColumnMappingOptions();

		for (int i = 0; i < numColumnMappingOptions; i++) {
			if (checkAbstractColumnConfig(i, parsingConfiguration.getEmailColumnConfig(), ColumnMappingOption.EMAIL)) {
				continue;
			}
			if (checkAbstractColumnConfig(i, parsingConfiguration.getMobileNumberColumnConfig(), ColumnMappingOption.MOBILE)) {
				continue;
			}
			if (checkAbstractColumnConfig(i, parsingConfiguration.getSequenceColumnConfig(), ColumnMappingOption.SEQUENCE_NR)) {
				continue;
			}

			if (checkNumSeatsColumnConfig(i, parsingConfiguration.getNumSeatsColumnConfig())) {
				continue;
			}

			if (checkNameColumnConfig(i, parsingConfiguration.getNameColumnConfig())) {
				continue;
			}

			if (checkAddressColumnConfig(i, parsingConfiguration.getAddressColumnConfig())) {
				continue;
			}

			columnMappings.put(i, StringUtils.EMPTY); // Indicate no selection
		}
	}

	private boolean checkNameColumnConfig(final int columnIndexToCheck, final NameColumnConfig nameColumnConfig) {
		if (nameColumnConfig.isAvailable() && nameColumnConfig.isComposite()) {
			if (columnIndexToCheck == nameColumnConfig.getLastnameColumn()) {
				columnMappings.put(columnIndexToCheck, ColumnMappingOption.FULLNAME);
				return true;
			}
		}
		if (nameColumnConfig.isAvailable() && !nameColumnConfig.isComposite()) {
			if (columnIndexToCheck == nameColumnConfig.getLastnameColumn()) {
				columnMappings.put(columnIndexToCheck, ColumnMappingOption.LASTNAME);
				return true;
			}
			if (columnIndexToCheck == nameColumnConfig.getFirstnameColumn()) {
				columnMappings.put(columnIndexToCheck, ColumnMappingOption.FIRSTNAME);
				return true;
			}
		}
		return false;
	}

	private boolean checkNumSeatsColumnConfig(final int columnIndexToCheck, NumberOfSeatsColumnConfig numSeatsColumnConfig) {
		if (!numSeatsColumnConfig.isAvailable()) {
			return false;
		}

		final int columnIndex = numSeatsColumnConfig.getColumnIndex();

		if (columnIndexToCheck == columnIndex) {
			if (numSeatsColumnConfig.isNumericDeclaration()) {
				columnMappings.put(columnIndex, ColumnMappingOption.NUMBER_OF_SEATS);
			}
			else {
				columnMappings.put(columnIndex, ColumnMappingOption.CAN_HOST);
			}
			return true;
		}
		return false;
	}

	private boolean checkAbstractColumnConfig(final int columnIndexToCheck, final AbstractColumnConfig columnConfig, final String mappedName) {

		if (!columnConfig.isAvailable()) {
			return false;
		}

		if (columnIndexToCheck == columnConfig.getColumnIndex()) {
			columnMappings.put(columnIndexToCheck, mappedName);
			return true;
		}
		return false;
	}

	private boolean checkAddressColumnConfig(final int columnIndexToCheck, final AddressColumnConfig addressColumnConfig) {

		if (addressColumnConfig.isCompositeConfig()) {
			if (columnIndexToCheck == addressColumnConfig.getStreetColumn()) {
				columnMappings.put(columnIndexToCheck, ColumnMappingOption.COMPLETE_ADDRESS);
				return true;
			}
		}

		if (addressColumnConfig.isStreetAndStreetNrCompositeConfig()) {
			if (columnIndexToCheck == addressColumnConfig.getStreetColumn()) {
				columnMappings.put(columnIndexToCheck, ColumnMappingOption.STREET_WITH_NR);
				return true;
			}
		}

		if (addressColumnConfig.isZipAndCityCompositeConfig()) {
			if (columnIndexToCheck == addressColumnConfig.getZipColumn()) {
				columnMappings.put(columnIndexToCheck, ColumnMappingOption.ZIP_WITH_CITY);
				return true;
			}
		}

		if (addressColumnConfig.isSingleColumnConfig()) {
			if (columnIndexToCheck == addressColumnConfig.getStreetColumn()) {
				columnMappings.put(columnIndexToCheck, ColumnMappingOption.STREET);
				return true;
			}
			if (columnIndexToCheck == addressColumnConfig.getStreetNrColumn()) {
				columnMappings.put(columnIndexToCheck, ColumnMappingOption.STREET_NR);
				return true;
			}
			if (columnIndexToCheck == addressColumnConfig.getCityColumn()) {
				columnMappings.put(columnIndexToCheck, ColumnMappingOption.CITY);
				return true;
			}
			if (columnIndexToCheck == addressColumnConfig.getZipColumn()) {
				columnMappings.put(columnIndexToCheck, ColumnMappingOption.ZIP);
				return true;
			}
		}

		return false;
	}

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	/**
	 * Creates a new ParsingConfiguration from the entered user values
	 * 
	 * @return
	 */
	public ParsingConfiguration createParsingConfiguration() {

		NumberOfSeatsColumnConfig numberOfSeatsColumnConfig = NumberOfSeatsColumnConfig.noNumberOfSeatsColumn();
		EmailColumnConfig emailColumnConfig = EmailColumnConfig.noEmailColumn();
		MobileNumberColumnConfig mobileColumnConfig = MobileNumberColumnConfig.noMobileNumberColumn();

		Set<String> columnMappingNames = new HashSet<String>(columnMappings.values());
		DualHashBidiMap bidirectionalColumnMappings = new DualHashBidiMap(columnMappings);

		NameColumnConfig nameColumnConfig = createNameColumnConfig(columnMappingNames, bidirectionalColumnMappings);

		AddressColumnConfig addressColumnConfig = createAddressColumnConfig(columnMappingNames, bidirectionalColumnMappings);

		if (columnMappingNames.contains(ColumnMappingOption.EMAIL)) {
			Integer columnIndex = (Integer)bidirectionalColumnMappings.getKey(ColumnMappingOption.EMAIL);
			emailColumnConfig = EmailColumnConfig.createEmailColumnConfig(columnIndex);
		}
		if (columnMappingNames.contains(ColumnMappingOption.MOBILE)) {
			Integer columnIndex = (Integer)bidirectionalColumnMappings.getKey(ColumnMappingOption.MOBILE);
			mobileColumnConfig = MobileNumberColumnConfig.createMobileNumberColumnConfig(columnIndex);
		}

		if (columnMappingNames.contains(ColumnMappingOption.CAN_HOST)) {
			Integer columnIndex = (Integer)bidirectionalColumnMappings.getKey(ColumnMappingOption.CAN_HOST);
			numberOfSeatsColumnConfig = NumberOfSeatsColumnConfig.newBooleanSeatsColumnConfig(columnIndex);
		}
		else if (columnMappingNames.contains(ColumnMappingOption.NUMBER_OF_SEATS)) {
			Integer columnIndex = (Integer)bidirectionalColumnMappings.getKey(ColumnMappingOption.NUMBER_OF_SEATS);
			numberOfSeatsColumnConfig = NumberOfSeatsColumnConfig.newBooleanSeatsColumnConfig(columnIndex);
		}

		ParsingConfiguration result = new ParsingConfiguration(nameColumnConfig, addressColumnConfig, numberOfSeatsColumnConfig);
		result.setEmailColumnConfig(emailColumnConfig);
		result.setMobileNumberColumnConfig(mobileColumnConfig);

		if (columnMappingNames.contains(ColumnMappingOption.SEQUENCE_NR)) {
			Integer columnIndex = (Integer)bidirectionalColumnMappings.getKey(ColumnMappingOption.SEQUENCE_NR);
			result.setSequenceColumnConfig(SequenceColumnConfig.createSequenceColumnConfig(columnIndex));
		}

		return result;
	}

	private AddressColumnConfig createAddressColumnConfig(final Set<String> columnMappingNames, final BidiMap bidirectionalColumnMappings) {
		if (columnMappingNames.contains(ColumnMappingOption.COMPLETE_ADDRESS)) {
			Integer compositeColumnIndex = (Integer)bidirectionalColumnMappings.getKey(ColumnMappingOption.COMPLETE_ADDRESS);
			return AddressColumnConfig.newBuilder().withCompositeColumn(compositeColumnIndex).build();
		}
		else {
			AddressColumnConfigBuilder addressColumnConfigBuilder = AddressColumnConfig.newBuilder();

			if (columnMappingNames.contains(ColumnMappingOption.STREET_WITH_NR)) {
				Integer streetWithNrIndex = (Integer)bidirectionalColumnMappings.getKey(ColumnMappingOption.STREET_WITH_NR);
				CompositeAddressColumnConfigBuilder compositeBuilder = addressColumnConfigBuilder.withStreetAndStreetNrColumn(streetWithNrIndex);

				if (columnMappingNames.contains(ColumnMappingOption.ZIP_WITH_CITY)) {
					Integer zipAndCityColumnIndex = (Integer)bidirectionalColumnMappings.getKey(ColumnMappingOption.ZIP_WITH_CITY);
					return compositeBuilder.buildWithZipAndCityColumn(zipAndCityColumnIndex);
				}

				Integer zipColumnIndex = (Integer)bidirectionalColumnMappings.get(ColumnMappingOption.ZIP);
				SingleAddressColumnConfigBuilder singleBuilder = compositeBuilder.withZipColumn(zipColumnIndex);

				Integer cityColumnIndex = (Integer)bidirectionalColumnMappings.get(ColumnMappingOption.CITY);
				if (cityColumnIndex != null) {
					singleBuilder = singleBuilder.andCity(cityColumnIndex);
				}
				return singleBuilder.build();

			}
			else {
				Integer streetColumnIndex = (Integer)bidirectionalColumnMappings.get(ColumnMappingOption.STREET);
				Integer streetNrColumnIndex = (Integer)bidirectionalColumnMappings.get(ColumnMappingOption.STREET_NR);
				Integer zipColumnIndex = (Integer)bidirectionalColumnMappings.get(ColumnMappingOption.ZIP);
				Integer cityColumnIndex = (Integer)bidirectionalColumnMappings.get(ColumnMappingOption.CITY);

				SingleAddressColumnConfigBuilder singleBuilder = addressColumnConfigBuilder.withStreet(streetColumnIndex).andStreetNrColumn(
						streetNrColumnIndex).andZip(zipColumnIndex);
				if (cityColumnIndex != null) {
					singleBuilder = singleBuilder.andCity(cityColumnIndex);
				}
				return singleBuilder.build();
			}
		}

	}

	private static NameColumnConfig createNameColumnConfig(final Set<String> columnMappingNames, final BidiMap bidirectionalColumnMappings) {
		if (columnMappingNames.contains(ColumnMappingOption.FULLNAME)) {
			Integer columnIndex = (Integer)bidirectionalColumnMappings.getKey(ColumnMappingOption.FULLNAME);
			return NameColumnConfig.createForOneColumn(columnIndex);
		}
		else {
			Integer firstNameColumnIndex = (Integer)bidirectionalColumnMappings.getKey(ColumnMappingOption.FIRSTNAME);
			Integer lastNameColumnIndex = (Integer)bidirectionalColumnMappings.getKey(ColumnMappingOption.LASTNAME);
			return NameColumnConfig.createForTwoColumns(firstNameColumnIndex, lastNameColumnIndex);
		}
	}

	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	/**
	 * Contains the column mapping selection from user
	 * 
	 * @return
	 */
	public Map<Integer, String> getColumnMappings() {
		return columnMappings;
	}

	public void setColumnMappings(Map<Integer, String> columnMappings) {
		this.columnMappings = columnMappings;
	}

	public void sortColumnMappings() {
		if (columnMappings == null) {
			return;
		}

		List<Integer> columnIndexList = new ArrayList<Integer>(columnMappings.keySet());
		Collections.sort(columnIndexList);

		Map<Integer, String> tmpSortedMap = new LinkedHashMap<Integer, String>(columnIndexList.size());
		for (Integer columnIndex : columnIndexList) {
			tmpSortedMap.put(columnIndex, columnMappings.get(columnIndex));
		}
		this.columnMappings = tmpSortedMap;

	}

}
