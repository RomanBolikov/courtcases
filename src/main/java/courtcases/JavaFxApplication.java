package courtcases;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class JavaFxApplication extends Application {
	private ConfigurableApplicationContext appContext;

	@Override
	public void init() {
		String[] args = getParameters().getRaw().toArray(new String[0]);
		this.appContext = new SpringApplicationBuilder().sources(CourtCasesApplication.class).run(args);
	}

	@Override
	public void start(Stage primaryStage) {
		appContext.publishEvent(new StageReadyEvent(primaryStage));
	}

	public void stop() {
		this.appContext.close();
		Platform.exit();
	}
}