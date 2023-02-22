 package mainapp.controllers;

import java.sql.Timestamp;
import java.time.LocalDate;

import javax.persistence.OptimisticLockException;

import org.springframework.stereotype.Component;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import mainapp.customGUI.CustomAlert;
import mainapp.data.ACase;
import mainapp.data.Court;
import mainapp.data.CourtRepo;
import mainapp.data.DataModel;
import mainapp.data.DatePickerConverter;
import mainapp.data.Relation;
import net.rgielen.fxweaver.core.FxmlView;

@Component
@FxmlView("editcase.fxml")
public class EditCaseController extends AbstractCaseController {

	private Stage stage;

	private MainController parent;
	
	private ObservableList<ACase> caseList;

	private ACase caseToEdit;

	private final DataModel model;

	public EditCaseController(DataModel model) {
		this.model = model;
	}

// 	FXML-annotated methods

	@Override
	@FXML
	public void initialize() {
		this.stage = new Stage();
		stage.setTitle("Редактирование дела");
		stage.setResizable(false);
		stage.setScene(new Scene(gridPane));
		caseTypeChoiceBox.setItems(FXCollections.observableArrayList(model.getCaseTypeRepo().findAll()));
		relationChoiceBox.setItems(FXCollections.observableArrayList(model.getRelationRepo().findAll()));
		representativeChoiceBox.setItems(FXCollections.observableArrayList(model.getReprRepo().findAll()));
		stageChoiceBox.setItems(FXCollections.observableArrayList(model.getStageRepo().findAll()));
		courtComboBox.setItems(FXCollections.observableArrayList(model.getCourtRepo().findAll()));
		courtComboBox.setConverter(new StringConverter<Court>() {
			@Override
			public String toString(Court court) {
				if (court == null) {
					return "";
				}
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
				if (!minuteTextField.getText().matches("[0-5][05]")) {
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
		String courtName = court.getName();
		CourtRepo courtRepo = model.getCourtRepo();
		if (!courtRepo.existsByName(courtName)) {
			court = courtRepo.save(court);
			caseToEdit.setCourt(court);
		} else if (!courtName.equals(caseToEdit.getCourt().getName())) { // if such a court exists in DB then
			caseToEdit.setCourt(courtRepo.findByName(courtName).get()); // we simply set it as case property
		}
		if (!caseNoTextField.getText().isEmpty()) {
			caseToEdit.setCaseNo(caseNoTextField.getText());
		}
		String repr = representativeChoiceBox.getValue().getName();
		if (!repr.equals(caseToEdit.getRepr().getName())) {
			caseToEdit.setRepr(model.getReprRepo().findByName(repr));
		}
		if (currDatePicker.getValue() != null && !hourTextField.getText().isEmpty()
				&& !minuteTextField.getText().isEmpty()) {
			try {
				int hours = Integer.parseInt(hourTextField.getText());
				int mins = Integer.parseInt(minuteTextField.getText());
				caseToEdit
						.setCurrentDate(DatePickerConverter.convertToTimestamp(currDatePicker.getValue(), hours, mins));
			} catch (NumberFormatException e) {
				caseToEdit.setCurrentDate(null);
			}
		} else {
			caseToEdit.setCurrentDate(null);
		}
		if (!description.getText().equals(caseToEdit.getTitle())) {
			caseToEdit.setTitle(description.getText());
		}
		if (!caseNoTextField.getText().equals(caseToEdit.getCaseNo())) {
			caseToEdit.setCaseNo(caseNoTextField.getText());
		}
		if (!plaintiffTextField.getText().equals(caseToEdit.getPlaintiff())) {
			caseToEdit.setPlaintiff(plaintiffTextField.getText());
		}
		if (!defendantTextField.getText().equals(caseToEdit.getDefendant())) {
			caseToEdit.setDefendant(defendantTextField.getText());
		}
		if (!currentState.getText().equals(caseToEdit.getCurrentState())) {
			caseToEdit.setCurrentState(currentState.getText());
		}
		try {
			ACase updatedCase = model.getCaseRepo().save(caseToEdit);
			new CustomAlert("Подтверждение", "", "Дело внесено в базу данных!", ButtonType.OK).show();
			caseList.add(updatedCase);
		} catch (OptimisticLockException ole) {
			new CustomAlert("Обновление данных", "", "Параметры дела изменены другим пользователем!", ButtonType.OK)
					.show();
			caseList.add(model.getCaseRepo().findById(caseToEdit.getId()).get());
		} finally {
			caseList.remove(caseToEdit);
			parent.refreshTable();
			stage.close();
		}
	}

//	***************************************************************************

	public void show(ObservableList<ACase> caseList, ACase caseToEdit) {
		this.caseList = caseList;
		this.caseToEdit = caseToEdit;
		Relation relation = caseToEdit.getRelation();
		Timestamp timestamp = caseToEdit.getCurrentDate();
		LocalDate date = DatePickerConverter.extractLocalDate(timestamp);
		int[] time = DatePickerConverter.extractTime(timestamp);

		// setting inputs according to the current data stored in DB
		plaintiffTextField.setText(caseToEdit.getPlaintiff());
		defendantTextField.setText(caseToEdit.getDefendant());
		caseTypeChoiceBox.setValue(caseToEdit.getCaseType());
		relationChoiceBox.setValue(relation);
		representativeChoiceBox.setValue(caseToEdit.getRepr());
		stageChoiceBox.setValue(caseToEdit.getStage());
		courtComboBox.setValue(caseToEdit.getCourt());
		caseNoTextField.setText(caseToEdit.getCaseNo());
		description.setText(caseToEdit.getTitle());
		currentState.setText(caseToEdit.getCurrentState());
		currDatePicker.setValue(date);
		if (date != null) {
			currDatePicker.getEditor()
					.setStyle(date.isBefore(LocalDate.now()) ? "-fx-text-fill: red" : "-fx-text-fill: black");
		}
		if (time.length > 1) {
			hourTextField.setText(String.format("%02d", time[0]));
			minuteTextField.setText(String.format("%02d", time[1]));
		}

		// adding listeners to avoid extra DB operations
		relationChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> setRestrictions(newVal));
		relationChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> caseToEdit.setRelation(newVal));
		caseTypeChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> caseToEdit.setCaseType(newVal));
		representativeChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> caseToEdit.setRepr(newVal));
		stageChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> caseToEdit.setStage(newVal));
		courtComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
			Court court = newVal;
			if (model.getCourtRepo().existsByName(court.getName())) {
				court = model.getCourtRepo().findByName(court.getName()).get();
			} else {
				court = model.getCourtRepo().save(court);
			}
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
