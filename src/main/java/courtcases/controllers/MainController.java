package courtcases.controllers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.springframework.stereotype.Component;
import courtcases.data.ACase;
import courtcases.data.CaseRepo;
import courtcases.data.Representative;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;

@Component
@FxmlView("main.fxml")
public class MainController {

	private ObservableList<ACase> caseList;

	private Representative user;

	private FxWeaver fxWeaver;
	
	public MainController(CaseRepo caseRepo, FxWeaver fxWeaver) {
		this.caseList = FXCollections.observableArrayList(caseRepo.findAll());
		this.fxWeaver = fxWeaver;
	}

// 	FXML GUI elements

	@FXML
	private Label userLabel;

	@FXML
	private Button changeUser;

	@FXML
	private TableView<ACase> tableView;

	@FXML
	private TableColumn<ACase, String> titleColumn;

	@FXML
	private TableColumn<ACase, String> courtColumn;

	@FXML
	private TableColumn<ACase, String> numberColumn;

	@FXML
	private TableColumn<ACase, String> plaintiffColumn;

	@FXML
	private TableColumn<ACase, String> defendantColumn;

	@FXML
	private TableColumn<ACase, String> reprColumn;

	@FXML
	private TableColumn<ACase, String> dateColumn;

	@FXML
	private Button addCaseButton;
	
	@FXML
	private Button moveCaseToArchiveButton;
	
	@FXML
	private CheckBox archiveCases;

	@FXML
	private RadioButton myCases;

	@FXML
	private RadioButton allCases;

	@FXML
	private ToggleGroup toggleGroup;

//  *********************************************

// 	FXML-annotated methods
	
	@FXML
	public void initialize() {
		titleColumn.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getTitle()));
		courtColumn.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getCourt().toString()));
		numberColumn.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getCase_no()));
		plaintiffColumn.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getPlaintiff()));
		defendantColumn.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getDefendant()));
		reprColumn.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getRepr().toString()));
		dateColumn.setCellValueFactory(f -> {
			SimpleStringProperty property = new SimpleStringProperty();
			DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
			property.setValue(dateFormat.format(f.getValue().getCurr_date()));
			return property;
		});
		myCases.setUserData("myCases");
		allCases.setUserData("allCases");
		toggleGroup.selectedToggleProperty()
				.addListener((obs, o, n) -> tableView.setItems(caseList.filtered(this::filterPredicate)));
		archiveCases.setOnAction(e -> tableView.setItems(caseList.filtered(this::filterPredicate)));
	}

	@FXML
	private void addCase(ActionEvent actionEvent) {
		fxWeaver.loadController(AddCaseController.class).show();
	}

	@FXML
	private void changeUser(ActionEvent actionEvent) {
		FxControllerAndView<LoginController, Parent> login = fxWeaver.load(LoginController.class);
		Stage stage = login.getController().getStage();
		stage.setMaximized(false);
		stage.setScene(new Scene(login.getView().get()));
	}
	
//	***************************************************************************
	
	public void displayUser(Representative user) {
		this.user = user;
		userLabel.setText("Пользователь: " + this.user);
		tableView.setItems(caseList.filtered(this::filterPredicate));
		if (user.isAdmin())
			moveCaseToArchiveButton.setDisable(false);
	}

	private boolean filterPredicate(ACase acase) {
		boolean userFilter = true, archiveFilter = !acase.isArchive();
		if ((String) toggleGroup.getSelectedToggle().getUserData() == "myCases")
			userFilter = acase.getRepr().toString().equals(user.toString());
		if (archiveCases.isSelected())
			archiveFilter = true;
		return userFilter && archiveFilter;
	}
}
