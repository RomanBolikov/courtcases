package courtcases;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;

public class JavaFxApplication extends Application {
	private ConfigurableApplicationContext appContext;

	@Override
	public void init() {
		String[] args = getParameters().getRaw().toArray(new String[0]);
		this.appContext = new SpringApplicationBuilder().sources(CourtCasesApplication.class).run(args);
	}

	@Override
	public void start(Stage stage) {
		FxWeaver fxWeaver = appContext.getBean(FxWeaver.class);
		Parent parent = fxWeaver.loadView(LoginController.class);
		Scene scene = new Scene(parent);
		stage.setScene(scene);
		stage.setTitle("Авторизация");
		stage.show();
	}

	public void stop() {
		this.appContext.close();
		Platform.exit();
	}
}