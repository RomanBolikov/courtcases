package courtcases.controllers;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import org.springframework.stereotype.Component;

import courtcases.customGUI.CustomAlert;
import courtcases.data.ACase;
import courtcases.data.CaseRepo;
import courtcases.data.CaseTypeRepo;
import courtcases.data.Court;
import courtcases.data.CourtRepo;
import courtcases.data.Relation;
import courtcases.data.RelationRepo;
import courtcases.data.RepresentativeRepo;
import courtcases.data.StageRepo;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import net.rgielen.fxweaver.core.FxmlView;

@Component
@FxmlView("editcase.fxml")
public class EditCaseController extends FormController {
	private Stage stage;

	private ACase caseToEdit;

	private final CaseRepo caseRepo;

	private final RepresentativeRepo reprRepo;

	private final CaseTypeRepo caseTypeRepo;

	private final RelationRepo relationRepo;

	private final StageRepo stageRepo;

	private final CourtRepo courtRepo;

	public EditCaseController(CaseRepo caseRepo, RepresentativeRepo reprRepo, CaseTypeRepo caseTypeRepo,
			RelationRepo relationRepo, StageRepo stageRepo, CourtRepo courtRepo) {
		this.caseRepo = caseRepo;
		this.reprRepo = reprRepo;
		this.caseTypeRepo = caseTypeRepo;
		this.relationRepo = relationRepo;
		this.stageRepo = stageRepo;
		this.courtRepo = courtRepo;
	}

// 	FXML-annotated methods

	@Override
	@FXML
	public void initialize() {
		this.stage = new Stage();
		stage.setTitle("Редактирование дела");
		stage.setResizable(false);
		stage.setScene(new Scene(gridPane));
		caseTypeChoiceBox.setItems(FXCollections.observableArrayList(caseTypeRepo.findAll()));
		relationChoiceBox.setItems(FXCollections.observableArrayList(relationRepo.findAll()));
		representativeChoiceBox.setItems(FXCollections.observableArrayList(reprRepo.findAll()));
		stageChoiceBox.setItems(FXCollections.observableArrayList(stageRepo.findAll()));
		courtComboBox.setItems(FXCollections.observableArrayList(courtRepo.findAll()));
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
		// set restrictions on inputs into hour and minute textfields
		hourTextField.focusedProperty().addListener((obs, oldValue, newValue) -> {
			if (!newValue) {
				if (!hourTextField.getText().matches("[0-1][0-9]")) {
					hourTextField.setText("");
					new CustomAlert("Ошибка ввода", "", "Неверный ввод в поле \"часы\"", ButtonType.OK).show();
				}
			}
		});

		minuteTextField.focusedProperty().addListener((obs, oldValue, newValue) -> {
			if (!newValue) {
				if (!minuteTextField.getText().matches("[0-6][05]")) {
					minuteTextField.setText("");
					new CustomAlert("Ошибка ввода", "", "Неверный ввод в поле \"минуты\"", ButtonType.OK).show();
				}
			}
		});
			
		// limit input into TextAreas to 300 characters
		description.setTextFormatter(
				new TextFormatter<String>(change -> change.getControlNewText().length() <= 300 ? change : null));
		
		currentState.setTextFormatter(
				new TextFormatter<String>(change -> change.getControlNewText().length() <= 300 ? change : null));
	}
	
	@Override
	@FXML
	public void saveCase(ActionEvent actionEvent) {
		if (isInputCorrect()) {
			Court court = courtComboBox.getValue();
			Optional<Court> courtInDB = courtRepo.findByName(court.getName());
			court = courtInDB.isPresent() ? courtInDB.get() : courtRepo.save(court);
			ACase newCase = new ACase(relationChoiceBox.getValue(), caseTypeChoiceBox.getValue(), description.getText(),
					court, plaintiffTextField.getText(), defendantTextField.getText(), stageChoiceBox.getValue(),
					currentState.getText());
			if (caseNoTextField.getText() != "")
				newCase.setCase_no(caseNoTextField.getText());
			if (representativeChoiceBox.getValue() != null)
				newCase.setRepr(representativeChoiceBox.getValue());
			if (currDatePicker.getValue() != null && hourTextField.getText() != "" && minuteTextField.getText() != "") {
				try {
					int hours = Integer.parseInt(hourTextField.getText());
					int mins = Integer.parseInt(minuteTextField.getText());
					LocalDate date = currDatePicker.getValue();
					LocalTime time = LocalTime.of(hours, mins);
					newCase.setCurr_date(Timestamp.valueOf(LocalDateTime.of(date, time)));
				} catch (NumberFormatException e) {
					newCase.setCurr_date(null);
				}
			}
			caseRepo.save(caseToEdit);
			new CustomAlert("Подтверждение", "", "Дело внесено в базу данных!", ButtonType.OK).show();
			stage.close();
		} else 
			displayErrors();
	}

//	***************************************************************************

	public void show(ACase caseToEdit) {
		this.caseToEdit = caseToEdit;
		Relation relation = caseToEdit.getRelation();
		Timestamp curr_date = caseToEdit.getCurr_date();
		LocalDate date;
		int hours, mins;

		// setting inputs according to the current data stored in DB
		plaintiffTextField.setText(caseToEdit.getPlaintiff());
		defendantTextField.setText(caseToEdit.getDefendant());
		caseTypeChoiceBox.setValue(caseToEdit.getCase_type());
		relationChoiceBox.setValue(relation);
		representativeChoiceBox.setValue(caseToEdit.getRepr());
		stageChoiceBox.setValue(caseToEdit.getStage());
		courtComboBox.setValue(caseToEdit.getCourt());
		caseNoTextField.setText(caseToEdit.getCase_no());
		description.setText(caseToEdit.getTitle());
		currentState.setText(caseToEdit.getCurr_state());
		if (curr_date != null) {
			date = curr_date.toLocalDateTime().toLocalDate();
			hours = curr_date.toLocalDateTime().getHour();
			mins = curr_date.toLocalDateTime().getMinute();
			currDatePicker.setValue(date);
			hourTextField.setText(String.valueOf(hours));
			minuteTextField.setText(String.valueOf(mins));
		}

		// adding listeners to avoid extra DB operations
		relationChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> setRestrictions(newVal));
		relationChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> caseToEdit.setRelation(newVal));
		caseTypeChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> caseToEdit.setCase_type(newVal));
		representativeChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> caseToEdit.setRepr(newVal));
		stageChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> caseToEdit.setStage(newVal));
		courtComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
			Court court = newVal;
			Optional<Court> courtInDB = courtRepo.findByName(court.getName());
			if (courtInDB.isPresent())
				court = courtInDB.get();
			else
				court = courtRepo.save(court);
			caseToEdit.setCourt(court);
		});
//		currDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
//			if (currDatePicker.getValue() != null && hourTextField.getText() != "" && minuteTextField.getText() != "") {
//				try {
//					LocalTime time = LocalTime.of(
//							Integer.parseInt(hourTextField.getText()), Integer.parseInt(minuteTextField.getText()));
//					caseToEdit.setCurr_date(Timestamp.valueOf(LocalDateTime.of(currDatePicker.getValue(), time)));
//				} catch (NumberFormatException e) {
//					caseToEdit.setCurr_date(null);
//				}
//			}
//		});
//		TODO: read StringPropertyValues from text inputs
		stage.show();
	}
}
