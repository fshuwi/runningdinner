package org.runningdinner.ui;

import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.runningdinner.core.converter.config.ParsingConfiguration;
import org.runningdinner.ui.dto.ColumnMappingOption;
import org.runningdinner.ui.dto.UploadFileModel;

public class TestUploadColumnMapping {

	@Test
	public void testParsingConfigurationToColumnMappings() {
		ParsingConfiguration pc = ParsingConfiguration.newDefaultConfiguration();
		UploadFileModel uploadFileModel = UploadFileModel.newFromParsingConfiguration(pc);

		Map<Integer, String> columnMappings = uploadFileModel.getColumnMappings();
		assertEquals(ColumnMappingOption.FULLNAME, columnMappings.get(0));
		assertEquals(ColumnMappingOption.STREET_WITH_NR, columnMappings.get(1));
		assertEquals(ColumnMappingOption.ZIP_WITH_CITY, columnMappings.get(2));
		assertEquals(ColumnMappingOption.NUMBER_OF_SEATS, columnMappings.get(3));

		// Verify some empty mappings:
		assertEquals(StringUtils.EMPTY, columnMappings.get(5));
		assertEquals(StringUtils.EMPTY, columnMappings.get(6));

		// Verify that 1-index based start row is used (for rendering view)
		assertEquals(2, uploadFileModel.getStartRow());
	}

	@Test
	public void testColumnMappingsToParsingConfiguration() {
		ParsingConfiguration pc = ParsingConfiguration.newDefaultConfiguration();
		UploadFileModel uploadFileModel = UploadFileModel.newFromParsingConfiguration(pc);

		Map<Integer, String> columnMappings = new LinkedHashMap<Integer, String>();
		columnMappings.put(0, ColumnMappingOption.FIRSTNAME);
		columnMappings.put(1, ColumnMappingOption.LASTNAME);
		columnMappings.put(2, ColumnMappingOption.EMAIL);
		columnMappings.put(3, ColumnMappingOption.MOBILE);
		columnMappings.put(4, ColumnMappingOption.COMPLETE_ADDRESS);
		uploadFileModel.setColumnMappings(columnMappings);

		pc = uploadFileModel.createParsingConfiguration();
		assertEquals(true, pc.getNameColumnConfig().isAvailable());
		assertEquals(false, pc.getNameColumnConfig().isComposite());
		assertEquals(0, pc.getNameColumnConfig().getFirstnameColumn());
		assertEquals(1, pc.getNameColumnConfig().getLastnameColumn());

		assertEquals(true, pc.getEmailColumnConfig().isAvailable());
		assertEquals(2, pc.getEmailColumnConfig().getColumnIndex());

		assertEquals(3, pc.getMobileNumberColumnConfig().getColumnIndex());

		assertEquals(true, pc.getAddressColumnConfig().isCompositeConfig());
		assertEquals(false, pc.getAddressColumnConfig().isSingleColumnConfig());
		assertEquals(4, pc.getAddressColumnConfig().getStreetColumn());
		assertEquals(4, pc.getAddressColumnConfig().getStreetNrColumn());
		assertEquals(4, pc.getAddressColumnConfig().getZipColumn());
		assertEquals(4, pc.getAddressColumnConfig().getCityColumn());

		assertEquals(false, pc.getNumSeatsColumnConfig().isAvailable());

		// Verify that 0-index based row is used for parsing
		assertEquals(1, pc.getStartRow());
	}

	@Test
	public void testColumnMappingSorting() {
		UploadFileModel ufm = UploadFileModel.newFromParsingConfiguration(ParsingConfiguration.newDefaultConfiguration());

		// Mix column index order:
		Map<Integer, String> columnMappings = new LinkedHashMap<Integer, String>();
		columnMappings.put(3, ColumnMappingOption.FIRSTNAME);
		columnMappings.put(4, ColumnMappingOption.LASTNAME);
		columnMappings.put(2, ColumnMappingOption.EMAIL);
		columnMappings.put(1, ColumnMappingOption.MOBILE);
		columnMappings.put(0, ColumnMappingOption.COMPLETE_ADDRESS);
		ufm.setColumnMappings(columnMappings);

		ufm.sortColumnMappings();

		// Assert that all mappings are ordered from 0 to 4:
		Map<Integer, String> sortedMappings = ufm.getColumnMappings();
		int cnt = 0;
		for (Entry<Integer, String> entry : sortedMappings.entrySet()) {
			assertEquals(cnt, entry.getKey().intValue());
			cnt++;
		}
	}
}
