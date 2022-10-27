package courtcases;

import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import javafx.application.Application;

@SpringBootApplication
public class CourtCasesApplication {

	public static void main(String[] args) {
		Application.launch(JavaFxApplication.class, args);
	}
}
