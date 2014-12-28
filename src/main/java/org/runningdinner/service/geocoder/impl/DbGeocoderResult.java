package org.runningdinner.service.geocoder.impl;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.runningdinner.core.ParticipantAddress;
import org.runningdinner.core.model.AbstractEntity;


@NamedQueries({
	@NamedQuery(name=DbGeocoderResult.FIND_BY_NORMALIZED_ADDRESS, query="SELECT dgr FROM DbGeocoderResult dgr WHERE dgr.normalizedAddressString=:normalizedAddressString")
})
@Entity
@Access(AccessType.FIELD)
@Table(uniqueConstraints={ 
			@UniqueConstraint(name="dbGeocoderResultUniqueConstraint", columnNames={"lat", "lng", "normalizedAddressString"} )
		})
public class DbGeocoderResult extends AbstractEntity {

	private static final long serialVersionUID = -2437951767698871442L;
	
	public static final String FIND_BY_NORMALIZED_ADDRESS = "findByNormalizedAddress";

	@Column(nullable = false)
	private double lat;

	@Column(nullable = false)
	private double lng;

	@Column(nullable = false)
	private boolean exact;

	private String street;

	private String streetNr;

	@Column(nullable = false)
	private int zip;

	private String cityName;

	private String formattedAddressString;

	private String normalizedAddressString;

	@Temporal(TemporalType.TIMESTAMP)
	private Date lastAccess;

	public DbGeocoderResult() {
	}

	public DbGeocoderResult(double lat, double lng, boolean exact, int zip) {
		super();
		this.lat = lat;
		this.lng = lng;
		this.exact = exact;
		this.zip = zip;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public boolean isExact() {
		return exact;
	}

	public void setExact(boolean exact) {
		this.exact = exact;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(final String street) {
		this.street = getNormalizedString(street);
	}

	public String getStreetNr() {
		return streetNr;
	}

	public void setStreetNr(final String streetNr) {
		this.streetNr = getNormalizedString(streetNr);
	}

	public int getZip() {
		return zip;
	}

	public void setZip(int zip) {
		this.zip = zip;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(final String cityName) {
		this.cityName = getNormalizedString(cityName);
	}

	public String getFormattedAddressString() {
		return formattedAddressString;
	}

	public void setFormattedAddressString(String formattedAddressString) {
		this.formattedAddressString = formattedAddressString;
	}

	public String getNormalizedAddressString() {
		return normalizedAddressString;
	}

	public void setNormalizedAddressString(String normalizedAddressString) {
		this.normalizedAddressString = normalizedAddressString;
	}

	public Date getLastAccess() {
		return lastAccess;
	}

	public void setLastAccess(Date lastAccess) {
		this.lastAccess = lastAccess;
	}
	
	public static String normalizeAddress(final ParticipantAddress address) {
		String normalizedStreet = getNormalizedString(address.getStreet());
		String normalizedStreetNr = getNormalizedString(address.getStreetNr());
		String result = address.getZip() + " " + normalizedStreet + " " + normalizedStreetNr;
		return result;
	}
	
	protected static String getNormalizedString(final String s) {
		return StringUtils.lowerCase(StringUtils.trim(s));
	}

	@Override
	public String toString() {
		return "(" + lat + "," + lng + "): " + normalizedAddressString;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(137, 11).append(getLat()).append(getLng()).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}

		DbGeocoderResult other = (DbGeocoderResult)obj;
		return new EqualsBuilder().append(getLat(), other.getLng()).isEquals();
	}

	public static class Fields {
		public static final String NORMALIZED_ADDRESS_STRING = "normalizedAddressString";
	}
}
