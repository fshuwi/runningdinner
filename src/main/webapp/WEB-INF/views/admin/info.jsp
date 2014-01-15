<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2>${runningDinner.title}</h2>
<c:if test="${not empty runningDinner.city}">
	<p>Wo: <span class="text-success"><strong>${runningDinner.city}</strong></span></p>
</c:if>
<p>Wann: <span class="text-success"><strong><fmt:formatDate type="date" value="${runningDinner.date}" dateStyle="SHORT" /></strong></span></p>
<div class="row">
	<div class="col-xs-4">
		<c:forEach items="${runningDinner.configuration.mealClasses}" var="meal">
			<span class="label label-primary"><fmt:formatDate type="time" value="${meal.time}" timeStyle="SHORT" /> Uhr</span> <span class="badge">${meal.label}</span><br/>
		</c:forEach>
	</div>
</div>