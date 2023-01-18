package mainapp;

import org.springframework.context.ApplicationEvent;
import javafx.stage.Stage;

@SuppressWarnings("serial")
public class StageReadyEvent extends ApplicationEvent {
	public final Stage stage;

	public StageReadyEvent(Stage stage) {
		super(stage);
		this.stage = stage;
	}
}
