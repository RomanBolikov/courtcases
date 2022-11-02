package courtcases.controllers;

import org.springframework.stereotype.Component;
import courtcases.data.DatabaseModel;
import courtcases.data.Representative;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;

@Component
@FxmlView("main.fxml")
public class MainController {

	@SuppressWarnings("unused")
	private DatabaseModel dbModel;
	private FxWeaver fxWeaver;
	private Representative user = null;

	public MainController(DatabaseModel dbModel, FxWeaver fxWeaver) {
		this.dbModel = dbModel;
		this.fxWeaver = fxWeaver;
	}
	
	public void show(Stage stage, Representative user) {
		this.user = user;
		Parent root = fxWeaver.loadView(this.getClass());
		stage.setScene(new Scene(root));
		stage.setMaximized(true);
	}
	
	@FXML Label userLabel;
	
	@FXML
	public void initialize() {
		userLabel.setText("Пользователь: " + this.user);
	}
}
