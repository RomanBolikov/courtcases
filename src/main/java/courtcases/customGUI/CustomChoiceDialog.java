package courtcases.customGUI;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCrypt;

import courtcases.data.Representative;
import courtcases.data.RepresentativeRepo;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class CustomChoiceDialog extends Dialog<Representative> {

	private final GridPane grid;

	private final Label label;

	private final ComboBox<Representative> comboBox;

	private final Button changePasswordButton;

	private RepresentativeRepo reprRepo;

	public CustomChoiceDialog() {
		this(null);
	}

	public CustomChoiceDialog(RepresentativeRepo reprRepo) {

		this.reprRepo = reprRepo;

		final DialogPane dialogPane = getDialogPane();

		this.grid = new GridPane();

		this.grid.setHgap(10);

		this.grid.setMaxWidth(Double.MAX_VALUE);

		this.grid.setAlignment(Pos.CENTER_LEFT);

		label = new Label(dialogPane.getContentText());

		label.setPrefWidth(Region.USE_COMPUTED_SIZE);

		label.textProperty().bind(dialogPane.contentTextProperty());

		changePasswordButton = new Button("Изменить пароль");

		changePasswordButton.setOnAction(e -> changePassword());

		dialogPane.contentTextProperty().addListener(o -> updateGrid());

		setTitle("Подтверждение выбора");

		dialogPane.getStyleClass().add("choice-dialog");

		dialogPane.getButtonTypes().addAll(ButtonType.OK, new ButtonType("Отмена", ButtonData.CANCEL_CLOSE));

		final double MIN_WIDTH = 150;

		comboBox = new ComboBox<Representative>();

		comboBox.setMinWidth(MIN_WIDTH);

		comboBox.getItems().addAll(reprRepo.findAll());

		comboBox.setMaxWidth(Double.MAX_VALUE);

		GridPane.setHgrow(comboBox, Priority.ALWAYS);

		GridPane.setFillWidth(comboBox, true);

		comboBox.getSelectionModel().selectFirst();
		
		comboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> 
			changePasswordButton.setDisable(newVal.isAdmin() ? false : true));

		updateGrid();

		setResultConverter((dialogButton) -> {
			ButtonData data = dialogButton == null ? null : dialogButton.getButtonData();
			return data == ButtonData.OK_DONE ? getSelectedItem() : null;
		});
	}

	public final Representative getSelectedItem() {
		return comboBox.getSelectionModel().getSelectedItem();
	}

	public final ReadOnlyObjectProperty<Representative> selectedItemProperty() {
		return comboBox.getSelectionModel().selectedItemProperty();
	}

	public final void setSelectedItem(Representative item) {
		comboBox.getSelectionModel().select(item);
	}

	public final ObservableList<Representative> getItems() {
		return comboBox.getItems();
	}

	private void updateGrid() {
		grid.getChildren().clear();
		grid.add(label, 0, 0);
		grid.add(comboBox, 1, 0);
		grid.add(changePasswordButton, 2, 0);
		getDialogPane().setContent(grid);

		Platform.runLater(() -> comboBox.requestFocus());
	}

	private void changePassword() {
		Representative user = getSelectedItem();
		Dialog<String> prompt = new PasswordChangeDialog(user.getPassword());
		prompt.setHeaderText("Введите пароль");
		Optional<String> newPassword = prompt.showAndWait();
		if (newPassword.isPresent() && newPassword != null) {
			String pswHash = BCrypt.hashpw(newPassword.get(), BCrypt.gensalt());
			user.setPassword(pswHash);
			reprRepo.save(user);
			new CustomAlert("Подтверждение", "", "Пароль успешно изменен!", ButtonType.OK).show();
		} else {
			CustomAlert alert = new CustomAlert("Ошибка", "", "Неверный ввод!",
					new ButtonType("Повторить", ButtonData.OK_DONE),
					new ButtonType("Закрыть", ButtonData.CANCEL_CLOSE));
			ButtonData res = alert.showAndWait().get().getButtonData();
			if (res == ButtonData.OK_DONE)
				changePassword();
		}
	}
}