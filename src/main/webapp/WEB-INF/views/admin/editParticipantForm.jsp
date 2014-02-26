<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="rd" uri="http://org.runningdinner/tags/functions"%>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<h3 class="contentheadline">${participant.name.fullnameFirstnameFirst} (Nr: ${participant.participantNumber})</h3>

<tiles:insertDefinition name="view-status-info" flush="true"/>

<spring:message code="label.firstname" var="firstnameLabel"/>
<spring:message code="label.lastname" var="lastnameLabel"/>
<spring:message code="label.email" var="emailLabel"/>
<spring:message code="label.mobilenr" var="mobileLabel"/>
<spring:message code="label.street" var="streetLabel"/>
<spring:message code="label.streetnr" var="streetNrLabel"/>
<spring:message code="label.zip" var="zipLabel"/>
<spring:message code="label.city" var="cityLabel"/>
<spring:message code="label.numseats" var="numSeatsLabel"/>
<spring:message code="label.gender" var="genderLabel"/>

<form:form class="well" method="post" commandName="participant" htmlEscape="true" role="form">

	<bs:inputField name="name.firstnamePart" label="${firstnameLabel}" inputColClass="col-xs-6"/>
	<bs:inputField name="name.lastname" label="${lastnameLabel}" inputColClass="col-xs-6"/>
	<bs:inputField name="email" label="${emailLabel}" type="email" inputColClass="col-xs-6"/>
	<bs:inputField name="mobileNumber" label="${mobileLabel}" inputColClass="col-xs-6"/>

	<bs:inputField name="address.street" label="${streetLabel}" inputColClass="col-xs-6"/>
	<bs:inputField name="address.streetNr" label="${streetNrLabel}" inputColClass="col-xs-2"/>
	<bs:inputField name="address.zip" label="${zipLabel}" inputColClass="col-xs-4"/>
	<bs:inputField name="address.cityName" label="${cityLabel}" inputColClass="col-xs-6"/>
	
	<bs:inputField name="numSeats" label="${numSeatsLabel}" inputColClass="col-xs-2"/>
	
	<bs:selectField name="gender" itemLabel="label" itemValue="value" items="${genders}" label="${genderLabel}" />
	
	<input type="submit" class="btn btn-primary" value="<spring:message code="label.save" />" name="save" />
	<input type="submit" class="btn btn-danger" value="<spring:message code="label.cancel" />" name="cancel" />
</form:form>