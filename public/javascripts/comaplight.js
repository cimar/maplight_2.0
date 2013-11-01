$(function() {

	function addslashes(str) {
		str=str.replace(/\\/g,'\\\\');
		str=str.replace(/\'/g,'\\\'');
		str=str.replace(/\"/g,'\"');
		str=str.replace(/\0/g,'\\0');
		return str;
		}

	$("#election-tooltip").tooltip({
		content: "<p style='font-size:12px; line-height: 16px;'>Some ballot measure campaign committees support or oppose more than one proposition per election. Searching for all contributions within an election will return multiple instances of contributions for these committees.</p>",
		position: { my: "left+15 center", at: "right center" }

	});
	

	$("#proposition-select").multiselect();

	$('#ballot-measure').cascadingDropdown({
		selectBoxes : [ {
			selector : '.step1',
			source : function(request, response) {
				$.getJSON('/api/electionDropDown', request, function(data) {
					var selectOnlyOption = data.length <= 1;
					response($.map(data, function(item, index) {
						return {
							label : item,
							value : item,
							selected : selectOnlyOption
						// Select if only option
						};
					}));
				});
			}
		}, {
			selector : '.step2',
			requires : [ '.step1' ],
			source : function(request, response) {
				$.getJSON('/api/propositionsByDate', request, function(data) {
					var selectOnlyOption = data.length <= 1;
					response($.map(data, function(item, index) {
						return {
							label : item,
							value : addslashes(item),
							selected : selectOnlyOption
						// Select if only option
						};
					}));
				});
			}

		}, {
			selector : '.step3',
			requires : [ '.step1', '.step2' ],
			requireAll : true,
			source : [ {
				label : 'Support',
				value : 'Support'
			}, {
				label : 'Oppose',
				value : 'Oppose'
			} ]

		} ]
	});

	$("#filter-committee-single-data").tooltip({
		content : "Type something",
		position : {
			my : "left+15 center",
			at : "right center"
		}
	});

	var enableMultiAutocomplete = function(el, values) {
		var split = function(val) {
			return val.split(/,\s*/);
		};
		// console.log("VALUES: "+values);
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
						// delegate back to autocomplete, but extract the last
						// term
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
						// add placeholder to get the comma-and-space at the end
						terms.push("");
						this.value = terms.join(", ");
						return false;
					}
				});
	};

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
      						"committee": readData("filter-committee"),
							"election" : $("#filter-election").val(),
							"proposition" : $("#filter-prop").val(),
							"position" : $("#filter-pos").val() == "position" ? "all" : $("#filter-pos").val() ,
							"allied_committee_bool" : $("#allied-committee-filter:checked").val(),
							"location_from" : $("#filter-location-from").val(),
							"location-to" : $("#filter-location-to").val(),
							"date_start" : $("#filter-date-start").val(), // changed
							"date_end" : $("#filter-date-end").val(), // changed
						};

						console.log(requestData);
						$('.query-results').hide();
						$('.query-loading').show();
						$
								.get(
								// "/api/donor",
								"/comapi/donor", requestData)
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

});

	$("#filter-committee-single-data").tooltip({
		content: "To search for multiple committees, separate their names with a semi-colon.  <i>e.x. THE PIXIES; BELLE & SEBASTIAN</i>",
		position: { my: "left+15 center", at: "right center" }
	});


	var enableMultiAutocomplete = function(el, values) {
    var split = function( val ) {
      return val.split( /;\s*/ );
    };
    console.log(values);
    el
      // don't navigate away from the field on tab when selecting an item
      .bind( "keydown", function( event ) {
        if ( event.keyCode === $.ui.keyCode.TAB &&
            $( this ).data( "ui-autocomplete" ).menu.active ) {
          event.preventDefault();
        }
      })
      .autocomplete({
        minLength: 0,
        source: function( request, response ) {
        	console.log("request, response =", request.term, response);
          var extractLast = function( term ) {
            return split( term ).pop();
          };
          // delegate back to autocomplete, but extract the last term
          response( $.ui.autocomplete.filter(
            values, extractLast( request.term ) ) );
        },
        focus: function() {
          // prevent value inserted on focus
          return false;
        },
        select: function( event, ui ) {
          var terms = split( this.value );
          // remove the current input
          terms.pop();
          // add the selected item
          terms.push( ui.item.value );
          // add placeholder to get the semi-colon-and-space at the end
          terms.push( "" );
          this.value = terms.join( "; " );
          return false;
        }
      });
  };

  $.get("/api/comautocomplete/committees")
    .done(function(candidateNames) {
    	var committee_ac = $("[autocomplete-type=committee]");
    	console.log('committee = ', committee_ac);
      enableMultiAutocomplete(committee_ac, candidateNames);
    });
  
  $(document).ready(function () {
	  console.log($("#raw_results").val());
	  if( $("#raw_results").val() == "true"){
		  $(".run-query").click();
	  }
	});