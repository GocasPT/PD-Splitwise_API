package pt.isec.pd.splitwise.client.ui.controller;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import pt.isec.pd.splitwise.client.model.ENavBarState;
import pt.isec.pd.splitwise.client.model.ModelManager;
import pt.isec.pd.splitwise.client.ui.manager.ViewManager;
import pt.isec.pd.splitwise.sharedLib.network.response.Response;

public class NavBarController extends BaseController {
	private static ObjectProperty<ENavBarState> currentButton;
	@FXML private Button btnGroups;
	@FXML private Button btnInvites;
	@FXML private Button btnUser;

	public NavBarController(ViewManager viewManager, ModelManager modelManager) {
		super(viewManager, modelManager);
		currentButton = new SimpleObjectProperty<>(modelManager.getNavBarState());
	}

	@Override
	protected void registerHandlers() {
		modelManager.getNavBarStateProperty().addListener((obs, oldVal, newVal) -> {
			currentButton.set(newVal);
		});

		btnGroups.setOnAction(e -> handleButtonClick("groups_view", ENavBarState.GROUPS));
		btnInvites.setOnAction(e -> handleButtonClick("invites_view", ENavBarState.INVITES));
		btnUser.setOnAction(e -> handleButtonClick("user_view", ENavBarState.USER));

		currentButton.addListener((obs, oldVal, newVal) -> {
			if (oldVal != newVal) updateButtonStyles();
		});

		updateButtonStyles();
	}

	private void handleButtonClick(String viewName, ENavBarState state) {
		try {
			viewManager.showView(viewName);
			modelManager.setNavBarState(state);
		} catch ( Exception ex ) {
			viewManager.showError("Failed to show " + viewName + ": " + ex.getMessage());
		} finally {
			update();
		}
	}

	private void updateButtonStyles() {
		btnGroups.getStyleClass().remove("active");
		btnInvites.getStyleClass().remove("active");
		btnUser.getStyleClass().remove("active");

		switch (currentButton.get()) {
			case GROUPS -> btnGroups.getStyleClass().add("active");
			case INVITES -> btnInvites.getStyleClass().add("active");
			case USER -> btnUser.getStyleClass().add("active");
		}
	}

	@Override
	protected void update() {
	}

	@Override
	protected void handleResponse(Response response) {

	}
}
