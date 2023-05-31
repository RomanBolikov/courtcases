package mainapp.controllers;

import java.util.Optional;

import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
import mainapp.customGUI.CustomAlert;
import mainapp.customGUI.CustomChoiceDialog;
import mainapp.customGUI.PasswordInputDialog;
import mainapp.data.Representative;
import mainapp.helpers.StageReadyEvent;
import mainapp.services.ReprService;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;

@Component
@FxmlView("start.fxml")
public class LoginController implements ApplicationListener<StageReadyEvent> {

	private final FxWeaver fxWeaver;

	private final ReprService reprService;

	private Representative user;

	private CustomChoiceDialog choiceDialog;

	private Stage stage;

	@FXML
	private Button chooseUserButton;

	@FXML
	private Button adminButton;

	@FXML
	private Button quitButton;

	public LoginController(FxWeaver fxWeaver, ReprService reprService) {
		this.reprService = reprService;
		this.fxWeaver = fxWeaver;
	}

	@Override
	public void onApplicationEvent(StageReadyEvent event) {
		this.stage = event.stage;
		Parent root = fxWeaver.loadView(this.getClass());
		stage.setScene(new Scene(root));
		stage.show();
	}

	@FXML
	private void chooseUser(ActionEvent actionEvent) {
		choiceDialog = new CustomChoiceDialog(reprService);
		DialogPane dp = choiceDialog.getDialogPane();
		Button okButton = (Button) dp.lookupButton(ButtonType.OK);
		okButton.setOnAction(this::promptLogin);
		choiceDialog.setTitle("Выбор пользователя");
		choiceDialog.setHeaderText("");
		choiceDialog.show();
	}

	@FXML
	private void loginAsAdmin(ActionEvent actionEvent) {
		choiceDialog = new CustomChoiceDialog(reprService);
		DialogPane dp = choiceDialog.getDialogPane();
		Button okButton = (Button) dp.lookupButton(ButtonType.OK);
		okButton.setOnAction(this::promptLoginAsAdmin);
		choiceDialog.setTitle("Выбор администратора");
		choiceDialog.setHeaderText("");
		choiceDialog.show();
	}

	@FXML
	private void quit(ActionEvent actionEvent) {
		Platform.exit();
	}

	private void promptLogin(ActionEvent actionEvent) {
		this.user = choiceDialog.getSelectedItem();
		if (!this.user.isAdmin()) {
			loadMainController();
		} else {
			displayPrompt(user);
		}
	}

	private void promptLoginAsAdmin(ActionEvent actionEvent) {
		Representative admin = choiceDialog.getSelectedItem();
		if (!admin.isAdmin()) {
			new CustomAlert("Ошибка доступа", "Данный пользователь не наделен правами администратора!", "",
					ButtonType.OK).show();
		} else {
			Dialog<String> prompt = new PasswordInputDialog();
			prompt.setHeaderText("Ввведите пароль");
			CustomAlert alert = new CustomAlert("Ошибка ввода", "Введен неверный пароль!", "",
					new ButtonType("Повторить", ButtonData.OK_DONE),
					new ButtonType("Закрыть", ButtonData.CANCEL_CLOSE));
			while (true) {
				Optional<String> input = prompt.showAndWait();
				if (input.isPresent()) {
					if (BCrypt.checkpw(input.get(), admin.getPassword())) {
						loadAdminController();
						break;
					} else {
						Optional<ButtonType> retry = alert.showAndWait();
						if (retry.isEmpty() || retry.get().getButtonData() == ButtonData.CANCEL_CLOSE)
							break;
					}
				} else
					break;
			}
		}
	}

	private void loadMainController() {
		FxControllerAndView<MainController, Parent> main = fxWeaver.load(MainController.class);
		stage.setScene(new Scene(main.getView().get()));
		stage.setMaximized(true);
		stage.setResizable(true);
		main.getController().setStage(stage);
		main.getController().displayUser(user);
	}

	private void loadAdminController() {
		FxControllerAndView<AdminController, Parent> adminController = fxWeaver.load(AdminController.class);
		stage.setScene(new Scene(adminController.getView().get()));
		stage.setResizable(false);
	}

	private void displayPrompt(Representative user) {
		Dialog<String> prompt = new PasswordInputDialog();
		prompt.setHeaderText("Ввведите пароль");
		CustomAlert alert = new CustomAlert("Ошибка ввода", "Введен неверный пароль!", "",
				new ButtonType("Повторить", ButtonData.OK_DONE), new ButtonType("Закрыть", ButtonData.CANCEL_CLOSE));
		while (true) {
			Optional<String> input = prompt.showAndWait();
			if (input.isPresent()) {
				if (BCrypt.checkpw(input.get(), user.getPassword())) {
					loadMainController();
					break;
				} else {
					Optional<ButtonType> retry = alert.showAndWait();
					if (retry.isEmpty() || retry.get().getButtonData() == ButtonData.CANCEL_CLOSE)
						break;
				}
			} else
				break;
		}
	}

	public Stage getStage() {
		return stage;
	}
}
