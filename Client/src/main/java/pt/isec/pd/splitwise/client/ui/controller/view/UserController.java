package pt.isec.pd.splitwise.client.ui.controller.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import pt.isec.pd.splitwise.client.model.ModelManager;
import pt.isec.pd.splitwise.client.ui.component.dialog.EditUserDialog;
import pt.isec.pd.splitwise.client.ui.controller.BaseController;
import pt.isec.pd.splitwise.client.ui.manager.ViewManager;
import pt.isec.pd.splitwise.sharedLib.database.DTO.User.DetailUserDTO;
import pt.isec.pd.splitwise.sharedLib.network.request.User.EditUser;
import pt.isec.pd.splitwise.sharedLib.network.request.User.GetUser;
import pt.isec.pd.splitwise.sharedLib.network.request.User.Logout;
import pt.isec.pd.splitwise.sharedLib.network.response.Response;
import pt.isec.pd.splitwise.sharedLib.network.response.ValueResponse;

import java.io.IOException;

public class UserController extends BaseController {
	@FXML private BorderPane homePane;
	@FXML private Button btnLogout;
	@FXML private Text txtEmail;
	@FXML private Text txtPhoneNumber;
	@FXML private Text txtUsername;
	@FXML private Button btnEdit;

	public UserController(ViewManager viewManager, ModelManager modelManager) {
		super(viewManager, modelManager);
	}

	@Override
	protected void registerHandlers() {
		btnEdit.setOnAction(e -> editPopup());
		btnLogout.setOnAction(e -> logoutPopup());
	}

	@Override
	protected void update() {
		viewManager.sendRequestAsync(new GetUser(modelManager.getEmailLoggedUser()), this::handleResponse);
	}

	@Override
	protected void handleResponse(Response response) {
		if (!response.isSuccess()) {
			viewManager.showError(response.getErrorDescription());
			return;
		}

		if (response instanceof ValueResponse valueResponse) {
			if (valueResponse.getValue() instanceof DetailUserDTO user) {
				txtUsername.setText(user.getUsername());
				txtEmail.setText(user.getEmail());
				txtPhoneNumber.setText(user.getPhoneNumber());
			} else {
				viewManager.showError("Failed to get user data");
			}
		} else {
			viewManager.showError("Failed to cast response to ValueResponse");
		}
	}

	private void editPopup() {
		try {
			EditUserDialog dialog = new EditUserDialog(homePane.getScene().getWindow());
			dialog.showAndWait().ifPresent(infoUserDTO -> {
				viewManager.sendRequestAsync(
						new EditUser(infoUserDTO.getUsername(), infoUserDTO.getEmail(), infoUserDTO.getEmail(),
						             infoUserDTO.getPassword()), (response) -> {
							if (!response.isSuccess()) {
								viewManager.showError(response.getErrorDescription());
								return; //TODO: handle error
							}

							update();
						});
			});
		} catch ( IOException e ) {
			viewManager.showError("Error loading edit user popup");
		}
	}

	private void logoutPopup() {
		viewManager.sendRequestAsync(new Logout(modelManager.getEmailLoggedUser()), (response -> {
			if (!response.isSuccess()) {
				viewManager.showError(response.getErrorDescription());
				return;
			}

			modelManager.setEmailLoggedUser(null);
			viewManager.showView("login_view");
		}));
	}
}
