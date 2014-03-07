<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<c:if test="${statusMessage != null}">
	<div class="alert alert-danger">
		${statusMessage.message}
	</div>
	<hr/>
</c:if>


<form method="POST">
	<input type="text" name="id" value="${id}" readonly="readonly"/><br/>
	<input type="submit" class="btn btn-primary" value="Submit" name="submit" />
</form>