<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page pageEncoding="UTF-8" %>


<div class="modal-dialog">
  <div class="modal-content">
    <div class="modal-header">
      <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
      <h4 class="modal-title" id="dialogLabel">${title}</h4>
    </div>
    <div class="modal-body"><tiles:insertAttribute name="content" /></div>
    <div class="modal-footer">
      <button type="button" class="btn btn-primary" data-dismiss="modal"><spring:message code="label.close"/></button>
    </div>
  </div>
</div>