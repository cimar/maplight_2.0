$(function() {
	$('#html-form').submit(function() {
	    $('#loadingtiny').show();
	    $.post('/whatever.php', function() {
	        $('#loadingtiny').hide();
	    });
	    return false;
	});
});