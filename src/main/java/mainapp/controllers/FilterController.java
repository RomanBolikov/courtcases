package mainapp.controllers;

import org.springframework.stereotype.Component;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import mainapp.data.CaseType;
import mainapp.data.Court;
import mainapp.data.Relation;
import mainapp.data.Representative;
import mainapp.helpers.CaseFilter;
import mainapp.services.CourtService;
import mainapp.services.ReprService;
import net.rgielen.fxweaver.core.FxmlView;

@Component
@FxmlView("setfilters.fxml")
public class FilterController {

	private Stage stage;

	private MainController parent;

	private final CourtService courtService;

	private final ReprService reprService;

	private final CaseFilter caseFilter;

	public FilterController(CaseFilter caseFilter, CourtService courtService, ReprService reprService) {
		this.caseFilter = caseFilter;
		this.courtService = courtService;
		this.reprService = reprService;

	}

	@FXML
	private GridPane gridPane;

	@FXML
	private ChoiceBox<CaseType> caseTypeChoiceBox;

	@FXML
	private ChoiceBox<Relation> relationChoiceBox;

	@FXML
	private ChoiceBox<Representative> representativeChoiceBox;

	@FXML
	private ChoiceBox<Court> courtChoiceBox;

	@FXML
	private DatePicker currDatePicker;

	@FXML
	private TextField plaintiffTextField;

	@FXML
	private TextField defendantTextField;

	@FXML
	protected Button applyFilterButton;

	@FXML
	protected Button dropFilterButton;

	@FXML
	public void initialize() {
		this.stage = new Stage();
		stage.setTitle("Установка фильтров в таблице");
		stage.setResizable(false);
		stage.setScene(new Scene(gridPane));
		caseTypeChoiceBox.setItems(FXCollections.observableArrayList(CaseType.values()));
		relationChoiceBox.setItems(FXCollections.observableArrayList(Relation.values()));
		representativeChoiceBox
				.setItems(reprService.getAllReprs().sorted((r1, r2) -> r1.getName().compareTo(r2.getName())));
		courtChoiceBox.setItems(courtService.getAllCourts().sorted((c1, c2) -> c1.getName().compareTo(c2.getName())));
	}

	@FXML
	private void applyFilters(ActionEvent actionEvent) {
		caseFilter.setRelation(relationChoiceBox.getValue());
		caseFilter.setType(caseTypeChoiceBox.getValue());
		caseFilter.setRepr(representativeChoiceBox.getValue());
		caseFilter.setCourt(courtChoiceBox.getValue());
		caseFilter.setCurrentDate(currDatePicker.getValue());
		caseFilter.setPlaintiff(plaintiffTextField.getText());
		caseFilter.setDefendant(defendantTextField.getText());
		stage.close();
		parent.disableDropFilterButton(false);
		parent.refreshTable();
	}

	@FXML
	private void dropFilters(ActionEvent actionEvent) {
		caseTypeChoiceBox.setValue(null);
		relationChoiceBox.setValue(null);
		representativeChoiceBox.setValue(null);
		courtChoiceBox.setValue(null);
		currDatePicker.setValue(null);
		plaintiffTextField.setText("");
		defendantTextField.setText("");
	}

	public void setParent(MainController parent) {
		this.parent = parent;
	}

	public void show() {
		caseTypeChoiceBox.setValue(caseFilter.getType());
		relationChoiceBox.setValue(caseFilter.getRelation());
		representativeChoiceBox.setValue(caseFilter.getRepr());
		courtChoiceBox.setValue(caseFilter.getCourt());
		currDatePicker.setValue(caseFilter.getCurrentDate());
		stage.show();
	}
}
