package mainapp.controllers;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import javax.persistence.OptimisticLockException;

import org.apache.poi.EncryptedDocumentException;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import mainapp.customGUI.CustomAlert;
import mainapp.data.ACase;
import mainapp.data.CaseFilter;
import mainapp.data.CaseRepo;
import mainapp.data.DataModel;
import mainapp.data.Representative;
import mainapp.data.XLSXFileWriter;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;

@Component
@FxmlView("main1.fxml")
public class MainController {

	private final CaseRepo caseRepo;

	private ObservableList<ACase> caseList;

	private final CaseFilter caseFilter;

	private Representative user;

	private FxWeaver fxWeaver;

	private Stage stage;

	private final Image restoreIcon = new Image(
			this.getClass().getResource("/mainapp/images/restore.png").toExternalForm());

	private final Image removeIcon = new Image(
			this.getClass().getResource("/mainapp/images/remove.png").toExternalForm());

	public MainController(DataModel model, FxWeaver fxWeaver, CaseFilter casefilter) {
		this.caseRepo = model.getCaseRepo();
		this.caseList = FXCollections.observableArrayList(model.getCaseRepo().findAll());
		this.fxWeaver = fxWeaver;
		this.caseFilter = casefilter;
	}

// 	FXML GUI elements

	@FXML
	private Label filterLabel;

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
	private Button applyFilterButton;

	@FXML
	private Button dropFilterButton;

	@FXML
	private CheckBox archiveCheckbox;

	@FXML
	private Button refreshButton;

	@FXML
	private Button createReportButton;

//  *********************************************

// 	FXML-annotated methods

	@FXML
	public void initialize() {
		titleColumn.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getTitle()));
		courtColumn.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getCourt().toString()));
		numberColumn.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getCaseNo()));
		plaintiffColumn.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getPlaintiff()));
		defendantColumn.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getDefendant()));
		reprColumn.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getRepr().toString()));
		dateColumn.setCellValueFactory(cdf -> {
			SimpleStringProperty property = new SimpleStringProperty();
			ACase thisCase = cdf.getValue();
			Timestamp date = thisCase.getCurrentDate();
			if (date == null)
				property.setValue(thisCase.isArchive() ? "" : "не назначено");
			else {
				LocalDateTime datetime = date.toLocalDateTime();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
				property.setValue(formatter.format(datetime));
			}
			return property;
		});
		// setting cells displayed in red if date not appointed or is expired
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
						} else if (!item.isEmpty() && !item.equals("не назначено")) {
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
		// setting rows displayed in green for archived cases and make rows editable by
		// double-clicking
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
		tableView.getSelectionModel().selectedItemProperty().addListener(this::enableEditButton);
		archiveCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> refreshTable());
		refreshButton.setOnAction(evt -> tableView.refresh());
	}

	@FXML
	private void applyFilters(ActionEvent actionEvent) {
		FilterController filterController = fxWeaver.loadController(FilterController.class);
		filterController.setParent(this);
		filterController.show();
	}

	@FXML
	private void dropFilters(ActionEvent actionEvent) {
		caseFilter.setCourt(null);
		caseFilter.setCurrentDate(null);
		caseFilter.setDefendant(null);
		caseFilter.setPlaintiff(null);
		caseFilter.setRelation(null);
		caseFilter.setRepr(null);
		caseFilter.setType(null);
		disableDropFilterButton(true);
		refreshTable();
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
			acase.setCurrentDate(null);
			try {
				caseRepo.save(acase);
			} catch (OptimisticLockException ole) {
				new CustomAlert("Обновление данных", "", "Параметры дела изменены другим пользователем!", ButtonType.OK)
						.show();
			} finally {
				refreshTable();
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

	@FXML
	private void createReport(ActionEvent actionEvent) {
		DirectoryChooser dialog = new DirectoryChooser();
		dialog.setInitialDirectory(new File("C:\\Prog\\Java\\Spring\\testdir"));
		File saveDir = dialog.showDialog(stage);
		boolean reportSaved;
		try {
			reportSaved = XLSXFileWriter.createReport(saveDir);
		} catch (EncryptedDocumentException e) {
			reportSaved = false;
			e.printStackTrace();
		} catch (IOException e) {
			reportSaved = false;
			e.printStackTrace();
		}
		if (reportSaved) 
			new CustomAlert("Сохранение отчета", "Отчет сохранен!", "", ButtonType.OK).show();
		else
			new CustomAlert("Сохранение отчета", "Не удалось сохранить отчет!", "", ButtonType.CLOSE).show();
	}

//	***************************************************************************

	/**
	 * loads the main stage with a certain user (aka Representative) modifies
	 * Archive Button, implementing privilege restrictions for non-admin users to
	 * move and restore cases to/from archive
	 * 
	 * @param user the Representative chosen on login stage
	 */
	public void displayUser(Representative user) {
		this.user = user;
		userLabel.setText("Пользователь: " + this.user);
		if (!user.isAdmin()) {
			archiveButton.setDisable(true);
			tableView.getSelectionModel().selectedItemProperty().removeListener(this::modifyArchiveButton);
		} else
			tableView.getSelectionModel().selectedItemProperty().addListener(this::modifyArchiveButton);
		caseFilter.setRepr(user.toString());
		refreshTable();
	}

	/**
	 * this method is bound to a ChangeListener and modifies the Archive Button,
	 * disabling it if no case is selected, and changing its onAction event, text
	 * and icon depending on whether the selected case is in archive
	 * 
	 * @param property - selection property (i.e. the case selected)
	 * @param oldValue - the case previously selected - this parameter is
	 *                 disregarded
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

	void disableDropFilterButton(boolean disable) {
		dropFilterButton.setDisable(disable);
		filterLabel.setVisible(!disable);
	}

	/**
	 * if the case selected is an archive one, this method disables edit button
	 * 
	 * @param property - selection property (i.e. the case selected)
	 * @param oldValue - the case previously selected - this parameter is
	 *                 disregarded
	 * @param newValue - the case currently selected
	 */
	private void enableEditButton(ObservableValue<? extends ACase> property, ACase oldValue, ACase newValue) {
		if (newValue == null || newValue.isArchive())
			editCaseButton.setDisable(true);
		else
			editCaseButton.setDisable(false);
	}

	public void refreshTable() {
		tableView.setItems(
				caseList.filtered(caseFilter.and(acase -> archiveCheckbox.isSelected() ? true : !acase.isArchive())));
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
}
