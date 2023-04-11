package mainapp.controllers;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mainapp.customGUI.CustomAlert;
import mainapp.customGUI.PasswordSetDialog;
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
		if (!validate(input)) {
			new CustomAlert("Ошибка ввода", "", "Проверьте ввод Ф.И.О. сотрудника!", ButtonType.OK).show();
		} else {
			newUser = new Representative(input);
			newUser.setIsAdmin(checkBox.isSelected());
			if (newUser.isAdmin()) {
				Dialog<String> prompt = new PasswordSetDialog();
				prompt.setHeaderText("Установите пароль");
				CustomAlert alert = new CustomAlert("Ошибка", "",
						"Проверьте, что пароль не пустой, а также что пароль и подтверждение совпадают",
						new ButtonType("Повторить", ButtonData.OK_DONE),
						new ButtonType("Закрыть", ButtonData.CANCEL_CLOSE));
				while (true) {
					Optional<String> newPassword = prompt.showAndWait();
					if (newPassword.isPresent()) {
						if (!newPassword.get().equals("invalid")) {
							newUser.setPassword(BCrypt.hashpw(newPassword.get(), BCrypt.gensalt(4)));
							new CustomAlert("Подтверждение", "", "Пароль успешно установлен!", ButtonType.OK).show();
							break;
						} else {
							Optional<ButtonType> retry = alert.showAndWait();
							if (retry.isEmpty() || retry.get().getButtonData() == ButtonData.CANCEL_CLOSE) {
								newUser = null;
								break;
							}
						}
					} else {
						newUser = null;
						break;
					}
				}
			}
			stage.close();
		}
	}

	public Optional<Representative> showAndWait() {
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();
		return Optional.ofNullable(newUser);
	}
}
