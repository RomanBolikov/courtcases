package mainapp;


import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import ch.qos.logback.classic.Logger;
import javafx.application.Application;
import javafx.application.Platform;

@SpringBootApplication
public class CourtCasesApplication {
	
	private static final Logger logger = (Logger) LoggerFactory.getLogger("courtcases.mainapp.logback");
	
	public static void main(String[] args) {
		try {
			Application.launch(JavaFxApplication.class, args);
		} catch (RuntimeException rte) {
			logger.error("Произошла ошибка", rte);
			Platform.exit();
		}
	}
}
