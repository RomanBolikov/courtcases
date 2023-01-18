package mainapp.controllers;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Optional;

import javax.persistence.OptimisticLockException;

import org.springframework.stereotype.Component;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import mainapp.customGUI.CustomAlert;
import mainapp.data.ACase;
import mainapp.data.CaseRepo;
import mainapp.data.CaseTypeRepo;
import mainapp.data.Court;
import mainapp.data.CourtRepo;
import mainapp.data.DatePickerConverter;
import mainapp.data.Relation;
import mainapp.data.RelationRepo;
import mainapp.data.RepresentativeRepo;
import mainapp.data.StageRepo;
import net.rgielen.fxweaver.core.FxmlView;

@Component
@FxmlView("editcase.fxml")
public class EditCaseController extends AbstractCaseController {
	private Stage stage;

	private MainController parent;

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
		if (!isInputCorrect()) {
			displayErrors();
			return;
		}
		Court court = courtComboBox.getValue(); // what was specified by user
		Optional<Court> courtInDB = courtRepo.findByName(court.getName()); // try to lookup court in database
		if (!courtInDB.isPresent()) { // if the court is not in DB then it's saved to it and set as case property
			court = courtRepo.save(court);
			caseToEdit.setCourt(court);
		} else if (!court.getName().equals(caseToEdit.getCourt().getName())) { // if such a court exists in DB then
			caseToEdit.setCourt(courtInDB.get()); // we simply set it as case property
		}
		if (!caseNoTextField.getText().isEmpty())
			caseToEdit.setCase_no(caseNoTextField.getText());
		String repr = representativeChoiceBox.getValue().getName();
		if (!repr.equals(caseToEdit.getRepr().getName()))
			caseToEdit.setRepr(reprRepo.findByName(repr));
		if (currDatePicker.getValue() != null && !hourTextField.getText().isEmpty()
				&& !minuteTextField.getText().isEmpty()) {
			try {
				int hours = Integer.parseInt(hourTextField.getText());
				int mins = Integer.parseInt(minuteTextField.getText());
				caseToEdit.setCurr_date(DatePickerConverter.convertToTimestamp(currDatePicker.getValue(), hours, mins));
			} catch (NumberFormatException e) {
				caseToEdit.setCurr_date(null);
			}
		} else
			caseToEdit.setCurr_date(null);
		if (!description.getText().equals(caseToEdit.getTitle()))
			caseToEdit.setTitle(description.getText());
		if (!caseNoTextField.getText().equals(caseToEdit.getCase_no()))
			caseToEdit.setCase_no(caseNoTextField.getText());
		if (!plaintiffTextField.getText().equals(caseToEdit.getPlaintiff()))
			caseToEdit.setPlaintiff(plaintiffTextField.getText());
		if (!defendantTextField.getText().equals(caseToEdit.getDefendant()))
			caseToEdit.setDefendant(defendantTextField.getText());
		if (!currentState.getText().equals(caseToEdit.getCurr_state()))
			caseToEdit.setCurr_state(currentState.getText());
		try {
			caseToEdit = caseRepo.save(caseToEdit);
			new CustomAlert("Подтверждение", "", "Дело внесено в базу данных!", ButtonType.OK).show();
			parent.refreshTable();
		} catch (OptimisticLockException ole) {
			new CustomAlert("Обновление данных", "", "Параметры дела изменены другим пользователем!", ButtonType.OK)
					.show();
			parent.refreshTable();
		} finally {
			stage.close();
		}
	}

//	***************************************************************************

	public void show(ACase caseToEdit) {
		this.caseToEdit = caseToEdit;
		Relation relation = caseToEdit.getRelation();
		Timestamp timestamp = caseToEdit.getCurr_date();
		LocalDate date = DatePickerConverter.extractLocalDate(timestamp);
		int[] time = DatePickerConverter.extractTime(timestamp);

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
		currDatePicker.setValue(date);
		if (date != null)
			currDatePicker.getEditor()
					.setStyle(date.isBefore(LocalDate.now()) ? "-fx-text-fill: red" : "-fx-text-fill: black");
		if (time.length > 1) {
			hourTextField.setText(String.format("%02d", time[0]));
			minuteTextField.setText(String.format("%02d", time[1]));
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
		currDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
			hourTextField.setText("");
			minuteTextField.setText("");
		});
		stage.show();
	}

	public void setParent(MainController parent) {
		this.parent = parent;
	}
}
