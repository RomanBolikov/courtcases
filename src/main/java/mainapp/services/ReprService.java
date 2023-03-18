package mainapp.services;

import javafx.collections.ObservableList;
import mainapp.data.Representative;
import mainapp.helpers.SaveEntityException;

public interface ReprService {
	
	ObservableList<Representative> getAllReprs();
			
	Representative addNewRepr(Representative newRepr) throws SaveEntityException;

	Representative updateRepr(int id, Representative updatedRepr) throws SaveEntityException;
}
