package mainapp.controllers;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import mainapp.customGUI.CustomAlert;
import mainapp.data.CaseType;
import mainapp.data.Court;
import mainapp.data.CourtStage;
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
	protected ChoiceBox<CourtStage> stageChoiceBox;

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
	protected Label requiredFields;

	@FXML
	protected Button saveButton;

//  *********************************************


	protected ChangeListener<? super Boolean> hourListener = (obs, oldValue, newValue) -> {
		if (!newValue) {
			if (!hourTextField.getText().matches("^(0?[89]|1[0-7])$")) {
				hourTextField.setText("");
				new CustomAlert("Ошибка ввода", "", "Неверный ввод в поле \"часы\"", ButtonType.OK).show();
			}
		}
	};
	
	protected ChangeListener<? super Boolean> minuteListener = (obs, oldValue, newValue) -> {
		if (!newValue) {
			if (!minuteTextField.getText().matches("[0-5][05]")) {
				minuteTextField.setText("");
				new CustomAlert("Ошибка ввода", "", "Неверный ввод в поле \"минуты\"", ButtonType.OK).show();
			}
		}
	};
	
// 	Abstract FXML-annotated methods

	public abstract void initialize();
	

	public abstract void saveCase(ActionEvent actionEvent);
	

//	***************************************************************************
	
	protected void setRelationRestrictions(Relation relation) {
		if (relation == Relation.PLAINTIFF) {
			plaintiffTextField.setText("Наша компания");
			plaintiffTextField.setDisable(true);
			defendantTextField.setDisable(false);
		} else if (relation == Relation.DEFENDANT) {
			plaintiffTextField.setDisable(false);
			defendantTextField.setText("Наша компания");
			defendantTextField.setDisable(true);
		} else {
			plaintiffTextField.setDisable(false);
			defendantTextField.setDisable(false);
		}
	}

	protected void setCaseTypeRestrictions(CaseType caseType) {
		if (caseType == CaseType.CRIMINAL || caseType == CaseType.ADMIN_OFFENCE) {
			relationChoiceBox.setValue(Relation.CRIMINAL_AND_ADMIN_OFFENCES);
			relationChoiceBox.setDisable(true);
		} else {
			relationChoiceBox.setDisable(false);
			relationChoiceBox.setValue(Relation.CONTROLLED);
		}
	}
	
	protected boolean isInputCorrect() {
		return relationChoiceBox.getValue() != null && caseTypeChoiceBox.getValue() != null
				&& description.getText() != null && !description.getText().isEmpty() 
				&& courtComboBox.getValue() != null	&& stageChoiceBox.getValue() != null 
				&& currentState.getText() != null && !currentState.getText().isEmpty();
	}

	protected void displayErrors() {
		new CustomAlert("Ошибка ввода", "", "Проверьте заполнение обязательных полей!", ButtonType.OK).show();
	}
}
