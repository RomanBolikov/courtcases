package courtcases.controllers;

import java.util.List;
import java.util.Optional;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;
import courtcases.StageReadyEvent;
import courtcases.customGUI.PasswordInputDialog;
import courtcases.data.Representative;
import courtcases.data.RepresentativeRepo;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Alert.AlertType;
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

	private ChoiceDialog<Representative> choiceDialog;

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
		List<Representative> reprList = reprRepo.findAll();
		choiceDialog = new ChoiceDialog<Representative>(null, reprList);
		DialogPane dp = choiceDialog.getDialogPane();
		Button cancelButton = (Button) dp.lookupButton(ButtonType.CANCEL);
		cancelButton.setText("Отмена");
		Button okButton = (Button) dp.lookupButton(ButtonType.OK);
		okButton.setOnAction(this::promptLogin);
		choiceDialog.setTitle("Выбор пользователя");
		choiceDialog.setHeaderText("");
		choiceDialog.show();
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
		Optional<String> input = prompt.showAndWait();
		Alert alert;
		if (input.isPresent()) {
			if (BCrypt.checkpw(input.get(), user.getPassword()))
				loadMainController();
			else {
				alert = new Alert(AlertType.ERROR, "Пароль неверный!", new ButtonType("Повторить", ButtonData.OK_DONE),
						new ButtonType("Закрыть", ButtonData.CANCEL_CLOSE));
				alert.setHeaderText("");
				alert.setGraphic(null);
				ButtonData res = alert.showAndWait().get().getButtonData();
				if (res == ButtonData.OK_DONE)
					displayPrompt(user);
			}
		}
	}

	public Stage getStage() {
		return stage;
	}

	@FXML
	private void loginAsAdmin(ActionEvent actionEvent) {
		// TODO
	}

	@FXML
	private void quit(ActionEvent actionEvent) {
		Platform.exit();
	}
}
