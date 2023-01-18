package mainapp.controllers;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import mainapp.customGUI.CustomAlert;
import mainapp.data.Representative;
import net.rgielen.fxweaver.core.FxmlView;

@Component
@FxmlView("edituser.fxml")
public class EditUserController extends AbstractUserController {

	private String name;
	
	private boolean status;
	
	private boolean unchanged = true; 
	
	@FXML
	private Label label;
	
	@Override
	@FXML
	public void initialize() {
		this.stage = new Stage();
		stage.setScene(new Scene(vbox));
		cancelButton.setOnAction(e -> stage.close());
		textField.setTextFormatter(
				new TextFormatter<String>(change -> change.getControlNewText().length() <= 50 ? change : null));
	}

	@Override
	@FXML
	public void saveUser(ActionEvent actionEvent) {
		String input = textField.getText();
		boolean selectedStatus = checkBox.isSelected();
		if (!validate(input))
			new CustomAlert("Ошибка ввода", "", "Проверьте ввод Ф.И.О. сотрудника!", ButtonType.OK).show();
		else {
			if (!input.equals(name) || selectedStatus != status) {
				unchanged = false;
				name = input;
				status = selectedStatus;
			}
			stage.close();
		}
	}

	public Pair<String, Boolean> showAndWait(Representative userToEdit) {
		label.setText("Данные пользователя " + userToEdit.getName() + ":");
		textField.setText(userToEdit.getName());
		checkBox.setSelected(userToEdit.isAdmin() ? true : false);
		stage.showAndWait();
		if (unchanged)
			return null;
		return Pair.of(name, status);
	}
}
