function toggleErrorView(errorTextId, showText, hideText) {
	if ($("#"+errorTextId).hasClass("errorTextInvisible")) {
		$("#"+errorTextId).toggleClass("errorTextInvisible",false);
		$("#"+errorTextId).toggleClass("errorTextVisible", true);
		$("a#"+errorTextId+"_label").text(hideText); // "Hide Technical Error Information"
	} else {
		$("#"+errorTextId).toggleClass("errorTextVisible", false);	
		$("#"+errorTextId).toggleClass("errorTextInvisible",true);
		$("a#"+errorTextId+"_label").text(showText); // "Show Technical Error Information"			
	}
}

// Used when creating and editing meals (create wizard and admin area)
function saveMealTimesToModel() {
	var meals = [];
	
	$('.meal-label').each(function() {
		var label = $(this).text();
		var time = $(this).next().val();
		meals.push({"label":label, "time":time});
	});
	
	$('#meals').val(JSON.stringify(meals));
}


function getSuccessBox(successLabel, successMessage) {
   var responseMessage = "<div class='alert alert-success alert-dismissable' style='margin-bottom:0px ! important;'>";
   responseMessage += "<button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-hidden=\"true\">&times;</button>";
   responseMessage += "<strong>" + successLabel + "</strong> " + successMessage;
   responseMessage += "</div>";
   return responseMessage;
}

function getErrorBox(errorLabel, errorMessage) {
    var responseMessage = "<div class='alert alert-danger alert-dismissable' style='margin-bottom:0px ! important;'>";
    responseMessage += "<button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-hidden=\"true\">&times;</button>";
    responseMessage += "<strong>" + errorLabel + "</strong> " + errorMessage;
    responseMessage += "</div>";
    return responseMessage;
 }


(function($) {
	/**
	 * attaches a character counter to each textarea element in the jQuery object
	 * usage: $("#myTextArea").charCounter(max, settings);
	 */
	
	$.fn.charCounter = function (max, settings) {
		max = max || 100;
		settings = $.extend({
			container: "<span></span>",
			classname: "charcounter",
			format: "(%1 characters remaining)",
			pulse: true,
			delay: 0
		}, settings);
		var p, timeout;
		
		function count(el, container) {
			el = $(el);
			if (el.val().length > max) {
				el.val(el.val().substring(0, max));
				if (settings.pulse && !p) {
					pulse(container, true);
				}
			}
			if (settings.delay > 0) {
				if (timeout) {
					window.clearTimeout(timeout);
				}
				timeout = window.setTimeout(function () {
					container.html(settings.format.replace(/%1/, (max - el.val().length)));
				}, settings.delay);
			} else {
				container.html(settings.format.replace(/%1/, (max - el.val().length)));
			}
		}
		
		function pulse(el, again) {
			if (p) {
				window.clearTimeout(p);
				p = null;
			}
			el.animate({ opacity: 0.1 }, 100, function () {
				$(this).animate({ opacity: 1.0 }, 100);
			});
			if (again) {
				p = window.setTimeout(function () { pulse(el); }, 200);
			}
		}
		
		return this.each(function () {
			var container;
			if (!settings.container.match(/^<.+>$/)) {
				// use existing element to hold counter message
				container = $(settings.container);
			} else {
				// append element to hold counter message (clean up old element first)
				$(this).next("." + settings.classname).remove();
				container = $(settings.container)
								.insertAfter(this)
								.addClass(settings.classname);
			}
			$(this)
				.unbind(".charCounter")
				.bind("keydown.charCounter", function () { count(this, container); })
				.bind("keypress.charCounter", function () { count(this, container); })
				.bind("keyup.charCounter", function () { count(this, container); })
				.bind("focus.charCounter", function () { count(this, container); })
				.bind("mouseover.charCounter", function () { count(this, container); })
				.bind("mouseout.charCounter", function () { count(this, container); })
				.bind("paste.charCounter", function () { 
					var me = this;
					setTimeout(function () { count(me, container); }, 10);
				});
			if (this.addEventListener) {
				this.addEventListener('input', function () { count(this, container); }, false);
			}
			count(this, container);
		});
	};

})(jQuery);
