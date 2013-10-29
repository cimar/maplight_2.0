package controllers;

import java.util.List;

import models.CandidateContributions;
import models.CaliforniaCandidates;
import play.mvc.Controller;

public class AutoComplete extends Controller {
	public static void candidates() {
		renderJSON(CandidateContributions.getCandidatesNames());
	}
	
	public static void companies() {
		renderJSON(CandidateContributions.getCompaniesNames());
	}
	
	public static void currentCandidates() {
		renderJSON(CandidateContributions.getCurrentCandidates());	
	}
}
