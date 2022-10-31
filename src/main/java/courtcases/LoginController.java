package courtcases;

import java.util.List;
import org.springframework.stereotype.Component;
import courtcases.data.Representative;
import courtcases.data.RepresentativeRepo;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import net.rgielen.fxweaver.core.FxmlView;

@Component
@FxmlView("start.fxml")
public class LoginController {
	private final RepresentativeRepo reprRepo;
	
	public LoginController(RepresentativeRepo reprRepo) {
		this.reprRepo = reprRepo;
	}
	
	@FXML
	private Button userButton;

	@FXML
	private void selectUser(ActionEvent actionEvent) {
		List<Representative> reprList = reprRepo.findAll();
		ChoiceDialog<Representative> choiceDialog = new ChoiceDialog<Representative>(null, reprList);
		choiceDialog.setTitle("Выбор пользователя");
		choiceDialog.showAndWait();
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
