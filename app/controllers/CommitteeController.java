package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class CommitteeController extends Controller {
	public static void index() {
		System.out.println("I'm in committees!!");
		render();
	}

	public static void electionDropDown() {
		renderJSON(CaliforniaCommittees.getElectionDropDown());

	}

	public static void propositionDropDown(String electionCycle) {
		renderJSON(CaliforniaCommittees.getPropositionByDate(electionCycle));

	}

	public static void byDonors(String donor, String election, String proposition, String position, String date_start,
			String date_end, String location_from, String location_to, boolean download, String raw_results,
			String committee) {

		String ballotMeasure = null;
		String anyMeasure = null;
		String specificMeasure = null;

		// List<CaliforniaCommittees> cc = CaliforniaCommittees.get(params);

		renderArgs.put("url", request.url.toString() + "&raw_results=true");

		if (download) {
			
			System.out.println(params.allSimple());
			List<CaliforniaCommittees> cc = CaliforniaCommittees.get(params);
			response.setHeader("Content-Disposition", "attachment; filename=download.csv");
			renderTemplate("CommitteeController/CaliforniaCommittees.csv", cc);
		} else if ("true".equals(raw_results)) {

			if ("__any".equals(committee)) {
				anyMeasure = "checked";
			} else if ("__ballot_measure".equals(committee)) {
				ballotMeasure = "checked";
			} else {
				specificMeasure = "checked";
			}

			System.out.println("IN RAW RESULTS");
			proposition = CaliforniaCommittees.decodeString(proposition);
			
			renderTemplate("CommitteeController/index.html", anyMeasure, ballotMeasure, specificMeasure, raw_results,
					donor, date_start, date_end, location_from, location_to, election, proposition, position);

		} else {
			float total = 0;

			total = CaliforniaCommittees.getTotal(params);
			List<CaliforniaCommittees> cc = CaliforniaCommittees.get(params);
			renderTemplate("CommitteeController/CaliforniaCommittees.html", cc, total, donor, date_start, date_end,
					location_from, location_to);
		}

	}
}
