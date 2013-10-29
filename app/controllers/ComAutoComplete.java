package controllers;

import java.util.List;

import models.CaliforniaCommittees;
import play.mvc.Controller;

public class ComAutoComplete extends Controller {
	public static void committees() {
		renderJSON(CaliforniaCommittees.getCommitteeNames());
	}
}
