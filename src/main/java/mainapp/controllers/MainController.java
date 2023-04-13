package mainapp.controllers;

import java.io.File;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
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
import mainapp.customGUI.PasswordInputDialog;
import mainapp.customGUI.ReportTypeDialog;
import mainapp.data.ACase;
import mainapp.data.Representative;
import mainapp.helpers.CaseFilter;
import mainapp.helpers.SaveEntityException;
import mainapp.helpers.XLSXFileWriter;
import mainapp.services.CaseService;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;

@Component
@FxmlView("main.fxml")
public class MainController {

	private final CaseService caseService;

	private ObservableList<ACase> caseList;

	private FilteredList<ACase> filteredList;

	private SortedList<ACase> sortedList;

	private final CaseFilter caseFilter;

	private Representative user;

	private FxWeaver fxWeaver;

	private Stage stage;

	private final Image restoreIcon = new Image(
			this.getClass().getResource("/mainapp/images/restore.png").toExternalForm());

	private final Image removeIcon = new Image(
			this.getClass().getResource("/mainapp/images/archive.png").toExternalForm());

	public MainController(CaseService caseService, FxWeaver fxWeaver, CaseFilter casefilter) {
		this.caseService = caseService;
		this.caseList = caseService.getAllCases();
		this.filteredList = caseList.filtered(null);
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
	private Button deleteCaseButton;

	@FXML
	private Button applyFilterButton;

	@FXML
	private Button dropFilterButton;

	@FXML
	private CheckBox archiveCheckbox;

	@FXML
	private Label numberLabel;

	@FXML
	private Button createReportButton;

//  *********************************************

// 	FXML-annotated methods

	@FXML
	public void initialize() {
		titleColumn.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getTitle()));
		titleColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.37));
		courtColumn.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getCourt().toString()));
		courtColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.14));
		numberColumn.setCellValueFactory(cdf -> {
			SimpleStringProperty property = new SimpleStringProperty();
			String caseNo = cdf.getValue().getCaseNo();
			property.setValue(caseNo == null ? "" : caseNo);
			return property;
		});
		numberColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.07));
		plaintiffColumn.setCellValueFactory(cdf -> {
			SimpleStringProperty property = new SimpleStringProperty();
			String plaintiff = cdf.getValue().getPlaintiff();
			property.setValue(plaintiff == null ? "" : plaintiff);
			return property;
		});
		plaintiffColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.14));
		defendantColumn.setCellValueFactory(cdf -> {
			SimpleStringProperty property = new SimpleStringProperty();
			String defendant = cdf.getValue().getDefendant();
			property.setValue(defendant == null ? "" : defendant);
			return property;
		});
		defendantColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.14));
		reprColumn.setCellValueFactory(cdf -> {
			if (cdf.getValue().getRepr() == null) {
				return new SimpleStringProperty("");
			}
			String repr = cdf.getValue().getRepr().toString();
			return new SimpleStringProperty(repr);
		});
		reprColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.07));
		dateColumn.setCellValueFactory(cdf -> {
			SimpleStringProperty property = new SimpleStringProperty();
			ACase thisCase = cdf.getValue();
			Timestamp date = thisCase.getCurrentDate();
			if (date == null) {
				property.setValue(thisCase.isArchive() ? "" : "не назначено");
			} else {
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
						} else if (!item.isEmpty()) {
							if (item.equals("не назначено")) {
								setTextFill(Color.BLUE);
							} else {
								DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
								LocalDateTime datetime = LocalDateTime.parse(item, formatter);
								setTextFill(datetime.isBefore(LocalDateTime.now()) ? Color.RED : Color.BLACK);
							}
						}
					}
				};
				cell.itemProperty().addListener((obs, oldVal, newVal) -> {
					if (newVal != null) {
						cell.setText(newVal);
					}
				});
				return cell;
			};
		});
		dateColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.07));
		// setting rows displayed in green for archived cases and make rows editable by
		// double-clicking
		tableView.setRowFactory(tv -> {
			TableRow<ACase> row = new TableRow<ACase>() {
				@Override
				protected void updateItem(ACase item, boolean empty) {
					super.updateItem(item, empty);
					if (empty || item == null) {
						setStyle("");
						setGraphic(null);
					} else {
						for (int i = 0; i < getChildren().size() - 1; i++) {
							((Labeled) getChildren().get(i)).setTextFill(item.isArchive() ? Color.GREEN : Color.BLACK);
						}
					}
				}
			};
			row.setOnMouseClicked(evt -> {
				if (evt.getButton().equals(MouseButton.PRIMARY) && evt.getClickCount() == 2 && row.getItem() != null) {
					editCase(new ActionEvent());
				}
			});
			return row;
		});
		tableView.getSelectionModel().selectedItemProperty().addListener(this::enableButtons);
		tableView.getSortOrder().add(reprColumn);
		archiveCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> refreshTable());
		numberLabel.textProperty()
				.bind(new SimpleStringProperty("Выбрано дел: ").concat(Bindings.size(filteredList).asString()));
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
		ACase caseToEdit = tableView.getSelectionModel().selectedItemProperty().getValue();
		if (!caseToEdit.isEditable())
			return;
		caseToEdit.setEditable(false);
		EditCaseController editController = fxWeaver.loadController(EditCaseController.class);
		editController.show(caseList, user, caseToEdit);
	}

	@FXML
	private void moveCaseToArchive(ActionEvent actionEvent) {
		Optional<ButtonType> confirmed = new CustomAlert("Подтверждение", "Переместить дело в архив?", "",
				ButtonType.OK, new ButtonType("Отмена", ButtonData.CANCEL_CLOSE)).showAndWait();
		if (confirmed.isPresent() && confirmed.get() == ButtonType.OK) {
			ACase caseToBeMoved = tableView.getSelectionModel().getSelectedItem();
			int id = caseToBeMoved.getId();
			caseToBeMoved.setIsArchive(true);
			caseToBeMoved.setCurrentDate(null);
			try {
				ACase edited = caseService.saveCase(caseToBeMoved);
				caseList.add(edited);
				new CustomAlert("Перемещение в архив", "", "Дело перемещено в архив!", ButtonType.OK).show();
			} catch (SaveEntityException see) {
				new CustomAlert("Обновление данных", "", "Параметры дела изменены другим пользователем!", ButtonType.OK)
						.show();
				caseList.add(caseService.getCaseById(id));
			} finally {
				caseList.remove(caseToBeMoved);
			}
		}
	}

	@FXML
	private void restoreCaseFromArchive(ActionEvent actionEvent) {
		Optional<ButtonType> confirmed = new CustomAlert("Подтверждение", "Восстановить дело из архива?", "",
				ButtonType.OK, new ButtonType("Отмена", ButtonData.CANCEL_CLOSE)).showAndWait();
		if (confirmed.isPresent() && confirmed.get() == ButtonType.OK) {
			ACase caseToRestore = tableView.getSelectionModel().getSelectedItem();
			int id = caseToRestore.getId();
			caseToRestore.setIsArchive(false);
			try {
				ACase edited = caseService.saveCase(caseToRestore);
				caseList.add(edited);
			} catch (SaveEntityException see) {
				new CustomAlert("Обновление данных", "", "Параметры дела изменены другим пользователем!", ButtonType.OK)
						.show();
				caseList.add(caseService.getCaseById(id));
			} finally {
				caseList.remove(caseToRestore);
			}
		}
	}

	@FXML
	private void deleteCase(ActionEvent actionEvent) {
		Optional<ButtonType> confirmed = new CustomAlert("Подтверждение", "Вы уверены, что хотите удалить дело?", "",
				ButtonType.OK, new ButtonType("Отмена", ButtonData.CANCEL_CLOSE)).showAndWait();
		if (confirmed.isEmpty() || confirmed.get().getButtonData() == ButtonData.CANCEL_CLOSE)
			return;
		Dialog<String> prompt = new PasswordInputDialog();
		prompt.setHeaderText("Ввведите пароль");
		CustomAlert alert = new CustomAlert("Ошибка ввода", "Введен неверный пароль!", "",
				new ButtonType("Повторить", ButtonData.OK_DONE), new ButtonType("Закрыть", ButtonData.CANCEL_CLOSE));
		while (true) {
			Optional<String> input = prompt.showAndWait();
			if (input.isEmpty())
				break;
			if (BCrypt.checkpw(input.get(), user.getPassword())) {
				ACase caseToBeDeleted = tableView.getSelectionModel().getSelectedItem();
				int id = caseToBeDeleted.getId();
				try {
					caseService.deleteCase(id);
					caseList.remove(caseToBeDeleted);
					new CustomAlert("Удаление дела", "", "Дело удалено!", ButtonType.OK).show();
					break;
				} catch (SaveEntityException see) {
					new CustomAlert("Удаление дела", "", "Произошла ошибка базы данных!", ButtonType.OK).show();
					break;
				}
			} else {
				Optional<ButtonType> retry = alert.showAndWait();
				if (retry.isEmpty() || retry.get().getButtonData() == ButtonData.CANCEL_CLOSE)
					break;
			}
		}
	}

	@FXML
	private void createReport(ActionEvent actionEvent) {
		DirectoryChooser dialog = new DirectoryChooser();
		dialog.setInitialDirectory(new File(System.getProperty("user.dir")));
		File saveDir = dialog.showDialog(stage);
		if (saveDir == null)
			return;
		Optional<Boolean> includeAllCases = new ReportTypeDialog().showAndWait();
		if (includeAllCases.isEmpty())
			return;
		List<ACase> casesToInclude = includeAllCases.get()
				? caseList.stream().filter(acase -> !acase.isArchive()).toList()
				: tableView.getItems().stream().toList();
		boolean reportSaved = XLSXFileWriter.createReport(saveDir, casesToInclude);
		if (reportSaved) {
			new CustomAlert("Сохранение отчета", "Отчет сохранен!", "", ButtonType.OK).show();
		} else {
			new CustomAlert("Сохранение отчета", "Не удалось сохранить отчет!", "", ButtonType.CLOSE).show();
		}
	}

//	***************************************************************************

	public void displayUser(Representative user) {
		this.user = user;
		userLabel.setText("Пользователь: " + this.user);
		if (!user.isAdmin()) {
			archiveButton.setDisable(true);
			tableView.getSelectionModel().selectedItemProperty().removeListener(this::modifyArchiveButton);
		} else {
			tableView.getSelectionModel().selectedItemProperty().addListener(this::modifyArchiveButton);
		}
		caseFilter.setRepr(user);
		refreshTable();
	}

	private void modifyArchiveButton(ObservableValue<? extends ACase> property, ACase oldValue, ACase newValue) {
		if (newValue == null) {
			archiveButton.setDisable(true);
		} else {
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

	private void enableButtons(ObservableValue<? extends ACase> property, ACase oldValue, ACase newValue) {
		if (newValue == null) {
			editCaseButton.setDisable(true);
			deleteCaseButton.setDisable(true);
		} else if (newValue.isArchive()) {
			editCaseButton.setDisable(true);
			if (user.isAdmin()) {
				deleteCaseButton.setDisable(false);
			}
		} else {
			editCaseButton.setDisable(false);
			if (user.isAdmin()) {
				deleteCaseButton.setDisable(false);
			}
		}
	}

	public void refreshTable() {
		filteredList.setPredicate(caseFilter.and(acase -> archiveCheckbox.isSelected() ? true : !acase.isArchive()));
		sortedList = new SortedList<>(filteredList);
		sortedList.comparatorProperty().bind(tableView.comparatorProperty());
		tableView.setItems(sortedList);
		tableView.sort();
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
}
