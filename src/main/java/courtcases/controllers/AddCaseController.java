package courtcases.controllers;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import org.springframework.stereotype.Component;

import courtcases.customGUI.AddCourtDialog;
import courtcases.data.ACase;
import courtcases.data.CaseRepo;
import courtcases.data.CaseType;
import courtcases.data.CaseTypeRepo;
import courtcases.data.Court;
import courtcases.data.CourtRepo;
import courtcases.data.Relation;
import courtcases.data.RelationRepo;
import courtcases.data.Representative;
import courtcases.data.RepresentativeRepo;
import courtcases.data.StageRepo;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import net.rgielen.fxweaver.core.FxmlView;

@Component
@FxmlView("addcase.fxml")
public class AddCaseController {
	private Stage stage;

	private FilteredList<Court> courtList;

	public AddCaseController(RepresentativeRepo reprRepo, CaseRepo caseRepo, CaseTypeRepo caseTypeRepo,
			RelationRepo relationRepo, StageRepo stageRepo, CourtRepo courtRepo) {
		this.reprRepo = reprRepo;
		this.caseRepo = caseRepo;
		this.caseTypeRepo = caseTypeRepo;
		this.relationRepo = relationRepo;
		this.stageRepo = stageRepo;
		this.courtRepo = courtRepo;
		this.courtList = new FilteredList<>(FXCollections.observableArrayList(courtRepo.findAll()));
	}

	private final RepresentativeRepo reprRepo;

	private final CaseRepo caseRepo;

	private final CaseTypeRepo caseTypeRepo;

	private final RelationRepo relationRepo;

	private final StageRepo stageRepo;

	private final CourtRepo courtRepo;

// 	FXML GUI elements

	@FXML
	private GridPane gridPane;

	@FXML
	private ChoiceBox<CaseType> caseTypeChoiceBox;

	@FXML
	private ChoiceBox<Relation> relationChoiceBox;

	@FXML
	private ChoiceBox<Representative> representativeChoiceBox;

	@FXML
	private ComboBox<Court> courtComboBox;

	@FXML
	private TextField caseNoTextField;

	@FXML
	private ChoiceBox<courtcases.data.Stage> stageChoiceBox;

	@FXML
	private DatePicker currDatePicker;

	@FXML
	private TextField hourTextField;

	@FXML
	private TextField minuteTextField;

	@FXML
	private TextArea description;

	@FXML
	private TextField plaintiffTextField;

	@FXML
	private TextField defendantTextField;

	@FXML
	private TextArea currentState;

	@FXML
	private Button saveButton;

//  *********************************************

// 	FXML-annotated methods

	@FXML
	public void initialize() {
		this.stage = new Stage();
		stage.setTitle("Добавление нового дела");
		stage.setResizable(false);
		stage.setScene(new Scene(gridPane));
		caseTypeChoiceBox.setItems(FXCollections.observableArrayList(caseTypeRepo.findAll()));
		relationChoiceBox.setItems(FXCollections.observableArrayList(relationRepo.findAll()));
		relationChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> setRestrictions(newVal));
		representativeChoiceBox.setItems(FXCollections.observableArrayList(reprRepo.findAll()));
		stageChoiceBox.setItems(FXCollections.observableArrayList(stageRepo.findAll()));
		courtComboBox.setItems(courtList);
		courtComboBox.getEditor().textProperty().addListener((obs, oldVal, newVal) -> filterCourts(newVal));
		courtComboBox.setConverter(new StringConverter<Court>() {
			@Override
			public String toString(Court court) {
				if (court == null)
					return "";
				return court.getName();
			}

			@Override
			public Court fromString(String string) {
				return new Court(string);
			}
		});
	}

	@FXML
	public void saveCase(ActionEvent actionEvent) {
		if (!isInputCorrect()) {
			displayErrors();
		} else {
			ACase newCase = new ACase(relationChoiceBox.getValue(), caseTypeChoiceBox.getValue(), description.getText(),
					courtComboBox.getValue(), plaintiffTextField.getText(), defendantTextField.getText(),
					stageChoiceBox.getValue(), currentState.getText());
			if (caseNoTextField.getText() != "")
				newCase.setCase_no(caseNoTextField.getText());
			if (representativeChoiceBox.getValue() != null)
				newCase.setRepr(representativeChoiceBox.getValue());
			if (currDatePicker.getValue() != null) {
				LocalDate date = currDatePicker.getValue();
				int hours = 0, mins = 0;
				if (hourTextField.getText() != "" && minuteTextField.getText() != "") {
					hours = Integer.valueOf(hourTextField.getText());
					mins = Integer.valueOf(minuteTextField.getText());
				}
				LocalTime time = LocalTime.of(hours, mins);
				newCase.setCurr_date(Timestamp.valueOf(LocalDateTime.of(date, time)));
			}
			//caseRepo.save(newCase);
			System.out.println(newCase.getStage());
			new Alert(AlertType.CONFIRMATION, "Дело внесено!", ButtonType.OK).show();
			stage.close();
		}
	}

//	***************************************************************************

	public void show() {
		stage.show();
	}

	private void setRestrictions(Relation relation) {
		if (relation.getId() == 1) {
			plaintiffTextField.setText("Минстрой края");
			plaintiffTextField.setDisable(true);
			defendantTextField.setDisable(false);
			defendantTextField.setText("");
		} else if (relation.getId() == 2) {
			plaintiffTextField.setDisable(false);
			plaintiffTextField.setText("");
			defendantTextField.setText("Минстрой края");
			defendantTextField.setDisable(true);
		} else {
			plaintiffTextField.setDisable(false);
			defendantTextField.setDisable(false);
		}
	}

	private void filterCourts(String newVal) {
		if (newVal != "") {
			courtList.setPredicate(c -> c.getName().toLowerCase().startsWith(newVal.toLowerCase()));
			courtComboBox.show();
		} else {
			courtList.setPredicate(null);
			courtComboBox.hide();
		}
	}

	private void addCourtIfNotExisting(Court court) {
		Dialog<String> addCourtDialog = new AddCourtDialog(court.toString());
		Optional<String> courtName = addCourtDialog.showAndWait();
		if (courtName.isPresent()) {
			court.setName(courtName.get());
			courtRepo.save(court);
			courtList.addAll(court);
		}
	}

	private boolean isInputCorrect() {
		// TODO Auto-generated method stub
		return false;
	}

	private void displayErrors() {
		System.out.println("yeah");
	}
}
