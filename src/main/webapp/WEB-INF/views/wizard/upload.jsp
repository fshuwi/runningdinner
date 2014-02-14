<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>

<spring:message code="label.upload.file" var="fileLabel"/>
<spring:message code="label.upload.file.help" var="fileHelpText" />

<h2><spring:message code="label.runningdinner.upload.headline"/></h2>
<div class="well">
		
	<form:form enctype="multipart/form-data" method="post" id="startForm3" modelAttribute="uploadFileModel" action="wizard-upload" htmlEscape="true" role="form">
		
		<bs:inputField name="file" label="${fileLabel}" type="file" helpForInput="${fileHelpText}" inputColClass="col-xs-6"/>
				
		<div class="panel panel-primary">
			<div class="panel-heading"><h4 class="panel-title">Einstellungen zum Einlesen der Datei</h4></div>
			
			<div class="panel-body">
				<p class="help-block">Hier kann das Format der Datei mit den Teilnehmern an die eigenen Beduerfnisse angepasst werden.</p>
			
				<bs:inputField name="startRow" label="Erste Zeile" inputColClass="col-xs-1" inputTooltip="Ab welcher Zeile beginnen die 'eigentlichen' Daten" />
			
				<table class="table table-striped table-bordered">
					<thead>
						<tr>
							<th>Spalte</th>
							<th>Information</th>
							<th class="td-actions"></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${uploadFileModel.columnMappings}" var="columnMapping">
						 	<tr>
						 		<td>${columnMapping.key + 1}</td>
						 		<td>
						 			<select class="form-control" id="select_${columnMapping.key}" name="columnMappings['${columnMapping.key}']" 
						 					onchange="toggleColumnMappingStatus('select_${columnMapping.key}', 'status_${columnMapping.key}')">
						 				<c:forEach items="${columnMappingOptionItems}" var="optionItem">
						 					<option value="${optionItem.name}" <c:if test="${columnMapping.value == optionItem.name}">selected</c:if>>${optionItem.label}</option>
						 				</c:forEach>
						 			</select>
						 		</td>
						 		<td>
						 			<c:choose>
						 				<c:when test="${not empty columnMapping.value}">
						 					<span id="status_${columnMapping.key}" class="label label-success">Spalte aktiv</span>
						 				</c:when>
						 				<c:otherwise>
						 					<span  id="status_${columnMapping.key}" class="label label-danger">Spalte deaktiviert</span>
						 				</c:otherwise>
						 			</c:choose>
						 		</td>
						 	</tr>
						 </c:forEach>
					</tbody>
				</table>
				
				<p class="help-block"><a href="#">Nichts kapiert? Hilfe!</a></p>
				
			</div>
		</div>
		
		<input type="hidden" value="2" name="_page" />
		<input type="submit" class="btn btn-primary" value="Weiter" name="_target3" />
		<input type="submit" class="btn btn-danger" value="Abbrechen" name="_cancel" />
			
	</form:form>
		
</div>