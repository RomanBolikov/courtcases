package mainapp.services;


import javafx.collections.ObservableList;
import mainapp.data.Court;
import mainapp.helpers.SaveEntityException;

public interface CourtService {
	
	ObservableList<Court> getAllCourts();

	boolean existsInDB(Court court);
	
	Court findCourtByName(String courtName);
	
	Court findCourtByEntity(Court court);
	
	Court addCourt(Court newCourt) throws SaveEntityException;
	
}
