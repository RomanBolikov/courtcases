package mainapp.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import mainapp.data.CaseFilter;
import mainapp.data.DataModel;
import net.rgielen.fxweaver.core.FxmlView;

@Component
@FxmlView("setfilters.fxml")
public class FilterController {

	private Stage stage;

	private MainController parent;

	private final DataModel model;

	private final CaseFilter caseFilter;

	public FilterController(DataModel model, CaseFilter caseFilter) {
		this.model = model;
		this.caseFilter = caseFilter;
	}

	@FXML
	private GridPane gridPane;

	@FXML
	private ChoiceBox<String> caseTypeChoiceBox;

	@FXML
	private ChoiceBox<String> relationChoiceBox;

	@FXML
	private ChoiceBox<String> representativeChoiceBox;

	@FXML
	private ChoiceBox<String> courtChoiceBox;

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
		List<String> typeList = model.getCaseTypeRepo().findAll().stream().map(type -> type.toString())
				.collect(Collectors.toList());
		caseTypeChoiceBox.setItems(FXCollections.observableArrayList(typeList));
		List<String> relationList = model.getRelationRepo().findAll().stream().map(rel -> rel.toString())
				.collect(Collectors.toList());
		relationChoiceBox.setItems(FXCollections.observableArrayList(relationList));
		List<String> reprList = model.getReprRepo().findAll().stream()
				.map(repr -> repr.toString())
				.sorted()
				.collect(Collectors.toList());
		representativeChoiceBox.setItems(FXCollections.observableArrayList(reprList));
		List<String> courtList = model.getCourtRepo().findAll().stream().map(court -> court.toString())
				.collect(Collectors.toList());
		courtChoiceBox.setItems(FXCollections.observableArrayList(courtList));
	}

	@FXML
	private void applyFilters(ActionEvent actionEvent) {
		caseFilter.setRelation(relationChoiceBox.getValue());
		caseFilter.setType(caseTypeChoiceBox.getValue());
		caseFilter.setRepr(representativeChoiceBox.getValue());
		caseFilter.setCourt(courtChoiceBox.getValue());
		caseFilter.setCurrentDate(currDatePicker.getValue());
		caseFilter.setPlaintiff(plaintiffTextField.getText());
		caseFilter.setDefendant(defendantTextField.getText());
		stage.close();
		parent.disableDropFilterButton(false);
		parent.refreshTable();
	}

	@FXML
	private void dropFilters(ActionEvent actionEvent) {
		caseTypeChoiceBox.setValue(null);
		relationChoiceBox.setValue(null);
		representativeChoiceBox.setValue(null);
		courtChoiceBox.setValue(null);
		currDatePicker.setValue(null);
		plaintiffTextField.setText("");
		defendantTextField.setText("");
	}

	public void setParent(MainController parent) {
		this.parent = parent;
	}

	public void show() {
		caseTypeChoiceBox.setValue(caseFilter.getType());
		relationChoiceBox.setValue(caseFilter.getRelation());
		representativeChoiceBox.setValue(caseFilter.getRepr());
		courtChoiceBox.setValue(caseFilter.getCourt());
		currDatePicker.setValue(caseFilter.getCurrentDate());
		stage.show();
	}
}
