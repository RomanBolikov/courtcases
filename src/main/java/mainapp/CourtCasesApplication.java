package mainapp;

//import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.annotation.Bean;

import javafx.application.Application;

@SpringBootApplication
public class CourtCasesApplication {

//	@Bean
//	public ModelMapper modelMapper() {
//		return new ModelMapper();
//	}
	
	public static void main(String[] args) {
		Application.launch(JavaFxApplication.class, args);
	}
}
