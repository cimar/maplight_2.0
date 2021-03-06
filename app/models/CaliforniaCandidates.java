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

import java.util.*;

@Entity
@Table(name = "pwr_ca_contrib_cand_committees")
public class CaliforniaCandidates extends Model {

	public String TransactionType;
	public String PrimaryGeneralIndicator;
	public String TransactionID;
	public String TransactionDate;
	public String TransactionAmount;
	public String RecipientCommitteeNameNormalized;
	public String RecipientCandidateNameNormalized;
	public String RecipientCandidateParty;
	public String RecipientCandidateICO;
	public String RecipientCandidateStatus;
	public String RecipientCandidateOffice;
	public String RecipientCandidateDistrict;
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
	// public String UpdateTimestamp;
	public String ElectionCycle;
	public String Election;
	public String FiledDate;
	public String FilerID;
	public String FilerEntity;
	public String Flag;
	@Transient
	private static Boolean noLimit = false;

	public static List<String> getCandidatesNames() {
		return find(
				"SELECT DISTINCT RecipientCandidateNameNormalized FROM CaliforniaCandidates where RecipientCandidateNameNormalized is not null")
				.fetch(); // CaliforniaCandidates").fetch();
	}

	public static List<String> getOfficeNames() {
		return find(
				"SELECT DISTINCT RecipientCandidateOffice FROM CaliforniaCandidates where RecipientCandidateOffice is not null")
				.fetch();
	}

	public static List<JsonObject> getCurrentCandidates() {
		String url = "http://data.maplight.org/FEC/active_incumbents.json";
		HttpResponse res = WS.url(url).get();
		JsonElement json = res.getJson();
		List<JsonObject> ret = new LinkedList<JsonObject>();
		for (JsonElement el : json.getAsJsonArray()) {
			JsonObject obj = el.getAsJsonObject();
			ret.add(obj);
		}
		return ret;
	}

	public static List<String> getCompaniesNames() {
		return find("SELECT DISTINCT DonorOrganization FROM CaliforniaCandidates").fetch();
	}

	public static String getOrEmpty(Params params, String key) {
		return getOrDefault(params, key, "");
	}

	public static String getOrDefault(Params params, String key, String default_) {
		String obj = params.get(key);
		return obj == null ? default_ : obj;
	}

	public static List<CaliforniaCandidates> get(Params params) {

		// String recipient = getOrEmpty(params, "recipient");
		// String donor = getOrEmpty(params, "donor");
		// if (recipient.isEmpty() && donor.isEmpty()) {
		// return new ArrayList<CaliforniaCandidates>();
		// }
		WhereData where = constructWhereClauseFromParams(params);

		// TODO(rkj): ORDER cannot be parametrized in MySQL, I do not have time
		// to escape this on my own...
		// String order = getOrDefault(params, "order", "TransactionAmount DESC");

		// System.out.println(sessions[0].length() > 1?"something":"nothing");

		//
		String sql = "SELECT c FROM CaliforniaCandidates c\n";
		sql = sql + where.create();

		Logger.info(sql.replace("?", "'%s'"), where.data.toArray());
		System.err.printf(sql.replace("?", "'%s'") + "\n", where.data.toArray());
		JPAQuery query = find(sql, where.data.toArray());

		return query.fetch(getLimit(params));
	}

	public static float getTotal(Params params) {
		float returnTotal = 0;
		WhereData where = constructWhereClauseFromParams(params);
		String result = find("SELECT SUM(c.TransactionAmount) FROM CaliforniaCandidates c " + where.create(),
				where.data.toArray()).first();
		try{
			returnTotal = Float.parseFloat(result);
		}catch(Exception e){
			
		}
		return returnTotal;
	}

	private static final int LIMIT = 1000;

	private static int getLimit(Params params) {
		if (!getOrEmpty(params, "download").isEmpty() || !getOrEmpty(params, "raw_results").isEmpty()) {
			Logger.info("In get limit",Integer.MAX_VALUE);
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
		String recipient = getOrEmpty(params, "recipient");
		String donor = getOrEmpty(params, "donor");
		String date_start = getOrEmpty(params, "date_start");
		String date_end = getOrEmpty(params, "date_end");
		String location_from = getOrEmpty(params, "location-from");
		String location_to = getOrEmpty(params, "location-to");
		String[] office = params.getAll("office[]");

		Arrays.toString(office).replaceAll("\\]|\\[", "").split("\\s*,\\s*");

		System.out.println("date start " + date_start);
		System.out.println("date end " + date_end);
		
		WhereData data = new WhereData();
		System.out.println("IN MODEL "+ recipient);
		if (!recipient.isEmpty()) {
			if (recipient.equals("__anyone")) {
				// nothing
			} else if (recipient.equals("office")) {
				if (office != null && !Arrays.toString(office).equalsIgnoreCase("[null]")) {
					System.out.println("OFFICE IS NOT NULL " + Arrays.toString(office));
					String[] officeSelection = Arrays.toString(office).replaceAll("\\]|\\[", "").split("\\s*,\\s*");
					List<String> dummy = new LinkedList<String>();
					for (String _ : officeSelection) {
						dummy.add("?");
					}
					data.append("c.RecipientCandidateOffice IN (" + JavaExtensions.join(dummy, ", ") + ")",
							officeSelection);
				}

				// data.append("c.RecipientCandidateStatus = Won"); // new Object[0]);" //OK SO THIS REQUIRES ADDING A
				// COLUMN TO OUR DATABASE FOR 'CURRENT' MEMBERS
				// System.out.println("data = " + data);
			} else {
				String[] recipients = recipient.split("\\s*;\\s*");
				System.out.println("IN here for recipients");
				List<String> dummy = new LinkedList<String>();
				for (String _ : recipients) {
					dummy.add("?");
				}
				data.append("c.RecipientCandidateNameNormalized IN (" + JavaExtensions.join(dummy, ", ") + ")",
						recipients);
			}
		}

		/*
		 * if (office != null && !Arrays.toString(office).equalsIgnoreCase("[null]") ) {
		 * System.out.println("OFFICE IS NOT NULL "+Arrays.toString(office)); String[] officeSelection =
		 * Arrays.toString(office).replaceAll("\\]|\\[", "").split("\\s*,\\s*"); List<String> dummy = new
		 * LinkedList<String>(); for (String _ : officeSelection) { dummy.add("?"); }
		 * data.append("c.RecipientCandidateOffice IN (" + JavaExtensions.join(dummy, ", ") + ")", officeSelection); }
		 */

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
			return " WHERE " + JavaExtensions.join(conditions, " AND ");
		}
	}
}
