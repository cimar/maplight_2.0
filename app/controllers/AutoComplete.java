package controllers;

import java.util.List;

import models.FECCandidates;
import models.CaliforniaCandidates;
import play.mvc.Controller;

public class AutoComplete extends Controller {
	public static void candidates() {
		renderJSON(FECCandidates.getCandidatesNames());
	}
	
	public static void companies() {
		renderJSON(FECCandidates.getCompaniesNames());
	}
	
	public static void currentCandidates() {
		renderJSON(FECCandidates.getCurrentCandidates());	
	}
}
