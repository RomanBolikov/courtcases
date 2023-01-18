package mainapp.controllers;

import java.util.Optional;

import org.springframework.stereotype.Component;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import mainapp.customGUI.CustomAlert;
import mainapp.data.Representative;
import net.rgielen.fxweaver.core.FxmlView;

@Component
@FxmlView("adduser.fxml")
public class AddUserController extends AbstractUserController {

	private Representative newUser;
	
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
		if (!validate(input))
			new CustomAlert("Ошибка ввода", "", "Проверьте ввод Ф.И.О. сотрудника!", ButtonType.OK).show();
		else {
			newUser = new Representative(input);
			newUser.setIsAdmin(checkBox.isSelected() ? true : false);
			stage.close();
		}
	}

	public Optional<Representative> showAndWait() {
		stage.showAndWait();
		return Optional.ofNullable(newUser);
	}
}
