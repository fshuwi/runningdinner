function highlightErrors(inputElementIds) {
	var i=0;
	for (i=0; i<inputElementIds.length; i++) {
		inputElementId = inputElementIds[i];
		inputElement = $('#' + inputElementId + '\\.errors');
		if (inputElement.length > 0) {
			// $(inputElement).addClass('control-label');
			$('#' + inputElementId + '\\.form\\.div').addClass('has-error');
		}
	}
}