package org.runningdinner.ui.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.MessageSource;

/**
 * Simple wrapper class that contains simple key<->label pairs for the column-mapping for setting up the parsing configuration.<br>
 * A key indicates which info can be found in a column.<br>
 * A label contains a human readable description of the key
 * 
 * @author i01002492
 * 
 */
public class ColumnMappingOption {

	public static final String SEQUENCE_NR = "sequenceNr";
	public static final String MOBILE = "mobile";
	public static final String EMAIL = "email";
	public static final String CAN_HOST = "canHost";
	public static final String NUMBER_OF_SEATS = "numberOfSeats";
	public static final String COMPLETE_ADDRESS = "completeAddress";
	public static final String CITY = "city";
	public static final String ZIP = "zip";
	public static final String STREET_NR = "streetNr";
	public static final String STREET = "street";
	public static final String ZIP_WITH_CITY = "zipWithCity";
	public static final String STREET_WITH_NR = "streetWithNr";
	public static final String LASTNAME = "lastname";
	public static final String FIRSTNAME = "firstname";
	public static final String FULLNAME = "fullname";

	private String name;

	private String label;

	public ColumnMappingOption() {
	}

	public ColumnMappingOption(String name, String label) {
		this.name = name;
		this.label = label;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Generates a static list with all available column-information types
	 * 
	 * @param messages
	 * @param locale
	 * @return
	 */
	public static List<ColumnMappingOption> generateColumnMappingOptions(final MessageSource messages, final Locale locale) {
		List<ColumnMappingOption> result = new ArrayList<ColumnMappingOption>();
		result.add(new ColumnMappingOption(StringUtils.EMPTY, "Keine Auswahl"));
		result.add(new ColumnMappingOption(FULLNAME, "Kompletter Name"));
		result.add(new ColumnMappingOption(FIRSTNAME, "Vorname"));
		result.add(new ColumnMappingOption(LASTNAME, "Nachname"));
		result.add(new ColumnMappingOption(STREET_WITH_NR, "Strasse + Hausnummer"));
		result.add(new ColumnMappingOption(ZIP_WITH_CITY, "PLZ + Stadt"));
		result.add(new ColumnMappingOption(STREET, "Strasse"));
		result.add(new ColumnMappingOption(STREET_NR, "Hausnummer"));
		result.add(new ColumnMappingOption(ZIP, "PLZ"));
		result.add(new ColumnMappingOption(CITY, "Stadt"));
		result.add(new ColumnMappingOption(COMPLETE_ADDRESS, "Komplette Adresse"));
		result.add(new ColumnMappingOption(NUMBER_OF_SEATS, "Anzahl Plätze (Zahl)"));
		result.add(new ColumnMappingOption(CAN_HOST, "Genügend Anzahl Plätze vorhanden"));
		result.add(new ColumnMappingOption(EMAIL, "Email"));
		result.add(new ColumnMappingOption(MOBILE, "Handy-Nummer"));
		result.add(new ColumnMappingOption(SEQUENCE_NR, "Nummerierung (Reihenfolge)"));
		return result;
	}

	/**
	 * How many column mappings can be performed?
	 * 
	 * @return
	 */
	public static int getNumColumnMappingOptions() {

		// TODO:
		int numColumnMappingOptionsInitial = generateColumnMappingOptions(null, null).size();

		// Subtract redundant options (Example: Name consumes max. 2 columns, but there exist 3 column mappings
		// (firstname,lastname,fullname)
		final int numColumnMappingOptions = numColumnMappingOptionsInitial - 3 - 1 - 1; // Subtract: redundant address, name and numSeats
																						// configs
		return numColumnMappingOptions;
	}
}
