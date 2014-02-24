<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<footer style="margin-bottom:20px;">
	<div class="row">
	
		<hr/>
	 
		<div class="col-sm-4 col-sm-offset-1">
			<ul class="footer-list">
				<li><span class="muted">© 2013 Clemens Stich</span></li>
				<li><a href="#">Impressum</a></li>
				<li class="last"><a href="#">Datenschutz</a></li>
			</ul>
		</div>
		<div class="col-sm-2">&nbsp;</div>
		<div class="col-sm-4 col-sm-offset-1">
			<ul class="footer-list">
				<li><a href="#">Über</a></li>
				<li><a href="<spring:eval expression="@globalProperties['github.repo.url']" />" target="_blank">Sourcecode auf Github</a></li>
				<li class="last"><a href="mailto:<spring:eval expression="@globalProperties['contact.mail']" />">Kontakt</a></li>		
			</ul>
		</div>
	</div>
</footer>