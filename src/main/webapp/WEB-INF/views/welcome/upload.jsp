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
		
		<%--
		<div class="form-group" id="file.form.div">
			<label for="uploadFile" class="control-label"><spring:message code="label.upload.file"/></label>
			<div class="row">
				<div class="col-xs-6">
					<form:input path="file" id="file" type="file" />
					<p class="help-block"><spring:message code="label.upload.file.help"/></p>
				</div>
				<div class="col-xs-4">
					<form:errors path="file" cssClass="control-label"/>
				</div>
			</div>
		</div>
		--%>
				
		<div class="panel panel-primary">
			<div class="panel-heading"><h4 class="panel-title">Einstellungen zum Einlesen der Datei</h4></div>
			
			<div class="panel-body">
				<p class="help-block">Hier kann das Format der Datei mit den Teilnehmern an die eigenen Beduerfnisse angepasst werden.</p>
			
				<bs:inputField name="configuration.startRow" label="Erste Zeile" inputColClass="col-xs-2" />
			
				<table class="table table-striped table-bordered">
				<thead>
					<tr>
						<th>Spalte</th>
						<th>Information</th>
						<th class="td-actions"></th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td>1</td>
						<td>
							<select class="form-control">
								<option selected>Kompletter Name</option>
								<option>Vorname</option>
								<option>Nachname</option>
								<option>Komplette Adresse</option>
								<option>Strasse + HausNr</option>
								<option>PLZ + Stadt</option>
								<option>Strasse</option>
								<option>HausNr</option>
								<option>PLZ</option>
								<option>Stadt</option>
								<option>Anzahl Plaetze (Zahl)</option>
							</select>
						</td>
						<td class="td-actions"><button class="btn btn-danger">Deaktivieren</button></td>
						<!--<td class="td-actions"><a href="#"><span class="glyphicon glyphicon-remove"></span></a></td>-->
					</tr>
					<tr>
						<td>2</td>
						<td>
							<select class="form-control">
								<option>Kompletter Name</option>
								<option>Vorname</option>
								<option>Nachname</option>
								<option>Komplette Adresse</option>
								<option selected>Strasse + HausNr</option>
								<option>PLZ + Stadt</option>
								<option>Strasse</option>
								<option>HausNr</option>
								<option>PLZ</option>
								<option>Stadt</option>
								<option>Anzahl Plaetze (Zahl)</option>
							</select>
						</td>
						<td class="td-actions"><button class="btn btn-danger">Deaktivieren</button></td>
					</tr>
					<tr>
						<td>3</td>
						<td>
							<select class="form-control">
								<option>Kompletter Name</option>
								<option>Vorname</option>
								<option>Nachname</option>
								<option>Komplette Adresse</option>
								<option>Strasse + HausNr</option>
								<option selected>PLZ + Stadt</option>
								<option>Strasse</option>
								<option>HausNr</option>
								<option>PLZ</option>
								<option>Stadt</option>
								<option>Anzahl Plaetze (Zahl)</option>
							</select>
						</td>
						<td class="td-actions"><button class="btn btn-danger">Deaktivieren</button></td>
					</tr>
					<tr>
						<td>4</td>
						<td>
							<select class="form-control">
								<option>Kompletter Name</option>
								<option>Vorname</option>
								<option>Nachname</option>
								<option>Komplette Adresse</option>
								<option>Strasse + HausNr</option>
								<option>PLZ + Stadt</option>
								<option>Strasse</option>
								<option>HausNr</option>
								<option>PLZ</option>
								<option>Stadt</option>
								<option selected>Anzahl Plaetze (Zahl)</option>
							</select>
						</td>
						<td class="td-actions"><button class="btn btn-danger">Deaktivieren</button></td>
					</tr>
					<tr>
						<td>5</td>
						<td>
							<select class="form-control">
								<option>Kompletter Name</option>
								<option>Vorname</option>
								<option>Nachname</option>
								<option>Komplette Adresse</option>
								<option>Strasse + HausNr</option>
								<option>PLZ + Stadt</option>
								<option>Strasse</option>
								<option>HausNr</option>
								<option>PLZ</option>
								<option>Stadt</option>
								<option>Anzahl Plaetze (Zahl)</option>
							</select>
						</td>
						<td class="td-actions"><button class="btn btn-success">Aktivieren</button></td>
					</tr>
					<tr>
						<td>6</td>
						<td>Undefiniert</td>
						<td class="td-actions"><button class="btn btn-success">Aktivieren</button></td>
					</tr>
					</tbody>
					</table>
				</div>
			</div>
		
		
		<input type="hidden" value="2" name="_page" />
		<input type="submit" class="btn btn-primary" value="Weiter" name="_target3" />
		<input type="submit" class="btn btn-danger" value="Abbrechen" name="_cancel" />
			
	</form:form>
		
</div>