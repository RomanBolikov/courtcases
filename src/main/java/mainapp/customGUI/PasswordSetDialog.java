package mainapp.customGUI;

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

public class PasswordSetDialog extends Dialog<String> {

	private final GridPane grid;

	private final Label newPswLabel;
	
	private final Label retypePswLabel;
	
	private final PasswordField newPasswordField;

	private final PasswordField retypePasswordField;

	public PasswordSetDialog() {
		final DialogPane dialogPane = getDialogPane();
		this.newPasswordField = new PasswordField();
		this.newPasswordField.setMaxWidth(Double.MAX_VALUE);
		GridPane.setHgrow(newPasswordField, Priority.ALWAYS);
		GridPane.setFillWidth(newPasswordField, true);
		this.retypePasswordField = new PasswordField();
		this.retypePasswordField.setMaxWidth(Double.MAX_VALUE);
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
			String input1 = newPasswordField.getText(), input2 = retypePasswordField.getText();
			return input1.equals(input2) ? input1 : "invalid";
		});
	}

	private void updateGrid() {
		grid.getChildren().clear();
		grid.add(newPswLabel, 0, 0);
		grid.add(newPasswordField, 1, 0);
		grid.add(retypePswLabel, 0, 1);
		grid.add(retypePasswordField, 1, 1);
		getDialogPane().setContent(grid);
		Platform.runLater(() -> newPasswordField.requestFocus());
	}
}