package mainapp.controllers;

import java.sql.Timestamp;
import java.time.LocalDate;

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
import mainapp.data.CaseType;
import mainapp.data.Court;
import mainapp.data.CourtStage;
import mainapp.data.Relation;
import mainapp.data.Representative;
import mainapp.helpers.DatePickerConverter;
import mainapp.helpers.SaveEntityException;
import mainapp.services.CaseService;
import mainapp.services.CourtService;
import mainapp.services.ReprService;
import net.rgielen.fxweaver.core.FxmlView;

@Component
@FxmlView("editcase.fxml")
public class EditCaseController extends AbstractCaseController {

	private Stage stage;

	private ObservableList<ACase> caseList;

	private ACase caseToEdit;

	private final CaseService caseService;

	private final CourtService courtService;

	private final ReprService reprService;

	public EditCaseController(CaseService caseService, CourtService courtService, ReprService reprService) {
		this.caseService = caseService;
		this.courtService = courtService;
		this.reprService = reprService;
	}

// 	FXML-annotated methods

	@Override
	@FXML
	public void initialize() {
		this.stage = new Stage();
		stage.setTitle("Редактирование дела");
		stage.setResizable(false);
		stage.setScene(new Scene(gridPane));
		caseTypeChoiceBox.setItems(FXCollections.observableArrayList(CaseType.values()));
		relationChoiceBox.setItems(FXCollections.observableArrayList(Relation.values()));
		representativeChoiceBox
				.setItems(reprService.getAllReprs().sorted((r1, r2) -> r1.getName().compareTo(r2.getName())));
		stageChoiceBox.setItems(FXCollections.observableArrayList(CourtStage.values()));
		courtComboBox.setItems(courtService.getAllCourts().sorted((c1, c2) -> c1.getName().compareTo(c2.getName())));
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
		if (!courtService.existsInDB(court)) {
			try {
				court = courtService.addCourt(court);
			} catch (SaveEntityException see) {
				new CustomAlert("Ошибка", "", "Возникла ошибка при сохранении", ButtonType.OK).show();
				return;
			}
			caseToEdit.setCourt(court);
		} else if (!court.getName().equals(caseToEdit.getCourt().getName())) { // if such a court exists in DB then
			caseToEdit.setCourt(courtService.findCourtByEntity(court)); // we simply set it as case property
		}
		if ((caseToEdit.getCaseNo() == null && caseNoTextField.getText() != null
				&& !caseNoTextField.getText().isEmpty())
				|| (caseToEdit.getCaseNo() != null && caseNoTextField.getText() != null
						&& !caseNoTextField.getText().equals(caseToEdit.getCaseNo()))) {
			caseToEdit.setCaseNo(caseNoTextField.getText());
		}
		Representative repr = representativeChoiceBox.getValue();
		if (repr != null && !repr.getName().equals(caseToEdit.getRepr().getName())) {
			caseToEdit.setRepr(reprService.getRepr(repr));
		}
		if (currDatePicker.getValue() != null && hourTextField.getText() != null && !hourTextField.getText().isEmpty()
				&& minuteTextField.getText() != null && !minuteTextField.getText().isEmpty()) {
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
		if (description.getText() != null && !description.getText().equals(caseToEdit.getTitle())) {
			caseToEdit.setTitle(description.getText());
		}
		if (plaintiffTextField.getText() != null && !plaintiffTextField.getText().equals(caseToEdit.getPlaintiff())) {
			caseToEdit.setPlaintiff(plaintiffTextField.getText());
		}
		if (defendantTextField.getText() != null && !defendantTextField.getText().equals(caseToEdit.getDefendant())) {
			caseToEdit.setDefendant(defendantTextField.getText());
		}
		if (currentState.getText() != null && !currentState.getText().equals(caseToEdit.getCurrentState())) {
			caseToEdit.setCurrentState(currentState.getText());
		}
		try {
			ACase updatedCase = caseService.saveCase(caseToEdit);
			new CustomAlert("Подтверждение", "", "Дело внесено в базу данных!", ButtonType.OK).show();
			caseList.add(updatedCase);
		} catch (SaveEntityException see) {
			new CustomAlert("Обновление данных", "", "Параметры дела изменены другим пользователем!", ButtonType.OK)
					.show();
			caseList.add(caseService.getCaseById(caseToEdit.getId()));
		} finally {
			caseList.remove(caseToEdit);
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
		if (caseTypeChoiceBox.getValue() == CaseType.CRIMINAL 
				|| caseTypeChoiceBox.getValue() == CaseType.ADMIN_OFFENCE) {
			relationChoiceBox.setDisable(true);
		}
		representativeChoiceBox.setValue(caseToEdit.getRepr());
		stageChoiceBox.setValue(caseToEdit.getStage());
		courtComboBox.setValue(caseToEdit.getCourt());
		caseNoTextField.setText(caseToEdit.getCaseNo());
		description.setText(caseToEdit.getTitle());
		currentState.setText(caseToEdit.getCurrentState());
		currDatePicker.setValue(date);
		if (date != null && date.isBefore(LocalDate.now())) {
			currDatePicker.getEditor().setStyle("-fx-text-fill: red");
		}
		if (time.length > 1) {
			hourTextField.setText(String.format("%02d", time[0]));
			minuteTextField.setText(String.format("%02d", time[1]));
		}

		// adding listeners to change case properties automatically
		caseTypeChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> setCaseTypeRestrictions(newVal));
		relationChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> setRelationRestrictions(newVal));
		relationChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> caseToEdit.setRelation(newVal));
		caseTypeChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> caseToEdit.setCaseType(newVal));
		representativeChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> caseToEdit.setRepr(newVal));
		stageChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> caseToEdit.setStage(newVal));
		currDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal != null && newVal.isAfter(LocalDate.now())) {
				currDatePicker.getEditor().setStyle("-fx-text-fill: black");
			}
			hourTextField.setText("");
			minuteTextField.setText("");
		});
		stage.setOnHiding(e -> caseToEdit.setEditable(true));
		stage.show();
	}
}
