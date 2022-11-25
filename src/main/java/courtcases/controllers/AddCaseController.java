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
import courtcases.data.RelationRepo;
import courtcases.data.Representative;
import courtcases.data.RepresentativeRepo;
import courtcases.data.StageRepo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import net.rgielen.fxweaver.core.FxmlView;

@Component
@FxmlView("addcase.fxml")
public class AddCaseController extends FormController {
	private Stage stage;

	private ObservableList<ACase> caseList;

	private final CaseRepo caseRepo;

	private final RepresentativeRepo reprRepo;

	private final CaseTypeRepo caseTypeRepo;

	private final RelationRepo relationRepo;

	private final StageRepo stageRepo;

	private final CourtRepo courtRepo;

	public AddCaseController(CaseRepo caseRepo, RepresentativeRepo reprRepo, CaseTypeRepo caseTypeRepo,
			RelationRepo relationRepo, StageRepo stageRepo, CourtRepo courtRepo) {
		this.caseRepo = caseRepo;
		this.reprRepo = reprRepo;
		this.caseTypeRepo = caseTypeRepo;
		this.relationRepo = relationRepo;
		this.stageRepo = stageRepo;
		this.courtRepo = courtRepo;
	}

	@Override
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
		} else {
			Court court = courtComboBox.getValue();
			Optional<Court> courtInDB = courtRepo.findByName(court.getName());
			if (courtInDB.isPresent())
				court = courtInDB.get();
			else
				court = courtRepo.save(court);
			ACase newCase = new ACase(relationChoiceBox.getValue(), caseTypeChoiceBox.getValue(), description.getText(),
					court, plaintiffTextField.getText(), defendantTextField.getText(), stageChoiceBox.getValue(),
					currentState.getText());
			if (caseNoTextField.getText() != "")
				newCase.setCase_no(caseNoTextField.getText());
			if (representativeChoiceBox.getValue() != null)
				newCase.setRepr(representativeChoiceBox.getValue());
			if (currDatePicker.getValue() != null) {
				try {
					int hours = Integer.parseInt(hourTextField.getText(), 10); 
					int mins = Integer.parseInt(minuteTextField.getText(), 10);
					LocalDate date = currDatePicker.getValue();
					LocalTime time = LocalTime.of(hours, mins);
					newCase.setCurr_date(Timestamp.valueOf(LocalDateTime.of(date, time)));
				} catch (NumberFormatException nfe) {
					newCase.setCurr_date(null);
				}	
			}
			newCase = caseRepo.save(newCase);
			caseList.addAll(newCase);
			new CustomAlert("Подтверждение", "", "Дело внесено в базу данных!", ButtonType.OK).show();
			stage.close();
		}
	}

//	***************************************************************************

	public void show(ObservableList<ACase> caseList, Representative user) {
		this.caseList = caseList;
		representativeChoiceBox.setValue(user);
		if (!user.isAdmin())
			representativeChoiceBox.setDisable(true);
		stage.show();
	}
}
