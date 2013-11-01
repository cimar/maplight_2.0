$(function() {
  // var queryTypes = {
  //   donor: {
  //     selector: ".refine-donor",
  //     url: "/api/donor"
  //   },
  //   geographic: {
  //     selector: ".refine-geographic",
  //     url: "/api/geographic"
  //   }
  // };

  // $("input[name=query-type]").change(function() {
  //   var $el = $(this);

  //   $(".refine").children().hide();

  //   if ($el.is(":checked")) {
  //     $(queryTypes[$el.val()].selector).show();
  //   }
  // });

  // Autocomplete multiple, courtesy of http://jqueryui.com/autocomplete/#multiple



	$(document).on('click','th', function(e){
		var header = $(this); 
		console.log(header.context.firstChild.data);
	});
	
	  $.ajax({
          type: "GET",
          url: '/api/sessionDropDown',
          success: function (data) {
             var congress_select = $("#congress-select");
             console.log("HEYHEYHEY"+congress_select);
             for(x = 0; x < data.length; x++){
            	 //
            	 //console.log(congress_select.append($('<option></option>').val(data[x]).html(data[x])));
            	 congress_select.append($('<option></option>').val(data[x]).text(data[x]));
             }
             $("#congress-select").multiselect({
     			//header : false
     		});
          }
      });
	
	 
	
	
	$("#filter-recipient-single-data").tooltip({
		content: "To search for multiple candidates, separate their names with a comma.  <i>e.x. John Boehner, Al Franken</i>",
		position: { my: "left+15 center", at: "right center" }
	});
	
	var enableMultiAutocomplete = function(el, values) {
    var split = function( val ) {
      return val.split( /,\s*/ );
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
          // add placeholder to get the comma-and-space at the end
          terms.push( "" );
          this.value = terms.join( ", " );
          return false;
        }
      });
  };

  $.get("/api/autocomplete/candidates")
    .done(function(candidateNames) {
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
      delay: 0,
      source: window.maplight.states
  });

  $(".run-query").unbind("click");
  $(".run-query").click(function() {
    $(".run-query-error").hide();

    var showError = function(msg) {
      $(".run-query-error").text(msg);
      $('.query-results').hide();
      $('.query-loading').hide();
      $(".run-query-error").show();
    };

   

    var readData = function(radioGroupName) {
      var toData = $("input[name=" + radioGroupName + "]:checked").attr("data-selector");
      return toData && $(toData).val() || "";
    };

    var requestData = {
      "donor": $("#filter-donor").val(),
      "recipient": readData("filter-recipient"),
      "location-from": $("#filter-location-from").val(),
      "location-to": $("#filter-location-to").val(),
      "date_start": $("#filter-date-start").val(), //changed
      "date_end": $("#filter-date-end").val(), //changed
      "sessions": $("#congress-select").val()
    };
    
    var currentDate = new Date();

    console.log(requestData);
    $('.query-results').hide();
    $(' .query-loading').show();
    $.post(
      "/api/donor",
      requestData
    ).done(function(resp) {
      $(".query-results").html(resp);
      $('.query-loading').hide();
      $('.query-results').show();
      $("#data-table").dataTable({
        bPaginate: true,
        oLanguage: {
          sSearch: "Filter: ",
          sInfo: "Showing _START_ to _END_ of _TOTAL_ entries <br/> <i>*The maximum number of entries displayed on this page is 100.  To view the entire set of entries satisfying your query, download the CSV. Please cite as MapLight analysis of campaign finance data based on latest contributions from California Secretary of State as of</i> "+currentDate 
        }
      });
    }).fail(function() {
      showError("Error while fetching data!");
    });
    return false;
  });
  
  
  
});
