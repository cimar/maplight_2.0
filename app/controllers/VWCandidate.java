package controllers;

import play.mvc.*;
import models.VWCandidates;
public class VWCandidate extends Controller {

    public static void index() {
        render();
    }
    
    public static void sessionDropDown(){
    	renderJSON(VWCandidates.getSessionDropDown());
    	
    }

}
