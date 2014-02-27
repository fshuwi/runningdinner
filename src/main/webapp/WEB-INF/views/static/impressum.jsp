<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<p>
	<address>
	  <strong>Clemens Stich</strong><br/>
	  79100 Freiburg<br/>
	  <abbr title="Email"><spring:message code="label.contact"/>:</abbr> <spring:eval expression="@globalProperties['contact.mail']" />
	</address>
</p>

<p><spring:message code="text.impressum.disclaimer"/></p>
<br/><spring:message code="text.disclaimer"/>
