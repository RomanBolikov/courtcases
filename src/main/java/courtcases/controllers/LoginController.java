package courtcases.controllers;

import java.util.List;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import courtcases.StageReadyEvent;
import courtcases.data.Representative;
import courtcases.data.RepresentativeRepo;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;

@Component
@FxmlView("start.fxml")
public class LoginController implements ApplicationListener<StageReadyEvent> {
	private final RepresentativeRepo reprRepo;

	private final FxWeaver fxWeaver;

	private Representative user = null;
	
	private Stage stage;

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
	private Button chooseUserButton;

	@FXML
	private void chooseUser(ActionEvent actionEvent) {
		List<Representative> reprList = reprRepo.findAll();
		ChoiceDialog<Representative> choiceDialog = new ChoiceDialog<Representative>(null, reprList);
		DialogPane dp = choiceDialog.getDialogPane();
		Button cancelButton = (Button) dp.lookupButton(ButtonType.CANCEL);
		cancelButton.setText("Отмена");
		Button okButton = (Button) dp.lookupButton(ButtonType.OK);
		okButton.setOnAction(e -> {
			this.user = choiceDialog.getSelectedItem();
			MainController mainController = fxWeaver.loadController(MainController.class);
			mainController.show(stage, user); 
		});
		choiceDialog.setTitle("Выбор пользователя");
		choiceDialog.setHeaderText("");
		choiceDialog.show();
	}

	@FXML
	private Button adminButton;

	@FXML
	private void loginAsAdmin(ActionEvent actionEvent) {
		// TODO
	}

	@FXML
	private Button quitButton;

	@FXML
	private void quit(ActionEvent actionEvent) {
		Platform.exit();
	}
}
