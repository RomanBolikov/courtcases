package mainapp.controllers;

import java.util.function.Predicate;

import org.springframework.stereotype.Component;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import mainapp.data.CaseType;
import mainapp.data.Court;
import mainapp.data.DataModel;
import mainapp.data.Relation;
import mainapp.data.Representative;
import net.rgielen.fxweaver.core.FxmlView;

@Component
@FxmlView("setfilters.fxml")
public class FilterController {
	
	private Stage stage;
	
	private MainController parent;
	
	private final DataModel model;

	public FilterController(DataModel model) {
		this.model = model;
	}

	@FXML
	private GridPane gridPane;

	@FXML
	private ChoiceBox<CaseType> caseTypeChoiceBox;

	@FXML
	private ChoiceBox<Relation> relationChoiceBox;

	@FXML
	private ChoiceBox<Representative> representativeChoiceBox;

	@FXML
	private ComboBox<Court> courtComboBox;

	@FXML
	private DatePicker currDatePicker;

	@FXML
	private TextField plaintiffTextField;

	@FXML
	private TextField defendantTextField;

	@FXML
	protected Button applyFilterButton;
	
	@FXML
	protected Button dropFilterButton;
	
	@FXML
	public void initialize() {
		this.stage = new Stage();
		stage.setTitle("Установка фильтров в таблице");
		stage.setResizable(false);
		stage.setScene(new Scene(gridPane));
		caseTypeChoiceBox.setItems(FXCollections.observableArrayList(model.getCaseTypeRepo().findAll()));
		relationChoiceBox.setItems(FXCollections.observableArrayList(model.getRelationRepo().findAll()));
		representativeChoiceBox.setItems(FXCollections.observableArrayList(model.getReprRepo().findAll()));
		courtComboBox.setItems(FXCollections.observableArrayList(model.getCourtRepo().findAll()));
		courtComboBox.setConverter(new StringConverter<Court>() {
			@Override
			public String toString(Court court) {
				if (court == null)
					return "";
				return court.getName();
			}

			@Override
			public Court fromString(String string) {
				return new Court(string);
			}
		});
		
		ObjectProperty<Predicate<String>> plaintiffFilter = new SimpleObjectProperty<>();
        ObjectProperty<Predicate<String>> defendantFilter = new SimpleObjectProperty<>();

        plaintiffFilter.bind(Bindings.createObjectBinding(() -> 
            acase -> acase.getPlaintiff().toLowerCase().contains(nameFilterField.getText().toLowerCase()), 
            nameFilterField.textProperty()));
	}
	
	public void setParent(MainController parent) {
		this.parent = parent;
	}
}
