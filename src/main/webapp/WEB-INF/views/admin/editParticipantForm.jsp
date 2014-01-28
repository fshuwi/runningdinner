<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="rd" uri="http://org.runningdinner/tags/functions"%>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<h2>Bearbeite ${participant.participantNumber} - ${participant.name.fullnameFirstnameFirst}</h2>

<form:form class="well" method="post" commandName="participant" htmlEscape="true" role="form">

	<bs:inputField name="name.firstnamePart" label="Firstname" inputColClass="col-xs-6"/>
	<bs:inputField name="name.lastname" label="Firstname" inputColClass="col-xs-6"/>
	<bs:inputField name="email" label="EMail Adresse" type="email" inputColClass="col-xs-6"/>
	<bs:inputField name="mobileNumber" label="Handy Nummer" inputColClass="col-xs-6"/>

	<bs:inputField name="address.street" label="Street" inputColClass="col-xs-6"/>
	<bs:inputField name="address.streetNr" label="Street Nr" inputColClass="col-xs-2"/>
	<bs:inputField name="address.zip" label="Zip" inputColClass="col-xs-4"/>
	<bs:inputField name="address.cityName" label="City" inputColClass="col-xs-6"/>
	
	<bs:inputField name="numSeats" label="Anzahl Plätze" inputColClass="col-xs-2"/>
	
	<bs:selectField name="gender" itemLabel="label" itemValue="value" items="${genders}"  label="Geschlecht" />
	
	<input type="submit" class="btn btn-primary" value="Speichern" name="save" />
	<input type="submit" class="btn btn-danger" value="Abbrechen" name="cancel" />
</form:form>