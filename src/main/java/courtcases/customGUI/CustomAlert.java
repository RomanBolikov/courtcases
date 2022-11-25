package courtcases.customGUI;

import javafx.beans.NamedArg;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;

public class CustomAlert extends Dialog<ButtonType> {

	public CustomAlert() {
		this("", "", "");
	}

	public CustomAlert(String title, @NamedArg("contentText") String contentText, String headerText,
			ButtonType... buttons) {
		
		super();

		final DialogPane dialogPane = getDialogPane();
		
		dialogPane.setContentText(contentText);		
			
		for (ButtonType btnType : buttons) {
			getDialogPane().getButtonTypes().addAll(btnType);
		}

		setTitle(title);
		
		setHeaderText(headerText);
	}
}
