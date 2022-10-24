package courtcases;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JavaFxApplication extends Application {
	private ConfigurableApplicationContext appContext;

	@Override
	public void init() {
		String[] args = getParameters().getRaw().toArray(new String[0]);
		this.appContext = new SpringApplicationBuilder().sources(CourtcasesApplication.class).run(args);
	}

	@Override
	public void start(Stage stage) {
		FxWeaver fxweaver = appContext.getBean(MyController.class);
		Parent root = fxWeaver.loadview(MyController.class);
		Scene scene = new Scene(root);
		stage.show();

	}

	public void stop() {
		this.appContext.close();
		Platform.exit();
	}
}