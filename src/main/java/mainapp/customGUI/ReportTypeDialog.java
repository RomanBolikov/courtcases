package mainapp.customGUI;

import javafx.geometry.Pos;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

public class ReportTypeDialog extends Dialog<Boolean> {
	
	public ReportTypeDialog() {

		final DialogPane dialogPane = getDialogPane();
		
		VBox vbox = new VBox(10);
		
		ToggleGroup tg = new ToggleGroup();
		
		RadioButton btn1 = new RadioButton("Все дела (стандартный отчет)");
		btn1.setToggleGroup(tg);
		btn1.setUserData("allCases");
		btn1.setSelected(true);
		
		RadioButton btn2 = new RadioButton("Только выбранные дела");
		btn2.setToggleGroup(tg);
		btn2.setUserData("");
		btn2.setSelected(false);
		
		vbox.setAlignment(Pos.CENTER);
		
		vbox.getChildren().addAll(btn1, btn2);
		
		dialogPane.setContent(vbox);
		
		dialogPane.getButtonTypes().addAll(ButtonType.OK, new ButtonType("Отмена", ButtonData.CANCEL_CLOSE));
			
		setResultConverter((dialogButton) -> {
			ButtonData data = dialogButton == null ? null : dialogButton.getButtonData();
			if (data == null || data == ButtonData.CANCEL_CLOSE) {
				return null;
			}
			return tg.getSelectedToggle().getUserData().toString().equals("allCases");
		});
		
	}
}
