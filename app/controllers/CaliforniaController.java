package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import org.apache.commons.lang.StringUtils;

import models.*;

public class CaliforniaController extends Controller {
	public static void index() {
		System.out.println("I'm in here!!");
		render();
	}

	// @Before
	public static void renderIndex() {
		String donor = params.get("donor");
		System.out.println("Donor " + donor);
		String recipient = params.get("recipient");
		System.out.println("recipient " + recipient);
		String raw_results = params.get("raw_results");
		System.out.println("raw_results " + raw_results);
		if (Boolean.valueOf(raw_results))
			renderTemplate("CaliforniaController/index.html", donor);
	}

	public static void officeNames() {
		renderJSON(CaliforniaCandidates.getOfficeNames());
		/*
		 * List<String> officeNames = Cache.get("officeNames", List.class); if (officeNames == null) { officeNames =
		 * CaliforniaCandidates.getOfficeNames(); Cache.set("officeNames", officeNames, "10mn"); }
		 * renderJSON(officeNames);
		 */
	}

	public static void byDonors(String donor, String recipient, String date_start, String date_end,
			String location_from, String location_to, String[] office, boolean download, String raw_results) {

		String anyOne = null;
		String candidatesName = null;
		String offices = null;

		renderArgs.put("url", request.url.toString() + "&raw_results=true");

		if (download) {

			List<CaliforniaCandidates> cc = CaliforniaCandidates.get(params);
			System.out.println(params.toString());
			response.setHeader("Content-Disposition", "attachment; filename=download.csv");
			renderTemplate("CaliforniaController/CaliforniaCandidates.csv", cc);

		} else if ("true".equals(raw_results)) {

			System.out.println("inside " + raw_results);
			donor = params.get("donor").trim();
			System.out.println("Donor " + donor);
			recipient = params.get("recipient").trim();
			System.out.println("recipient " + recipient);
			raw_results = params.get("raw_results");
			System.out.println("raw_results " + raw_results);
			location_from = params.get("location-from");
			System.out.println("location-from " + location_from);
			System.out.println(date_start);
			StringUtils.isBlank(office[0]);

			if ("__anyone".equals(recipient)) {
				anyOne = "checked";
				recipient = "";
			} else if ("office".equals(recipient)) {
				offices = "checked";
			} else {
				candidatesName = "checked";
			}
			
			//System.out.println(Arrays.toString(office));
			renderTemplate("CaliforniaController/index.html", anyOne, candidatesName, offices, donor, raw_results,
					recipient, location_from, date_start, date_end, office);
		} else {
			List<CaliforniaCandidates> cc = CaliforniaCandidates.get(params);
			float total = 0;
			// total = CaliforniaCandidates.getTotal(params);
			total = CaliforniaCandidates.getTotal(params);

			renderTemplate("CaliforniaController/CaliforniaCandidates.html", cc, total, donor, recipient, date_start,
					date_end, location_from, location_to);
		}

	}
}
