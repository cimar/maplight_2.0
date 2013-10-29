#!/usr/bin/env python

import urllib
import json
import sys

create_table = """
DROP TABLE IF EXISTS Import;
CREATE TABLE Import (
    id BIGINT NOT NULL AUTO_INCREMENT,
    TransactionTypeCode VARCHAR(80),
    TransactionType VARCHAR(80),
    ElectionCycle VARCHAR(80),
    ReportingCommitteeMLID VARCHAR(80),
    ReportingCommitteeFECID VARCHAR(80),
    ReportingCommitteeName VARCHAR(80),
    ReportingCommitteeNameNormalized VARCHAR(80),
    PrimaryGeneralIndicator VARCHAR(80),
    TransactionID VARCHAR(80),
    FileNumber VARCHAR(80),
    RecordNumberML VARCHAR(80),
    RecordNumberFEC VARCHAR(80),
    TransactionDate VARCHAR(80),
    TransactionAmount VARCHAR(80),
    RecipientName VARCHAR(80),
    RecipientNameNormalized VARCHAR(80),
    RecipientCity VARCHAR(80),
    RecipientState VARCHAR(80),
    RecipientZipCode VARCHAR(80),
    RecipientEmployer VARCHAR(80),
    RecipientEmployerNormalized VARCHAR(80),
    RecipientOccupation VARCHAR(80),
    RecipientOccupationNormalized VARCHAR(80),
    RecipientOrganization VARCHAR(80),
    RecipientEntityTypeCode VARCHAR(80),
    RecipientEntityType VARCHAR(80),
    RecipientCommitteeMLID VARCHAR(80),
    RecipientCommitteeFECID VARCHAR(80),
    RecipientCommitteeName VARCHAR(80),
    RecipientCommitteeNameNormalized VARCHAR(80),
    RecipientCommitteeTreasurer VARCHAR(80),
    RecipientCommitteeDesignationCode VARCHAR(80),
    RecipientCommitteeDesignation VARCHAR(80),
    RecipientCommitteeTypeCode VARCHAR(80),
    RecipientCommitteeType VARCHAR(80),
    RecipientCommitteeParty VARCHAR(80),
    RecipientCandidateMLID VARCHAR(80),
    RecipientCandidateFECID VARCHAR(80),
    RecipientCandidateName VARCHAR(80),
    RecipientCandidateNameNormalized VARCHAR(80),
    RecipientCandidateParty VARCHAR(80),
    RecipientCandidateICO VARCHAR(80),
    RecipientCandidateStatus VARCHAR(80),
    RecipientCandidateOfficeState VARCHAR(80),
    RecipientCandidateOffice VARCHAR(80),
    RecipientCandidateDistrict VARCHAR(80),
    RecipientCandidateGender VARCHAR(80),
    DonorName VARCHAR(80),
    DonorNameNormalized VARCHAR(80),
    DonorCity VARCHAR(80),
    DonorState VARCHAR(80),
    DonorZipCode VARCHAR(80),
    DonorEmployer VARCHAR(80),
    DonorEmployerNormalized VARCHAR(80),
    DonorOccupation VARCHAR(80),
    DonorOccupationNormalized VARCHAR(80),
    DonorOrganization VARCHAR(80),
    DonorEntityTypeCode VARCHAR(80),
    DonorEntityType VARCHAR(80),
    DonorCommitteeMLID VARCHAR(80),
    DonorCommitteeFECID VARCHAR(80),
    DonorCommitteeName VARCHAR(80),
    DonorCommitteeNameNormalized VARCHAR(80),
    DonorCommitteeTreasurer VARCHAR(80),
    DonorCommitteeDesignationCode VARCHAR(80),
    DonorCommitteeDesignation VARCHAR(80),
    DonorCommitteeTypeCode VARCHAR(80),
    DonorCommitteeType VARCHAR(80),
    DonorCommitteeParty VARCHAR(80),
    DonorCandidateMLID VARCHAR(80),
    DonorCandidateFECID VARCHAR(80),
    DonorCandidateName VARCHAR(80),
    DonorCandidateNameNormalized VARCHAR(80),
    DonorCandidateParty VARCHAR(80),
    DonorCandidateICO VARCHAR(80),
    DonorCandidateStatus VARCHAR(80),
    DonorCandidateOfficeState VARCHAR(80),
    DonorCandidateOffice VARCHAR(80),
    DonorCandidateDistrict VARCHAR(80),
    DonorCandidateGender VARCHAR(80),
    UpdateTimestamp VARCHAR(80),
    PRIMARY KEY (id)
  );
"""

def load_file_string(csv):
  return """
LOAD DATA LOCAL INFILE '{0}' INTO TABLE Import FIELDS TERMINATED BY ',' ENCLOSED BY '"' LINES TERMINATED BY '\\n' IGNORE 1 LINES (
    TransactionTypeCode,
    TransactionType,
    ElectionCycle,
    ReportingCommitteeMLID,
    ReportingCommitteeFECID,
    ReportingCommitteeName,
    ReportingCommitteeNameNormalized,
    PrimaryGeneralIndicator,
    TransactionID,
    FileNumber,
    RecordNumberML,
    RecordNumberFEC,
    TransactionDate,
    TransactionAmount,
    RecipientName,
    RecipientNameNormalized,
    RecipientCity,
    RecipientState,
    RecipientZipCode,
    RecipientEmployer,
    RecipientEmployerNormalized,
    RecipientOccupation,
    RecipientOccupationNormalized,
    RecipientOrganization,
    RecipientEntityTypeCode,
    RecipientEntityType,
    RecipientCommitteeMLID,
    RecipientCommitteeFECID,
    RecipientCommitteeName,
    RecipientCommitteeNameNormalized,
    RecipientCommitteeTreasurer,
    RecipientCommitteeDesignationCode,
    RecipientCommitteeDesignation,
    RecipientCommitteeTypeCode,
    RecipientCommitteeType,
    RecipientCommitteeParty,
    RecipientCandidateMLID,
    RecipientCandidateFECID,
    RecipientCandidateName,
    RecipientCandidateNameNormalized,
    RecipientCandidateParty,
    RecipientCandidateICO,
    RecipientCandidateStatus,
    RecipientCandidateOfficeState,
    RecipientCandidateOffice,
    RecipientCandidateDistrict,
    RecipientCandidateGender,
    DonorName,
    DonorNameNormalized,
    DonorCity,
    DonorState,
    DonorZipCode,
    DonorEmployer,
    DonorEmployerNormalized,
    DonorOccupation,
    DonorOccupationNormalized,
    DonorOrganization,
    DonorEntityTypeCode,
    DonorEntityType,
    DonorCommitteeMLID,
    DonorCommitteeFECID,
    DonorCommitteeName,
    DonorCommitteeNameNormalized,
    DonorCommitteeTreasurer,
    DonorCommitteeDesignationCode,
    DonorCommitteeDesignation,
    DonorCommitteeTypeCode,
    DonorCommitteeType,
    DonorCommitteeParty,
    DonorCandidateMLID,
    DonorCandidateFECID,
    DonorCandidateName,
    DonorCandidateNameNormalized,
    DonorCandidateParty,
    DonorCandidateICO,
    DonorCandidateStatus,
    DonorCandidateOfficeState,
    DonorCandidateOffice,
    DonorCandidateDistrict,
    DonorCandidateGender,
    UpdateTimestamp);
""".format(csv)

add_current_field = """
ALTER TABLE Import CHANGE TransactionAmount TransactionAmount Integer;
ALTER TABLE Import ADD Current Boolean;
"""

def update_current():
  url = 'http://data.maplight.org/FEC/active_incumbents.json'
  data = json.load(urllib.urlopen(url))
  str = []
  str.append("UPDATE Import SET Current = False;")
  str.append("UPDATE Import SET Current = True WHERE RecipientCandidateMLID IN (")
  for person in data:
    str.append("\t{0},".format(person['person_id']))
  str.append("-1);")
  return "\n".join(str)

swap_tables = """
DROP TABLE IF EXISTS OldData;
CREATE TABLE IF NOT EXISTS CandidateContributions (id integer);
ALTER TABLE CandidateContributions RENAME OldData;
ALTER TABLE Import RENAME CandidateContributions;
"""

create_indexes = """
CREATE INDEX candidate_name ON Import (RecipientCandidateNameNormalized);
CREATE INDEX donor_name ON Import (DonorNameNormalized);
CREATE INDEX donor_org ON Import (DonorOrganization);
CREATE INDEX donor_state ON Import (DonorState);
CREATE INDEX donor_city ON Import (DonorCity);
CREATE INDEX transaction_date ON Import (TransactionDate);
CREATE INDEX candidate_state ON Import (RecipientCandidateOfficeState);
"""

file = sys.argv[1] if len(sys.argv) > 1  else "data.csv"
print(create_table)
print(load_file_string(file))
print(add_current_field)
print(update_current())
print(create_indexes)
print(swap_tables)

