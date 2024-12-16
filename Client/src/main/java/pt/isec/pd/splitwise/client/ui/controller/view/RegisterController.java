package pt.isec.pd.splitwise.client.ui.controller.view;

import com.dlsc.phonenumberfx.PhoneNumberField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import pt.isec.pd.splitwise.client.model.ModelManager;
import pt.isec.pd.splitwise.client.ui.controller.BaseController;
import pt.isec.pd.splitwise.client.ui.manager.ViewManager;
import pt.isec.pd.splitwise.sharedLib.network.request.User.Register;
import pt.isec.pd.splitwise.sharedLib.network.response.Response;

public class RegisterController extends BaseController {
	@FXML private TextField tfUsername;
	@FXML private PhoneNumberField tfPhoneNumber;
	@FXML private TextField tfEmail;
	@FXML private PasswordField tfPassword;
	@FXML private Button btnRegiste;
	@FXML private Hyperlink hpSignIn;

	public RegisterController(ViewManager viewManager, ModelManager modelManager) {
		super(viewManager, modelManager);
	}

	@Override
	protected void registerHandlers() {
		btnRegiste.setOnAction(e -> {
			try {
				handleRegister();
			} catch ( Exception ex ) {
				viewManager.showError("Register failed: " + ex.getMessage());
			}
		});
		hpSignIn.setOnAction(e -> {
			try {
				viewManager.showView("login_view");
			} catch ( Exception ex ) {
				viewManager.showError("Failed to go to login: " + ex.getMessage());
			}
		});
	}

	@Override
	protected void update() {
	}

	@Override
	protected void handleResponse(Response response) {
		if (response.isSuccess()) viewManager.showView("login_view");
		else {
			viewManager.showError(response.getErrorDescription());
			new Alert(Alert.AlertType.ERROR, response.getErrorDescription()).showAndWait();
			Platform.exit();
		}
	}

	private void handleRegister() {
		String username = tfUsername.getText();
		String phoneNumber = tfPhoneNumber.getText();
		String email = tfEmail.getText();
		String password = tfPassword.getText();

		//TODO: ValidatorFX
		if (username.isEmpty() || phoneNumber.isEmpty() || email.isEmpty() || password.isEmpty()) {
			viewManager.showError("All fields are required");
			return;
		}

		viewManager.sendRequestAsync(new Register(username, email, phoneNumber, password), this::handleResponse);
	}
}
