package mainapp.controllers;

import java.util.Optional;

import org.springframework.data.util.Pair;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import mainapp.customGUI.CustomAlert;
import mainapp.customGUI.PasswordSetDialog;
import mainapp.data.ACase;
import mainapp.data.Representative;
import mainapp.helpers.SaveEntityException;
import mainapp.services.CaseService;
import mainapp.services.ReprService;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;

@Component
@FxmlView("admin.fxml")
public class AdminController {

	private final CaseService caseService;

	private final ReprService reprService;

	private ObservableList<Representative> reprList;

	private final FxWeaver fxWeaver;

	public AdminController(CaseService caseService, ReprService reprService, FxWeaver fxWeaver) {
		this.caseService = caseService;
		this.reprService = reprService;
		this.reprList = reprService.getAllReprs();
		this.fxWeaver = fxWeaver;
	}

// 	FXML GUI elements

	@FXML
	private TableView<Representative> tableView;

	@FXML
	private TableColumn<Representative, String> nameColumn;

	@FXML
	private TableColumn<Representative, Boolean> isAdminColumn;

	@FXML
	private Button addUserButton;

	@FXML
	private Button editUserButton;

	@FXML
	private Button deleteUserButton;

	@FXML
	private Button logoutButton;

//  *********************************************

// 	FXML-annotated methods

	@FXML
	public void initialize() {
		nameColumn.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getName()));
		isAdminColumn.setCellValueFactory(cdf -> new SimpleBooleanProperty(cdf.getValue().isAdmin()));
		isAdminColumn.setCellFactory(column -> {
			TableCell<Representative, Boolean> cell = new TableCell<>();
			cell.itemProperty().addListener((obs, oldVal, newVal) -> {
				if (newVal != null) {
					Node checkbox = createCheckbox(newVal);
					cell.graphicProperty()
							.bind(Bindings.when(cell.emptyProperty()).then((Node) null).otherwise(checkbox));
				}
			});
			return cell;
		});
		tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
			editUserButton.setDisable(newVal == null ? true : false);
			deleteUserButton.setDisable(newVal == null ? true : false);
		});
		reprList.sort((r1, r2) -> r1.getName().compareTo(r2.getName()));
		tableView.setItems(reprList);
	}

	@FXML
	private void addUser(ActionEvent actionEvent) {
		Optional<Representative> newUserOptional = fxWeaver.loadController(AddUserController.class).showAndWait();
		if (newUserOptional.isEmpty())
			return;
		Representative newUser = newUserOptional.get();
		try {
			reprService.addNewRepr(newUser);
		} catch (SaveEntityException see) {
			new CustomAlert("Ошибка", "", "Возникла ошибка при сохранении нового пользователя", ButtonType.OK).show();
			return;
		}
		reprList.add(newUser);
//		tableView.refresh();
	}

	@FXML
	private void editUser(ActionEvent actionEvent) {
		Representative userToEdit = tableView.getSelectionModel().selectedItemProperty().getValue();
		Pair<String, Boolean> result = fxWeaver.loadController(EditUserController.class).showAndWait(userToEdit);
		if (result == null)
			return;
		userToEdit.setName(result.getFirst());
		userToEdit.setIsAdmin(result.getSecond());
		if (!userToEdit.isAdmin()) {
			try {
				reprService.updateRepr(userToEdit.getId(), userToEdit);
			} catch (SaveEntityException see) {
				new CustomAlert("Ошибка", "", "Возникла ошибка при сохранении нового пользователя", ButtonType.OK)
						.show();
				return;
			}
			new CustomAlert("Подверждение", "Данные пользователя " + userToEdit + " обновлены!", "", ButtonType.OK)
					.show();
//			tableView.refresh();
			return;
		}
		// this part of the method works only for users with admin privileges and sets a
		// password for them
		Dialog<String> prompt = new PasswordSetDialog();
		prompt.setHeaderText("Установите пароль");
		CustomAlert alert = new CustomAlert("Ошибка", "", "Неверный ввод!",
				new ButtonType("Повторить", ButtonData.OK_DONE), new ButtonType("Закрыть", ButtonData.CANCEL_CLOSE));
		while (true) {
			Optional<String> newPassword = prompt.showAndWait();
			if (newPassword.isEmpty()) break; 
			if (!newPassword.get().equals("invalid")) {
				String pswHash = BCrypt.hashpw(newPassword.get(), BCrypt.gensalt(4));
				userToEdit.setPassword(pswHash);
				try {
					reprService.updateRepr(userToEdit.getId(), userToEdit);
				} catch (SaveEntityException see) {
					new CustomAlert("Ошибка", "", "Возникла ошибка при сохранении нового пользователя", ButtonType.OK)
							.show();
					return;
				}
				new CustomAlert("Подтверждение", "", "Пароль успешно установлен!", ButtonType.OK).show();
				break;
			} else {
				Optional<ButtonType> retry = alert.showAndWait();
				if (retry.isEmpty() || retry.get().getButtonData() == ButtonData.CANCEL_CLOSE) break;
			}
		}
	}

	@FXML
	private void deleteUser(ActionEvent actionEvent) {
		Representative userToDelete = tableView.getSelectionModel().selectedItemProperty().getValue();
		CustomAlert alert = new CustomAlert("Подтверждение",
				"Вы действительно хотите удалить пользователя " + userToDelete + "?", "",
				new ButtonType("Подтвердить", ButtonData.OK_DONE), new ButtonType("Отменить", ButtonData.CANCEL_CLOSE));
		Optional<ButtonType> choice = alert.showAndWait();
		if (choice.isPresent() && choice.get().getButtonData() == ButtonData.OK_DONE) {
			for (ACase acase : caseService.getCasesByRepr(userToDelete)) {
				try {
					acase.setRepr(null);
					caseService.saveCase(acase);
				} catch (SaveEntityException ignored) {}
			}
			try {
				reprService.deleteRepr(userToDelete.getId());
				new CustomAlert("Подтверждение", "Пользователь " + userToDelete + " удален!", "", ButtonType.OK).show();
			} catch (SaveEntityException ignored) {
				new CustomAlert("Ошибка", "", "Возникла ошибка при удалении пользователя", ButtonType.OK).show();
			} finally {
				reprList.remove(userToDelete);
			}
//			tableView.refresh();
		}
	}

	@FXML
	private void logout(ActionEvent actionEvent) {
		FxControllerAndView<LoginController, Parent> login = fxWeaver.load(LoginController.class);
		Stage stage = login.getController().getStage();
		stage.setMaximized(false);
		stage.setScene(new Scene(login.getView().get()));
	}

	private Node createCheckbox(Boolean value) {
		HBox graphicContainer = new HBox();
		graphicContainer.setAlignment(Pos.CENTER);
		CheckBox checkbox = new CheckBox();
		checkbox.setSelected(value ? true : false);
		checkbox.setDisable(true);
		graphicContainer.getChildren().add(checkbox);
		return graphicContainer;
	}
}
