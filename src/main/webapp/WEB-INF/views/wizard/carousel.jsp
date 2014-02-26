<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div id="myCarousel" class="carousel slide" data-ride="carousel">
    <!-- Wrapper for slides -->
    <div class="carousel-inner">
        <div class="item active">
        	<div class="carousel-description explanationBg">
        		<div class="carousel-headline"><h3><spring:message code="text.runningdinner.explanation.subheadline" /></h3></div>
        		<div><p><spring:message code="text.runningdinner.explanation" /></p></div>
        	</div>
        </div>
        <div class="item">
        	<div class="carousel-description organizeBg">
        		<div class="carousel-headline"><h3><spring:message code="text.runningdinner.explanation.selfmade.subheadline" /></h3></div>
        		<div><p><spring:message code="text.runningdinner.explanation.selfmade" /></p></div>
        	</div>
        </div>
        <div class="item">
            <div class="carousel-description step1Bg">
        		<div class="carousel-headline"><h3><spring:message code="text.runningdinner.explanation.step1.subheadline" /></h3></div>
        		<div><p><spring:message code="text.runningdinner.explanation.step1" /></p></div>
        	</div> 
        </div>
        <div class="item">
            <div class="carousel-description step2Bg">
        		<div class="carousel-headline"><h3><spring:message code="text.runningdinner.explanation.step2.subheadline" /></h3></div>
        		<div><p><spring:message code="text.runningdinner.explanation.step2" /></p></div>
        	</div>
        </div>
    </div>
    
    <ul class="nav nav-pills nav-justified">
        <li data-target="#myCarousel" data-slide-to="0" class="active">
        	<a href="#" style="outline:none;">
        		Running Dinner<small><spring:message code="text.runningdinner.explanation.subheadline" /></small>
        	</a>
        </li>
        <li data-target="#myCarousel" data-slide-to="1">
        	<a href="#" style="outline:none;"><spring:message code="text.runningdinner.explanation.selfmade.headline" />
        		<small><spring:message code="text.runningdinner.explanation.selfmade.subheadline" /></small>
        	</a>
        </li>
        <li data-target="#myCarousel" data-slide-to="2">
        	<a href="#" style="outline:none;"><spring:message code="text.runningdinner.explanation.step1.headline" />
        		<small><spring:message code="text.runningdinner.explanation.step1.subheadline" /></small>
        	</a>
        </li>
        <li data-target="#myCarousel" data-slide-to="3">
        	<a href="#" style="outline:none;"><spring:message code="text.runningdinner.explanation.step2.headline" />
        		<small><spring:message code="text.runningdinner.explanation.step2.subheadline" /></small>
        	</a>
        </li>
    </ul>
</div>
<!-- End Carousel -->