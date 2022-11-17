package courtcases.customGUI;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class AddCourtDialog extends Dialog<String> {
	private final GridPane grid;

	private final Label label;

	private final TextField textField;

	public AddCourtDialog(String defaultValue) {
		final DialogPane dialogPane = getDialogPane();
		this.textField = new TextField(defaultValue);
		this.textField.setMaxWidth(Double.MAX_VALUE);
		GridPane.setHgrow(textField, Priority.ALWAYS);
		GridPane.setFillWidth(textField, true);
		label = new Label(dialogPane.getContentText());
		label.setPrefWidth(Region.USE_COMPUTED_SIZE);
		label.textProperty().bind(dialogPane.contentTextProperty());
		this.grid = new GridPane();
		this.grid.setHgap(10);
		this.grid.setMaxWidth(Double.MAX_VALUE);
		this.grid.setAlignment(Pos.CENTER_LEFT);
		dialogPane.contentTextProperty().addListener(o -> updateGrid());
		setTitle("Добавление нового суда");
		dialogPane.setHeaderText("Проверьте правильность введенного наименования!");
		dialogPane.getStyleClass().add("text-input-dialog");
		dialogPane.getButtonTypes().addAll(new ButtonType("OK", ButtonData.OK_DONE),
				new ButtonType("Отмена", ButtonData.CANCEL_CLOSE));
		updateGrid();
		setResultConverter((dialogButton) -> {
			ButtonData data = dialogButton == null ? null : dialogButton.getButtonData();
			return data == ButtonData.OK_DONE ? textField.getText() : null;
		});
	}

	private void updateGrid() {
		grid.getChildren().clear();
		grid.add(label, 0, 0);
		grid.add(textField, 1, 0);
		getDialogPane().setContent(grid);
		Platform.runLater(() -> textField.requestFocus());
	}
}


	