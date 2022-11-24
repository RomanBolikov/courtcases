package courtcases.controllers;

import courtcases.data.CaseType;
import courtcases.data.Court;
import courtcases.data.Relation;
import courtcases.data.Representative;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;


public abstract class FormController {

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
	protected ChoiceBox<courtcases.data.Stage> stageChoiceBox;

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

	@FXML
	public abstract void initialize();
	

	@FXML
	public abstract void saveCase(ActionEvent actionEvent);
	

//	***************************************************************************


	protected boolean isInputCorrect() {
		return relationChoiceBox.getValue() != null && caseTypeChoiceBox.getValue() != null
				&& description.getText() != null && courtComboBox.getValue() != null
				&& plaintiffTextField.getText() != "" && defendantTextField.getText() != ""
				&& stageChoiceBox.getValue() != null && currentState.getText() != "";
	}

	protected void displayErrors() {
		new Alert(AlertType.ERROR, "Проверьте заполнение полей!", ButtonType.OK).show();
	}
}
