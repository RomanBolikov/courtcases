package mainapp.controllers;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import javax.persistence.OptimisticLockException;

import org.springframework.stereotype.Component;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import mainapp.customGUI.CustomAlert;
import mainapp.data.ACase;
import mainapp.data.CaseRepo;
import mainapp.data.Representative;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;

@Component
@FxmlView("main.fxml")
public class MainController {

	private CaseRepo caseRepo;

	private ObservableList<ACase> caseList;

	private Representative user;

	private FxWeaver fxWeaver;

	private final Image restoreIcon = new Image(
			this.getClass().getResource("/mainapp/images/restore.png").toExternalForm());

	private final Image removeIcon = new Image(
			this.getClass().getResource("/mainapp/images/remove.png").toExternalForm());

	public MainController(CaseRepo caseRepo, FxWeaver fxWeaver) {
		this.caseRepo = caseRepo;
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
	private Button editCaseButton;

	@FXML
	private Button archiveButton;

	@FXML
	private ImageView archiveIcon;

	@FXML
	private CheckBox archiveCases;

	@FXML
	private RadioButton myCases;

	@FXML
	private RadioButton allCases;

	@FXML
	private ToggleGroup toggleGroup;
	
	@FXML
	private Button refreshButton;
	

//  *********************************************

// 	FXML-annotated methods

	@FXML
	public void initialize() {
		titleColumn.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getTitle()));
		courtColumn.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getCourt().toString()));
		numberColumn.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getCase_no()));
		plaintiffColumn.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getPlaintiff()));
		defendantColumn.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getDefendant()));
		reprColumn.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getRepr().toString()));
		dateColumn.setCellValueFactory(cdf -> {
			SimpleStringProperty property = new SimpleStringProperty();
			ACase thisCase = cdf.getValue();
			Timestamp date = thisCase.getCurr_date();
			if (date == null)
				property.setValue(thisCase.isArchive() ? "" : "не назначено");
			else {
				LocalDateTime datetime = date.toLocalDateTime();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
				property.setValue(formatter.format(datetime));
			}
			return property;
		});
		// setting cells displayed in red if no date or date is expired
		dateColumn.setCellFactory(new Callback<TableColumn<ACase, String>, TableCell<ACase, String>>() {
			@Override
			public TableCell<ACase, String> call(TableColumn<ACase, String> param) {
				TableCell<ACase, String> cell = new TableCell<ACase, String>() {
					@Override
					protected void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if (empty || item == null) {
							setText(null);
							setGraphic(null);
							setStyle("");
						} else if (item.equals("не назначено"))
							setTextFill(Color.RED);
						else if (!item.isEmpty()) {
							DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
							LocalDateTime datetime = LocalDateTime.parse(item, formatter);
							setTextFill(datetime.isBefore(LocalDateTime.now()) ? Color.RED : Color.BLACK);
						}
					}
				};
				cell.itemProperty().addListener((obs, oldVal, newVal) -> {
					if (newVal != null)
						cell.setText(newVal);
				});
				return cell;
			};
		});
		// setting rows displayed in green for archived cases and make rows editable by double-clicking
		tableView.setRowFactory(tv -> {
			TableRow<ACase> row = new TableRow<ACase>() {
				@Override
				protected void updateItem(ACase item, boolean empty) {
					super.updateItem(item, empty);
					if (empty || item == null)
						setStyle("");
					else {
						for (int i = 0; i < getChildren().size() - 1; i++) {
							((Labeled) getChildren().get(i)).setTextFill(item.isArchive() ? Color.GREEN : Color.BLACK);
						}
					}
				}
			};
			row.setOnMouseClicked(evt -> {
				if (evt.getButton().equals(MouseButton.PRIMARY) && evt.getClickCount() == 2)
			        editCase(new ActionEvent());	
				});
			return row;
		});
		myCases.setUserData("myCases");
		allCases.setUserData("allCases");
		toggleGroup.selectedToggleProperty()
				.addListener((obs, o, n) -> tableView.setItems(caseList.filtered(this::filterPredicate)));
		archiveCases.setOnAction(e -> tableView.setItems(caseList.filtered(this::filterPredicate)));
		tableView.getSelectionModel().selectedItemProperty().addListener(this::enableEditButton);
		refreshButton.setOnAction(e -> tableView.refresh());
	}

	@FXML
	private void addCase(ActionEvent actionEvent) {
		fxWeaver.loadController(AddCaseController.class).show(caseList, user);
	}

	@FXML
	private void changeUser(ActionEvent actionEvent) {
		FxControllerAndView<LoginController, Parent> login = fxWeaver.load(LoginController.class);
		Stage stage = login.getController().getStage();
		stage.setMaximized(false);
		stage.setScene(new Scene(login.getView().get()));
	}

	@FXML
	private void editCase(ActionEvent actionEvent) {
		EditCaseController editController = fxWeaver.loadController(EditCaseController.class);
		editController.setParent(this); // this is needed to refresh main stage TableView after editing
		editController.show(tableView.getSelectionModel().selectedItemProperty().getValue());
	}

	@FXML
	private void moveCaseToArchive(ActionEvent actionEvent) {
		Optional<ButtonType> confirmed = new CustomAlert("Подтверждение", "Переместить дело в архив?", "",
				ButtonType.OK, new ButtonType("Отмена", ButtonData.CANCEL_CLOSE)).showAndWait();
		if (confirmed.isPresent() && confirmed.get() == ButtonType.OK) {
			ACase acase = tableView.getSelectionModel().getSelectedItem();
			acase.setIsArchive(true);
			acase.setCurr_date(null);
			try {
				caseRepo.save(acase);
			} catch (OptimisticLockException ole) {
				new CustomAlert("Обновление данных", "", "Параметры дела изменены другим пользователем!", ButtonType.OK)
				.show();
			} finally {
				refreshTable();
				tableView.setItems(caseList.filtered(this::filterPredicate));
			}
		}
	}

	@FXML
	private void restoreCaseFromArchive(ActionEvent actionEvent) {
		Optional<ButtonType> confirmed = new CustomAlert("Подтверждение", "Восстановить дело из архива?", "",
				ButtonType.OK, new ButtonType("Отмена", ButtonData.CANCEL_CLOSE)).showAndWait();
		if (confirmed.isPresent() && confirmed.get() == ButtonType.OK) {
			ACase acase = tableView.getSelectionModel().getSelectedItem();
			acase.setIsArchive(false);
			caseRepo.save(acase);
			refreshTable();
		}
	}

//	***************************************************************************

	/**
	 * loads the main stage with a certain user (aka Representative)
	 * modifies Archive Button, implementing privilege restrictions for non-admin users to move and restore cases
	 * to/from archive
	 * @param user the Representative chosen on login stage
	 */
	public void displayUser(Representative user) {
		this.user = user;
		userLabel.setText("Пользователь: " + this.user);
		tableView.setItems(caseList.filtered(this::filterPredicate));
		if (!user.isAdmin()) {
			archiveButton.setDisable(true);
			tableView.getSelectionModel().selectedItemProperty().removeListener(this::modifyArchiveButton);
		} else
			tableView.getSelectionModel().selectedItemProperty().addListener(this::modifyArchiveButton);
	}

	/**
	 * this method is bound to a ChangeListener and modifies the Archive Button, disabling it if no case is selected,
	 * and changing its onAction event, text and icon depending on whether the selected case is in archive
	 * @param property - selection property (i.e. the case selected)
	 * @param oldValue - the case previously selected - this parameter is disregarded
	 * @param newValue - the case currently selected
	 */
	private void modifyArchiveButton(ObservableValue<? extends ACase> property, ACase oldValue, ACase newValue) {
		if (newValue == null)
			archiveButton.setDisable(true);
		else {
			archiveButton.setDisable(false);
			archiveButton.setOnAction(newValue.isArchive() ? this::restoreCaseFromArchive : this::moveCaseToArchive);
			archiveButton.setText(newValue.isArchive() ? "Из архива" : "В архив");
			archiveIcon.setImage(newValue.isArchive() ? restoreIcon : removeIcon);
		}
	}

	/**
	 * if the case selected is an archive one, this method disables edit button
	 * @param property - selection property (i.e. the case selected)
	 * @param oldValue - the case previously selected - this parameter is disregarded
	 * @param newValue - the case currently selected
	 */
	private void enableEditButton(ObservableValue<? extends ACase> property, ACase oldValue, ACase newValue) {
		if (newValue == null || newValue.isArchive())
			editCaseButton.setDisable(true);
		else
			editCaseButton.setDisable(false);
	}

	/**
	 * this filter defines which cases are displayed in the TableView
	 * 'allCases' is a user filter that defines whether only user-specific or all cases should be displayed
	 * 'includeArchiveCases' is a filter that defines whether archived cases should be included
	 * initially user filter is set to 'true' (meaning all cases are displayed), however because the default
	 * value for the correspondent ToggleGroup is 'myCases', in effect only the latter are displayed on loading
	 * archive filter is initially set to filter out archived cases
	 * @param acase - a case that is to be filtered
	 * @return a logical AND of user and archive filters
	 */
	private boolean filterPredicate(ACase acase) {
		boolean allCases = true, includeArchiveCases = !acase.isArchive();
		if ((String) toggleGroup.getSelectedToggle().getUserData() == "myCases")
			allCases = acase.getRepr().toString().equals(user.toString());
		if (archiveCases.isSelected())
			includeArchiveCases = true;
		return allCases && includeArchiveCases;
	}
	
	public void refreshTable() {
		tableView.refresh();
	}
}
