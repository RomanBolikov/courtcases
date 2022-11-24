package courtcases.customGUI;

import javafx.beans.NamedArg;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;

public class ConfirmationAlert extends Dialog<ButtonType> {

	public ConfirmationAlert() {
		this("", "", "");
	}

	public ConfirmationAlert(String title, @NamedArg("contentText") String contentText, String headerText,
			ButtonType... buttons) {
		super();

		final DialogPane dialogPane = getDialogPane();
		dialogPane.setContentText(contentText);
		getDialogPane().getStyleClass().add("alert");

		for (ButtonType btnType : buttons) {
			getDialogPane().getButtonTypes().setAll(btnType);
		}

		dialogPane.getStyleClass().add("confirmation");

		setTitle(title);
		setHeaderText(headerText);
	}
}
