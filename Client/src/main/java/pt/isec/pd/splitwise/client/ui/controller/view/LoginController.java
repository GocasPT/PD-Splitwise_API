package pt.isec.pd.splitwise.client.ui.controller.view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import pt.isec.pd.splitwise.client.model.ENavBarState;
import pt.isec.pd.splitwise.client.model.ModelManager;
import pt.isec.pd.splitwise.client.ui.controller.BaseController;
import pt.isec.pd.splitwise.client.ui.manager.ViewManager;
import pt.isec.pd.splitwise.sharedLib.network.request.User.Login;
import pt.isec.pd.splitwise.sharedLib.network.response.Response;

public class LoginController extends BaseController {
	@FXML private TextField tfEmail;
	@FXML private TextField tfPassword;
	@FXML private Button btnLogin;
	@FXML private Hyperlink hpSignUp;

	public LoginController(ViewManager viewManager, ModelManager modelManager) {
		super(viewManager, modelManager);
	}

	/*private StackPane createIconNode(MaterialDesign mdiKey) {
		FontIcon leftIcon = new FontIcon(mdiKey);
		StackPane leftIconWrapper = new StackPane(leftIcon);
		leftIconWrapper.getStyleClass().add("icon-wrapper");
		return leftIconWrapper;
	}*/

	@Override
	protected void registerHandlers() {
		/*tfPassword = new EnhancedPasswordField();
		((EnhancedPasswordField) tfPassword).setLeft(createIconNode(MaterialDesign.MDI_KEY));
		tfPassword.setStyle("-fx-echo-char: '■';");
		StackPane closeIconWrapper = createIconNode(MaterialDesign.MDI_CLOSE);
		closeIconWrapper.setOnMouseClicked(e -> tfPassword.clear());
		((EnhancedPasswordField) tfPassword).setRight(closeIconWrapper);*/

		btnLogin.setOnAction(e -> {
			try {
				handleLogin();
			} catch ( Exception ex ) {
				viewManager.showError("Login failed: " + ex.getMessage());
			}
		});
		hpSignUp.setOnAction(e -> {
			try {
				viewManager.showView("register_view");
			} catch ( Exception ex ) {
				viewManager.showError("Failed to show sign up view: " + ex.getMessage());
			}
		});
	}

	@Override
	protected void update() {
	}

	@Override
	protected void handleResponse(Response response) {
		if (response.isSuccess()) {
			modelManager.setEmailLoggedUser(tfEmail.getText());

			viewManager.showView("groups_view");
			modelManager.getNavBarStateProperty().setValue(ENavBarState.GROUPS);
		} else {
			viewManager.showError(response.getErrorDescription());
			new Alert(Alert.AlertType.ERROR, response.getErrorDescription()).showAndWait();
			Platform.exit();
		}
	}

	private void handleLogin() {
		String username = tfEmail.getText();
		String password = tfPassword.getText();

		//TODO: add validator (ValidatorFX)
		if (username.isEmpty() || password.isEmpty()) {
			viewManager.showError("Username and password are required");
			return;
		}

		viewManager.sendRequestAsync(new Login(username, password), this::handleResponse);
	}
}
