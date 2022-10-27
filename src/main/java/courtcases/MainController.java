package courtcases;

import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import net.rgielen.fxweaver.core.FxmlView;

@Component
@FxmlView("main_demo.fxml")
public class MainController {
	@FXML
	private Button quitButton;
	
	public void quit(ActionEvent actionEvent) {
		Platform.exit();
	}
}
