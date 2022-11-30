package courtcases.controllers;

import java.util.Optional;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;
import courtcases.StageReadyEvent;
import courtcases.customGUI.CustomAlert;
import courtcases.customGUI.CustomChoiceDialog;
import courtcases.customGUI.PasswordInputDialog;
import courtcases.data.Representative;
import courtcases.data.RepresentativeRepo;
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
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;

@Component
@FxmlView("start.fxml")
public class LoginController implements ApplicationListener<StageReadyEvent> {

	private final RepresentativeRepo reprRepo;

	private final FxWeaver fxWeaver;

	private Representative user;

	private CustomChoiceDialog choiceDialog;

	private Stage stage;

	@FXML
	private Button chooseUserButton;

	@FXML
	private Button adminButton;

	@FXML
	private Button quitButton;

	public LoginController(RepresentativeRepo reprRepo, FxWeaver fxWeaver) {
		this.reprRepo = reprRepo;
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
		choiceDialog = new CustomChoiceDialog(reprRepo);
		DialogPane dp = choiceDialog.getDialogPane();
		Button okButton = (Button) dp.lookupButton(ButtonType.OK);
		okButton.setOnAction(this::promptLogin);
		choiceDialog.setTitle("Выбор пользователя");
		choiceDialog.setHeaderText("");
		choiceDialog.show();
	}

	@FXML
	private void loginAsAdmin(ActionEvent actionEvent) {
		// TODO
	}

	@FXML
	private void quit(ActionEvent actionEvent) {
		Platform.exit();
	}

	private void promptLogin(ActionEvent actionEvent) {
		this.user = choiceDialog.getSelectedItem();
		if (!this.user.isAdmin())
			loadMainController();
		else
			displayPrompt(user);
	}

	private void loadMainController() {
		FxControllerAndView<MainController, Parent> main = fxWeaver.load(MainController.class);
		stage.setScene(new Scene(main.getView().get()));
		stage.setMaximized(true);
		stage.setResizable(false);
		main.getController().displayUser(user);
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
			} else break;
		}
	}

	public Stage getStage() {
		return stage;
	}
}
