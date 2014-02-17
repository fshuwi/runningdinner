<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<tiles:useAttribute name="meals" id="meals" classname="java.util.Collection" />

<c:forEach items="${meals}" var="meal" varStatus="loopCounter">
	<div class="form-group">
		<div class="row">
			<div class="col-xs-2">
				<label id="meal-${loopCounter.count}" class="control-label meal-label">${meal.label}</label>
				<input type="text" class="form-control meal-time" value="<fmt:formatDate  value="${meal.time}" pattern="HH:mm"/>" id="time-${loopCounter.count}"/>
			</div>
		</div>
	</div>
</c:forEach>