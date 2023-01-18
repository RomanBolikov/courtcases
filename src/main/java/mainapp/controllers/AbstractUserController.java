package mainapp.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public abstract class AbstractUserController {
	
	protected Stage stage;
	
	@FXML
	protected VBox vbox;
	
	@FXML
	protected TextField textField;
	
	@FXML
	protected CheckBox checkBox;
	
	@FXML
	protected Button OKButton;
	
	@FXML
	protected Button cancelButton;
	
	
//  *********************************************

// 	Abstract FXML-annotated methods

	@FXML
	public abstract void initialize();
	

	@FXML
	public abstract void saveUser(ActionEvent actionEvent);
	

//	***************************************************************************
	
	protected boolean validate (String input) {
		return input.matches("[А-Я]([a-яё]){1,25}\\s([А-Я]\\.){2}");
	}
	
}
