<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div id="myCarousel" class="carousel slide" data-ride="carousel">
    <!-- Wrapper for slides -->
    <div class="carousel-inner">
        <div class="item active">
        	<div class="carousel-description explanationBg">
        		<div class="carousel-headline"><h3>Um was geht es?</h3></div>
        		<div><p><spring:message code="text.runningdinner.explanation" /></p></div>
        	</div>
        </div>
        <div class="item">
        	<div class="carousel-description organizeBg">
        		<div class="carousel-headline"><h3>Selbst veranstalten</h3></div>
        		<div><p><spring:message code="text.runningdinner.explanation.selfmade" /></p></div>
        	</div>
        </div>
        <div class="item">
            <div class="carousel-description step1Bg">
        		<div class="carousel-headline"><h3>Schritt 1: Neues Dinner anlegen</h3></div>
        		<div><p><spring:message code="text.runningdinner.explanation.step1" /></p></div>
        	</div> 
        </div>
        <div class="item">
            <div class="carousel-description step2Bg">
        		<div class="carousel-headline"><h3>Schritt 2: Dinner verwalten</h3></div>
        		<div><p><spring:message code="text.runningdinner.explanation" /></p></div>
        	</div>   
        </div>
    </div>

    <ul class="nav nav-pills nav-justified">
        <li data-target="#myCarousel" data-slide-to="0" class="active"><a href="#" style="outline:none;">Running Dinner<small>Um was geht es?</small></a></li>
        <li data-target="#myCarousel" data-slide-to="1"><a href="#" style="outline:none;">Berechnung<small>Selbst veranstalten</small></a></li>
        <li data-target="#myCarousel" data-slide-to="2"><a href="#" style="outline:none;">How To (1)<small>Schritt 1: Neues Dinner anlegen</small></a></li>
        <li data-target="#myCarousel" data-slide-to="3"><a href="#" style="outline:none;">How To (2)<small>Schritt 2: Dinner verwalten</small></a></li>
    </ul>
</div>
<!-- End Carousel -->