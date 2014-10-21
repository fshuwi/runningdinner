<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>

<spring:message code="label.upload.file" var="fileLabel"/>
<spring:message code="label.upload.file.help" var="fileHelpText" />
<spring:message code="tooltip.upload.parse.startrow" var="startRowTooltip" />

<spring:message code="label.upload.parse.row.active" var="rowActiveLabel" />
<spring:message code="label.upload.parse.row.inactive" var="rowInactiveLabel" />

<h2><spring:message code="label.runningdinner.upload.headline"/></h2>
<div class="well">
		
	<form:form enctype="multipart/form-data" method="post" id="startForm3" modelAttribute="uploadFileModel" action="wizard-upload" htmlEscape="true" role="form">
		
		<bs:inputField name="file" label="${fileLabel}" type="file" helpForInput="${fileHelpText}" inputColClass="col-xs-6"/>
				
		<div class="panel panel-primary">
			<div class="panel-heading"><h4 class="panel-title"><spring:message code="headline.upload.parse.settings" /></h4></div>
			
			<div class="panel-body">
				<p class="help-block"><spring:message code="text.upload.parse.settings" /></p>
			
				<bs:inputField name="startRow" label="Erste Zeile" inputColClass="col-xs-1" inputTooltip="${startRowTooltip}" />
						
				<spring:bind path="columnMappings">
					<c:if test="${status.error}">
						<div class="form-group has-error">
							<div class="row">
								<div class="col-xs-12">
									<span class="control-label">${status.errorMessage}</span>
								</div>
							</div>
						</div>
					</c:if>
				</spring:bind>		
							
				<table class="table table-striped table-bordered">
					<thead>
						<tr>
							<th><spring:message code="label.upload.parse.row" /></th>
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
						 					<span id="status_${columnMapping.key}" class="label label-success">${rowActiveLabel}</span>
						 				</c:when>
						 				<c:otherwise>
						 					<span  id="status_${columnMapping.key}" class="label label-danger">${rowInactiveLabel}</span>
						 				</c:otherwise>
						 			</c:choose>
						 		</td>
						 	</tr>
						 </c:forEach>
					</tbody>
				</table>
								
				<p class="help-block">
					<a data-toggle="modal" data-target="#uploadHelp" style="cursor:pointer;"><spring:message code="label.upload.parse.help"/></a>
				</p>
				
			</div>
		</div>
		
		<input type="hidden" value="2" name="_page" />
		<input type="submit" class="btn btn-primary" value="<spring:message code="label.next" />" name="_target3" />
		<input type="submit" class="btn btn-danger" value="<spring:message code="label.cancel" />" name="_cancel" />
			
	</form:form>
		
</div>

<div class="modal fade" id="uploadHelp" tabindex="-1" role="dialog" aria-labelledby="#uploadHelpLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h4 class="modal-title" id="uploadHelpLabel"><spring:message code="headline.upload.parse.settings" /></h4>
      </div>
      <div class="modal-body">
      		<c:url value="/resources/images/demo.png" var="demoImageUrl" />
      		<c:url value="/resources/files/demo.xls" var="demoFileUrl" />
      		<spring:message code="label.upload.parse.help.explanation" arguments="<img src='${demoImageUrl}'/>,<a href='${demoFileUrl}' target='_blank'>Download</a>"/>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary" data-dismiss="modal"><spring:message code="label.close"/></button>
      </div>
    </div>
  </div>
</div>