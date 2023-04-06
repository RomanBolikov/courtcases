package mainapp.services;

import java.util.List;

import javafx.collections.ObservableList;
import mainapp.data.ACase;
import mainapp.data.Representative;
import mainapp.helpers.SaveEntityException;

public interface CaseService {
	
	ObservableList<ACase> getAllCases();
	
	List<ACase> getCasesByRepr(Representative repr);
	
	List<ACase> getCasesByCaseNo(String caseNo);
	
	ObservableList<ACase> getCasesByRelation(String relation);
	
	ACase getCaseById(int id);
	
	ACase saveCase(ACase acase) throws SaveEntityException;
	
}
