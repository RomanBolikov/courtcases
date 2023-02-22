package mainapp.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import mainapp.customGUI.CustomAlert;
import mainapp.data.CaseType;
import mainapp.data.Court;
import mainapp.data.Relation;
import mainapp.data.Representative;


public abstract class AbstractCaseController {

// 	FXML GUI elements

	@FXML
	protected GridPane gridPane;

	@FXML
	protected ChoiceBox<CaseType> caseTypeChoiceBox;

	@FXML
	protected ChoiceBox<Relation> relationChoiceBox;

	@FXML
	protected ChoiceBox<Representative> representativeChoiceBox;

	@FXML
	protected ComboBox<Court> courtComboBox;

	@FXML
	protected TextField caseNoTextField;

	@FXML
	protected ChoiceBox<mainapp.data.Stage> stageChoiceBox;

	@FXML
	protected DatePicker currDatePicker;

	@FXML
	protected TextField hourTextField;

	@FXML
	protected TextField minuteTextField;

	@FXML
	protected TextArea description;

	@FXML
	protected TextField plaintiffTextField;

	@FXML
	protected TextField defendantTextField;

	@FXML
	protected TextArea currentState;

	@FXML
	protected Button saveButton;

//  *********************************************

// 	Abstract FXML-annotated methods

	public abstract void initialize();
	

	public abstract void saveCase(ActionEvent actionEvent);
	

//	***************************************************************************
	
	protected void setRestrictions(Relation relation) {
		if (relation.getId() == 1) {
			plaintiffTextField.setText("Минстрой края");
			plaintiffTextField.setDisable(true);
			defendantTextField.setDisable(false);
		} else if (relation.getId() == 2) {
			plaintiffTextField.setDisable(false);
			defendantTextField.setText("Минстрой края");
			defendantTextField.setDisable(true);
		} else {
			plaintiffTextField.setDisable(false);
			defendantTextField.setDisable(false);
		}
	}

	protected boolean isInputCorrect() {
		return relationChoiceBox.getValue() != null && caseTypeChoiceBox.getValue() != null
				&& !description.getText().isEmpty() && courtComboBox.getValue() != null
				&& stageChoiceBox.getValue() != null && !currentState.getText().isEmpty();
	}

	protected void displayErrors() {
		new CustomAlert("Ошибка ввода", "", "Проверьте заполнение полей!", ButtonType.OK).show();
	}
}
