package controllers;

import java.util.List;

import models.CaliforniaCandidates;
import play.mvc.Controller;

public class CAutoComplete extends Controller {
	public static void candidates() {
		renderJSON(CaliforniaCandidates.getCandidatesNames());
	}
	
	public static void companies() {
		renderJSON(CaliforniaCandidates.getCompaniesNames());
	}
	
	public static void currentCandidates() {
		renderJSON(CaliforniaCandidates.getCurrentCandidates());	
	}
}
