package courtcases;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import courtcases.controllers.LoginController;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;

@Component
public class PrimaryStageInitializer implements ApplicationListener<StageReadyEvent> {
	private final FxWeaver fxWeaver;

	public PrimaryStageInitializer(FxWeaver fxWeaver) {
		this.fxWeaver = fxWeaver;
	}

	@Override
	public void onApplicationEvent(StageReadyEvent event) {
		Stage stage = event.stage;
		Parent root = fxWeaver.loadView(LoginController.class);
		stage.setScene(new Scene(root));
		stage.show();
	}
}
