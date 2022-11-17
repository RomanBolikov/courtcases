package courtcases.data;

import org.springframework.stereotype.Component;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@Component
public class DatabaseModel {

	@SuppressWarnings("unused")
	private CaseRepo caseRepo;

	public ObservableList<ACase> caseList;

	public DatabaseModel(CaseRepo caseRepo) {
		this.caseRepo = caseRepo;
		this.caseList = FXCollections.observableArrayList(caseRepo.findAll());
	}
}
