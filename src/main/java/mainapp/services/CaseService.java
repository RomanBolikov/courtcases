package mainapp.services;

import javafx.collections.ObservableList;
import mainapp.data.ACase;
import mainapp.data.Representative;
import mainapp.helpers.SaveCaseException;

public interface CaseService {
	
	ObservableList<ACase> getAllCases();
	
	ObservableList<ACase> getCasesByRepr(Representative repr);
	
	ObservableList<ACase> getCasesByRelation(String relation);
	
	ACase getCaseById(int id);
	
	ACase addCase(ACase acase) throws SaveCaseException;

	ACase updateCase(int id, ACase updatedCase) throws SaveCaseException;
	
}
