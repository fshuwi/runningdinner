<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<h2><spring:message code="label.runningdinner.participantlist.headline"/></h2>

<tiles:insertDefinition name="view-participants" />