package courtcases.controllers;

import java.util.List;
import org.springframework.stereotype.Component;
import courtcases.data.Representative;
import courtcases.data.RepresentativeRepo;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.DialogPane;
import net.rgielen.fxweaver.core.FxmlView;

@Component
@FxmlView("start.fxml")
public class LoginController {
	private final RepresentativeRepo reprRepo;
	
	public LoginController(RepresentativeRepo reprRepo) {
		this.reprRepo = reprRepo;
	}
	
	@FXML private Button userButton;

	@FXML
	private void selectUser(ActionEvent actionEvent) {
		List<Representative> reprList = reprRepo.findAll();
		ChoiceDialog<Representative> choiceDialog = new ChoiceDialog<Representative>(null, reprList);
		DialogPane dp = choiceDialog.getDialogPane();
		List<ButtonType> l = dp.getButtonTypes();
		l.removeIf(t -> t == ButtonType.CANCEL);
		l.add(new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE));
		choiceDialog.setDialogPane(dp);
		choiceDialog.setTitle("Выбор пользователя");
		choiceDialog.setHeaderText("");
		choiceDialog.showAndWait();
		//Representative user = choiceDialog.getResult();
	}

	@FXML private Button adminButton;

	@FXML
	private void loginAsAdmin(ActionEvent actionEvent) {
		// TODO
	}

	@FXML private Button quitButton;

	@FXML
	private void quit(ActionEvent actionEvent) {
		Platform.exit();
	}
}
