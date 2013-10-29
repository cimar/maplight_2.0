package models;

import play.*;
import play.db.jpa.*;
import play.db.jpa.GenericModel.JPAQuery;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.mvc.Scope.Params;
import play.templates.JavaExtensions;

import javax.persistence.*;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

@Entity
@Table(name = "pwr_ca_contrib_other_committees")
public class CaliforniaCommittees extends Model {

	// Fields of the class have to the be the same case as the fields in the database.
	// TODO: Change the fields to match those of the database

	public String TransactionType;
	public String PrimaryGeneralIndicator;
	public String TransactionID;
	public String TransactionDate;
	public String TransactionAmount;
	public String RecipientCommitteeNameNormalized;
	public String Target;
	public String Position;
	public String DonorNameNormalized;
	public String DonorCity;
	public String DonorState;
	public String DonorZipCode;
	public String DonorEmployerNormalized;
	public String DonorOccupationNormalized;
	public String DonorOrganization;
	public String DonorIndustry;
	public String DonorEntityType;
	public String DonorCommitteeID;
	public String DonorCommitteeNameNormalized;
	public String DonorCommitteeType;
	public String DonorCommitteeParty;
	public String ElectionCycle;
	public String Election;
	public String FiledDate;
	public String FilerID;
	public String FilerEntity;
	public String Flag;

	public static List<String> getElectionDropDown() {
		return find("SELECT DISTINCT Election FROM CaliforniaCommittees WHERE Election is not null").fetch();
	}

	public static List<String> getPropositionByDate(String election) {
		return find("SELECT DISTINCT Target FROM CaliforniaCommittees WHERE Election = ? order by Target", election).fetch();
	}
	
	//SELECT DISTINCT Target FROM CaliforniaCommittees WHERE ElectionCycle = 

	
	public static List<String> getCommitteeNames() { return find(
	//		"SELECT DISTINCT RecipientCandidateNameNormalized FROM CaliforniaCandidates where RecipientCandidateNameNormalized is not null").fetch();

	  "SELECT DISTINCT RecipientCommitteeNameNormalized FROM CaliforniaCommittees where RecipientCommitteeNameNormalized is not null").fetch();
	}// CaliforniaCommittees").fetch(); }
	  
 

	public static List<String> getCompaniesNames() {
		return find("SELECT DISTINCT DonorOrganization FROM CaliforniaCommittees").fetch();
	}

	public static String getOrEmpty(Params params, String key) {
		return getOrDefault(params, key, "");
	}

	public static String getOrDefault(Params params, String key, String default_) {
		String obj = params.get(key);
		return obj == null ? default_ : obj;
	}

	public static List<CaliforniaCommittees> get(Params params) {
		// String recipient = getOrEmpty(params, "recipient");
		// String donor = getOrEmpty(params, "donor");
		// if (recipient.isEmpty() && donor.isEmpty()) {
		// return new ArrayList<CaliforniaCommittees>();
		// }
		WhereData where = constructWhereClauseFromParams(params);
		// TODO(rkj): ORDER cannot be parametrized in MySQL, I do not have time
		// to escape this on my own...
		// String order = getOrDefault(params, "order", "TransactionAmount DESC");

		// System.out.println(sessions[0].length() > 1?"something":"nothing");

		// if (params.getAll("sessions") != null){
		// System.out.println("In here");
		// sessions = params.getAll("sessions");
		// System.out.println(sessions[0]);
		// }
		//
		String ally_check = getOrEmpty(params, "allied_committee_bool");
		
		String sql = "SELECT c FROM CaliforniaCommittees c\n";
		
		if(ally_check != ""){
			System.out.println("ally check = "+ally_check);
			//sql = sql + "LEFT JOIN (SELECT DISTINCT FilerID, Election, Target, Position FROM CaliforniaCommittees) a ON c.DonorCommitteeID = a.FilerID AND c.Election = a.Election AND c.Target = a.Target AND c.Position = a.Position WHERE c.Target LIKE '%30%' AND a.FilerID is null";
		}
		
		sql = sql + where.create();

		Logger.info(sql.replace("?", "'%s'"), where.data.toArray());
		System.err.printf(sql.replace("?", "'%s'") + "\n", where.data.toArray());
		JPAQuery query = find(sql, where.data.toArray());
		return query.fetch(getLimit(params));
	}

	public static Float getTotal(Params params) {
		float returnTotal = 0;
		WhereData where = constructWhereClauseFromParams(params);
		String result = find("SELECT SUM(c.TransactionAmount) FROM CaliforniaCommittees c " + where.create(),
				where.data.toArray()).first();
		try{
			returnTotal = Float.parseFloat(result);
		}catch(Exception e){
			
		}
		return returnTotal;
	}

	private static final int LIMIT = 1000;

	private static int getLimit(Params params) {
		if (!getOrEmpty(params, "download").isEmpty()) {
			return Integer.MAX_VALUE;
		}
		int limit = LIMIT;
		try {
			limit = Integer.valueOf(getOrEmpty(params, "limit"));
			if (limit <= 0) {
				limit = LIMIT;
			}
		} catch (Exception _) {
			// leave default value
		}
		return limit;
	}

	private static WhereData constructWhereClauseFromParams(Params params) {
		String committee = getOrEmpty(params, "committee");
		String proposition = getOrEmpty(params, "proposition");
		String recipient = getOrEmpty(params, "recipient");
		String donor = getOrEmpty(params, "donor");
		String date_start = getOrEmpty(params, "date_start");
		String date_end = getOrEmpty(params, "date_end");
		String location_from = getOrEmpty(params, "location-from");
		String location_to = getOrEmpty(params, "location-to");
		String election = getOrEmpty(params, "election");
		String position = getOrEmpty(params, "position");
		System.out.println("Electio var="+ election);
		String ally_check = getOrEmpty(params, "allied_committee_bool");

		try {
			proposition = URLDecoder.decode(getOrEmpty(params, "proposition"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("committeeeee: "+committee);

		WhereData data = new WhereData();
		
		System.out.println(committee);
		
		if (!committee.isEmpty()) {
			if (committee.equals("__any")) {
				System.out.print("INSIDE ANY");

			} else if (committee.equals("__ballot_measure")) {
				System.out.print("INSIDE BALLOT");

				if (!election.isEmpty() && !election.equalsIgnoreCase("all")) {
					if (proposition.isEmpty() || proposition.equalsIgnoreCase("all")){
						data.append(" c.Election  = ? ", election);
					}
				}
				if (!position.isEmpty() && !position.equalsIgnoreCase("all")) {				
					data.append(" c.Position = ? ", position);
				}
				if (!proposition.isEmpty() && !proposition.equalsIgnoreCase("all")) {
					System.out.print("INSIDE INSIDE");
					data.append(" c.Target = ? ", proposition);
				}
				System.out.println("data = " + data);
			} else {
				System.out.print("INSIDE THESE");
				String[] committees = committee.split("\\s*;\\s*");
				List<String> dummy = new LinkedList<String>();
				for (String _ : committees) {
					dummy.add("?");
				}
				data.append("c.RecipientCommitteeNameNormalized IN (" + JavaExtensions.join(dummy, "; ") + ")",
						committees);
			}
		}



		if (!donor.isEmpty()) {
			String closeDonor = "%" + donor + "%";
			data.append("(c.DonorNameNormalized LIKE ? OR c.DonorOrganization LIKE ?)", closeDonor, closeDonor);
		}
		if (!date_start.isEmpty()) {
			data.append("TransactionDate >= ?", date_start);
		}
		if (!date_end.isEmpty()) {
			data.append("TransactionDate <= ?", date_end);
		}
		if (!location_from.isEmpty()) {
			data.append("(DonorState = ? OR DonorCity = ?)", location_from, location_from);
		}
		if (!location_to.isEmpty()) {
			data.append("RecipientCandidateOfficeState = ?", location_to);
		}
		if(ally_check != ""){
			data.append("c.Flag = 0");
		}
		return data;
	}

	private final static class WhereData {
		List<String> conditions = new LinkedList<String>();
		List<Object> data = new LinkedList<Object>();

		public WhereData append(String condition, Object... objects) {
			conditions.add(condition);
			if (objects != null) {
				data.addAll(Arrays.asList(objects));
			}
			return this;
		}

		public String create() {
			if (conditions.size() <= 0) {
				return "";
			}
			return "WHERE " + JavaExtensions.join(conditions, " AND ");
		}
	}
}
