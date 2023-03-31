package mainapp.controllers;

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
@FxmlView("addcase.fxml")
public class AddCaseController extends AbstractCaseController {

	private Stage stage;

	private ObservableList<ACase> caseList;
	
	private final CaseService caseService;
	
	private final ReprService reprService;
	
	private final CourtService courtService;

	public AddCaseController(CaseService caseService, ReprService reprService, CourtService courtService) {
		this.caseService = caseService;
		this.reprService = reprService;
		this.courtService = courtService;
	}

	@Override
	@FXML
	public void initialize() {
		this.stage = new Stage();
		stage.setTitle("Добавление нового дела");
		stage.setResizable(false);
		stage.setScene(new Scene(gridPane));
		caseTypeChoiceBox.setItems(FXCollections.observableArrayList(CaseType.values()));
		caseTypeChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> setCaseTypeRestrictions(newVal));
		relationChoiceBox.setItems(FXCollections.observableArrayList(Relation.values()));
		relationChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> setRelationRestrictions(newVal));
		representativeChoiceBox.setItems(reprService.getAllReprs().
				sorted((r1, r2) -> r1.getName().compareTo(r2.getName())));
		stageChoiceBox.setItems(FXCollections.observableArrayList(CourtStage.values()));
		courtComboBox.setItems(courtService.getAllCourts()
				.sorted((c1, c2) -> c1.getName().compareTo(c2.getName())));
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
				if (!minuteTextField.getText().matches("[0-6][05]")) {
					minuteTextField.setText("");
					new CustomAlert("Ошибка ввода", "", "Неверный ввод в поле \"минуты\"", ButtonType.OK).show();
				}
			}
		});
		// limit input into plaintiff anf defendant TextFields to 200 characters
		plaintiffTextField.setTextFormatter(
				new TextFormatter<String>(change -> change.getControlNewText().length() <= 200 ? change : null));

		defendantTextField.setTextFormatter(
				new TextFormatter<String>(change -> change.getControlNewText().length() <= 200 ? change : null));

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
		Court court = courtComboBox.getValue();
		if (courtService.existsInDB(court)) {
			court = courtService.findCourtByEntity(court);
		} else {
			try {
				court = courtService.addCourt(court);
			} catch (SaveEntityException see) {
			new CustomAlert("Ошибка", "", "Возникла ошибка при сохранении", ButtonType.OK).show();
			return;
			}
		}
		ACase newCase = new ACase(relationChoiceBox.getValue(), caseTypeChoiceBox.getValue(), description.getText(),
				court, stageChoiceBox.getValue(), currentState.getText());
		if (!caseNoTextField.getText().isEmpty()) {
			newCase.setCaseNo(caseNoTextField.getText());
		}
		if (!plaintiffTextField.getText().isEmpty()) {
			newCase.setPlaintiff(plaintiffTextField.getText());
		}
		if (!defendantTextField.getText().isEmpty()) {
			newCase.setDefendant(defendantTextField.getText());
		}
		if (representativeChoiceBox.getValue() != null) {
			newCase.setRepr(representativeChoiceBox.getValue());
		}
		if (currDatePicker.getValue() != null && !hourTextField.getText().isEmpty()
				&& !minuteTextField.getText().isEmpty()) {
			try {
				int hours = Integer.parseInt(hourTextField.getText(), 10);
				int mins = Integer.parseInt(minuteTextField.getText(), 10);
				newCase.setCurrentDate(
						DatePickerConverter.convertToTimestamp(currDatePicker.getValue(), hours, mins));
			} catch (NumberFormatException nfe) {
				newCase.setCurrentDate(null);
			}
		}
		try {
			newCase = caseService.saveCase(newCase);
		} catch (SaveEntityException see) {
			new CustomAlert("Ошибка", "", "Возникла ошибка при сохранении", ButtonType.OK).show();
			return;
		}
		caseList.add(newCase);
		new CustomAlert("Подтверждение", "", "Дело внесено в базу данных!", ButtonType.OK).show();
		stage.close();
	}

//	***************************************************************************

	public void show(ObservableList<ACase> caseList, Representative user) {
		this.caseList = caseList;
		representativeChoiceBox.setValue(user);
		if (!user.isAdmin()) {
			representativeChoiceBox.setDisable(true);
		}
		stage.show();
	}
}
