package models;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

//import models.CandidateContributions;

import java.util.*;

@Entity
@Table(name = "vw_candidates")
public class VWCandidates extends GenericModel {
	
	@Id
	public int candidate_id;
	public String display_name;
	public String election;
	public String election_year;
	public String statecode;
	public String office_level;
	public String local_level;
	public String local_name;
	public String office_body;
	public String office_title;
	public int district;
	public String district_sort;
	public String description;
	public int person_id;
	@Column(name = "recipient_fec_id", insertable = false, updatable = false)
	public String recipient_fec_id;
	
	@ManyToOne(targetEntity = CandidateContributions.class)
	@JoinColumn(name = "recipient_fec_id", referencedColumnName = "RecipientCandidateFECID")
 	public List<CandidateContributions> candidateContribution;

//	@ManyToOne(targetEntity = FECCandidates.class)
//	@JoinColumn(name = "recipient_fec_id", referencedColumnName = "RecipientCandidateFECID")
//	public List<FECCandidates> fecCandidate;
	
	public static List<String> getSessionDropDown(){
		return find("SELECT DISTINCT description FROM VWCandidates WHERE description LIKE '%Congress%' AND election_year > 0 AND election_year < 2014 ORDER BY election_year DESC").fetch();
		
	}
	
	
}


