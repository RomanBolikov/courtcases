package courtcases.customGUI;

import org.springframework.security.crypto.bcrypt.BCrypt;

import com.sun.javafx.scene.control.skin.resources.ControlResources;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class PasswordChangeDialog extends Dialog<String> {

	private final GridPane grid;

	private final Label oldPswLabel;
	
	private final Label newPswLabel;
	
	private final Label retypePswLabel;
	
	private final PasswordField oldPasswordField;
	
	private final PasswordField newPasswordField;

	private final PasswordField retypePasswordField;

	public PasswordChangeDialog(String oldPassword) {
		final DialogPane dialogPane = getDialogPane();
		this.oldPasswordField = new PasswordField();
		this.oldPasswordField.setMaxWidth(Double.MAX_VALUE);
		GridPane.setHgrow(oldPasswordField, Priority.ALWAYS);
		GridPane.setFillWidth(oldPasswordField, true);
		this.newPasswordField = new PasswordField();
		this.newPasswordField.setMaxWidth(Double.MAX_VALUE);
		this.retypePasswordField = new PasswordField();
		this.retypePasswordField.setMaxWidth(Double.MAX_VALUE);
		oldPswLabel = new Label("Текущий пароль");
		oldPswLabel.setPrefWidth(Region.USE_COMPUTED_SIZE);
		newPswLabel = new Label("Новый пароль");
		newPswLabel.setPrefWidth(Region.USE_COMPUTED_SIZE);
		retypePswLabel = new Label("Новый пароль (подтверждение)");
		retypePswLabel.setPrefWidth(Region.USE_COMPUTED_SIZE);
		this.grid = new GridPane();
		this.grid.setHgap(10);
		this.grid.setMaxWidth(Double.MAX_VALUE);
		this.grid.setAlignment(Pos.CENTER_LEFT);
		dialogPane.contentTextProperty().addListener(o -> updateGrid());
		setTitle(ControlResources.getString("Dialog.confirm.title"));
		dialogPane.setHeaderText(ControlResources.getString("Dialog.confirm.header"));
		dialogPane.getStyleClass().add("text-input-dialog");
		dialogPane.getButtonTypes().addAll(new ButtonType("OK", ButtonData.OK_DONE),
				new ButtonType("Отмена", ButtonData.CANCEL_CLOSE));
		updateGrid();
		setResultConverter((dialogButton) -> {
			ButtonData data = dialogButton == null ? null : dialogButton.getButtonData();
			if (data == null || data == ButtonData.CANCEL_CLOSE)
				return null;
			String input1 = oldPasswordField.getText();
			if (!BCrypt.checkpw(input1, oldPassword))
				return "invalid";
			String input2 = newPasswordField.getText(), input3 = retypePasswordField.getText();
			return input2.equals(input3) ? input2 : "invalid";
		});
	}

	private void updateGrid() {
		grid.getChildren().clear();
		grid.add(oldPswLabel, 0, 0);
		grid.add(oldPasswordField, 1, 0);
		grid.add(newPswLabel, 0, 1);
		grid.add(newPasswordField, 1, 1);
		grid.add(retypePswLabel, 0, 2);
		grid.add(retypePasswordField, 1, 2);
		getDialogPane().setContent(grid);
		Platform.runLater(() -> oldPasswordField.requestFocus());
	}
}