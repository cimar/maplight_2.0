$(function() {

	$('#filter-recipient-single').on('click', function() {
		$('.filter-recipient-single-data').focus();
	});

	
	$.ajax(
			{
				type : "GET",
				url : '/capi/office',
				success : function(data) {
					var congress_select = $("#office");

					var d = $("#office").find('option:selected').map(
							function() {
								return $(this).text();
							});
					console.log(d[1]);
					var match = false;

					for ( var x = 0; x < data.length; x++) {

						for ( var y = 0; y < d.length; y++) {
							if (d[y] == data[x]) {
								match = true;
							}
						}
						if (!match) {
							congress_select.append($('<option></option>').val(
									data[x]).text(data[x]));
						}
						match = false;
					}

					$("#office").multiselect({
						//header : false
						noneSelectedText : "Candidates by office..."
					});

				}
			}).done(function() {
				$("#office").multiselect({
					click: function(event, ui){
				       $("#filter-recipient-all").prop("checked",true);
				    }
				});

	});

	$("#date").tooltip({
		content : "Earliest entries are for 2001 election cycle",
		position : {
			my : "left+15 center",
			at : "right center"
		}
	});

	$("#filter-recipient-single-data")
			.tooltip(
					{
						content : "To search for multiple candidates, separate their names with a semi-colon.  <i>e.x. PAVLEY, FRAN; AMMIANO, TOM</i>",
						position : {
							my : "left+15 center",
							at : "right center"
						}
					});

	var enableMultiAutocomplete = function(el, values) {
		var split = function(val) {
			return val.split(/;\s*/);
		};
		console.log(values);
		el
		// don't navigate away from the field on tab when selecting an item
		.bind(
				"keydown",
				function(event) {
					if (event.keyCode === $.ui.keyCode.TAB
							&& $(this).data("ui-autocomplete").menu.active) {
						event.preventDefault();
					}
				}).autocomplete(
				{
					minLength : 0,
					source : function(request, response) {
						console.log("request, response =", request.term,
								response);
						var extractLast = function(term) {
							return split(term).pop();
						};
						// delegate back to autocomplete, but extract the last term
						response($.ui.autocomplete.filter(values,
								extractLast(request.term)));
					},
					focus : function() {
						// prevent value inserted on focus
						return false;
					},
					select : function(event, ui) {
						var terms = split(this.value);
						// remove the current input
						terms.pop();
						// add the selected item
						terms.push(ui.item.value);
						// add placeholder to get the semi-colon-and-space at the end
						terms.push("");
						this.value = terms.join("; ");
						return false;
					}
				});
	};

	$.get("/api/cautocomplete/candidates").done(function(candidateNames) {
		var candidate_ac = $("[autocomplete-type=candidate]");
		console.log('candidate_ac = ', candidate_ac);
		enableMultiAutocomplete(candidate_ac, candidateNames);
	});

	// $.widget( "custom.catcomplete", $.ui.autocomplete, {
	//   _renderMenu: function( ul, items ) {
	//     var that = this,
	//       currentCategory = "";
	//     $.each( items, function( index, item ) {
	//       if ( item.category != currentCategory ) {
	//         ul.append( "<li class='ui-autocomplete-category'>" + item.category + "</li>" );
	//         currentCategory = item.category;
	//       }
	//       that._renderItemData( ul, item );
	//     });
	//   }
	// });

	$("[autocomplete-type=location]").autocomplete({
		delay : 0,
		source : window.maplight.states
	});

	$(".run-query").unbind("click");

	$(".run-query")
			.click(
					function() {
						$(".run-query-error").hide();

						var showError = function(msg) {
							$(".run-query-error").text(msg);
							$('.query-results').hide();
							$('.query-loading').hide();
							$(".run-query-error").show();
						};

						var readData = function(radioGroupName) {
							var toData = $(
									"input[name=" + radioGroupName
											+ "]:checked")
									.attr("data-selector");
							console.log(toData);
							return toData && $(toData).val() || "";
						};

						var requestData = {
							"donor" : $("#filter-donor").val(),
							"recipient" : readData("filter-recipient"),
							"location-from" : $("#filter-location-from").val(),
							"location-to" : $("#filter-location-to").val(),
							"date_start" : $("#filter-date-start").val(), //changed
							"date_end" : $("#filter-date-end").val(), //changed
							"office" : $("#office").val()
						};

						//console.log(requestData);
						$('.query-results').hide();
						$('.query-loading').show();
						$
								.get(
								//      "/api/donor",
								"/capi/donor", requestData)
								.done(
										function(resp) {
											$(".query-results").html(resp);
											$('.query-loading').hide();
											$('.query-results').show();
											$("#data-table")
													.dataTable(
															{
																bPaginate : true,
																oLanguage : {
																	sSearch : "Filter: ",
																	sInfo : "Showing _START_ to _END_ of _TOTAL_ entries <br/> <i>*The maximum number of entries displayed on this page is 1,000.  To view the entire set of entries satisfying your query, download the CSV.  Please cite as MapLight analysis of campaign finance data based on latest contributions from California Secretary of State.</i>"
																}
															});
										}).fail(function() {
									showError("Error while fetching data!");
								});
						return false;
					});

	$(document).ready(function() {

		console.log($("#raw_results").val());
		if ($("#raw_results").val() == "true") {
			$(".run-query").click();
		}
	});

});
