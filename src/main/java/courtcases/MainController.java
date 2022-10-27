package courtcases;

import org.springframework.stereotype.Component;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import net.rgielen.fxweaver.core.FxmlView;

@Component
@FxmlView("start.fxml")
public class MainController {
	
	@FXML
	private Button authorizeButton;

	public void authorize(ActionEvent actionEvent) {
		
	}
	
	@FXML
	private Button quitButton;
	
	@FXML
	private void quit(ActionEvent actionEvent) {
		Platform.exit();
	}
}
