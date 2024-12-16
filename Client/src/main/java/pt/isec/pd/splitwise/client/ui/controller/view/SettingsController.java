package pt.isec.pd.splitwise.client.ui.controller.view;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pt.isec.pd.splitwise.client.model.ModelManager;
import pt.isec.pd.splitwise.client.ui.component.Card;
import pt.isec.pd.splitwise.client.ui.component.UsersSearchField;
import pt.isec.pd.splitwise.client.ui.controller.BaseController;
import pt.isec.pd.splitwise.client.ui.manager.ViewManager;
import pt.isec.pd.splitwise.sharedLib.database.DTO.User.DetailUserDTO;
import pt.isec.pd.splitwise.sharedLib.database.DTO.User.PreviewUserDTO;
import pt.isec.pd.splitwise.sharedLib.database.Entity.User;
import pt.isec.pd.splitwise.sharedLib.network.request.Group.DeleteGroup;
import pt.isec.pd.splitwise.sharedLib.network.request.Group.EditGroup;
import pt.isec.pd.splitwise.sharedLib.network.request.Group.ExitGroup;
import pt.isec.pd.splitwise.sharedLib.network.request.Group.GetMembersGroup;
import pt.isec.pd.splitwise.sharedLib.network.request.Invite.InviteUser;
import pt.isec.pd.splitwise.sharedLib.network.request.User.GetUsers;
import pt.isec.pd.splitwise.sharedLib.network.response.ListResponse;
import pt.isec.pd.splitwise.sharedLib.network.response.Response;

import java.io.IOException;

public class SettingsController extends BaseController {
	@FXML public Button btnClose;
	@FXML private Button btnInvite;
	@FXML private Button btnEdit;
	@FXML private Button btnDelete;
	@FXML private Button btnExit;
	@FXML private VBox vbMembers;

	public SettingsController(ViewManager viewManager, ModelManager modelManager) {
		super(viewManager, modelManager);
	}

	@Override
	protected void registerHandlers() {
		btnClose.setOnAction(e -> viewManager.showView("group_view"));
		btnInvite.setOnAction(e -> inviteUserPopup());
		btnEdit.setOnAction(e -> editNamePopup());
		btnDelete.setOnAction(e -> deleteGroupPopup());
		btnExit.setOnAction(e -> exitGroupPopup());
	}

	@Override
	protected void update() {
		fetchMembers();
	}

	private void fetchMembers() {
		viewManager.sendRequestAsync(new GetMembersGroup(modelManager.getGroupInViewId()), this::handleResponse);
	}

	@Override
	protected void handleResponse(Response response) {
		if (!response.isSuccess()) {
			viewManager.showError(response.getErrorDescription());
			return;
		}

		if (response instanceof ListResponse listResponse) {
			if (listResponse.isEmpty()) {
				viewManager.showError("No members found");
				return;
			}

			if (listResponse.getList() instanceof DetailUserDTO[] members) {
				vbMembers.getChildren().clear();

				try {
					for (DetailUserDTO member : members)
						vbMembers.getChildren().add(
								new Card.Builder().id("member-card").title(member.getUsername()).subtitle(
										member.getEmail()).description(member.getPhoneNumber()).addStyleClass(
										"member-card").build());
				} catch ( IOException e ) {
					viewManager.showError("Failed to fetch members: " + e.getMessage());
				}
			} else viewManager.showError("Failed to get members list");
		} else viewManager.showError("Failed to cast response to ListResponse");
	}

	private void inviteUserPopup() {
		Stage popupStage = new Stage();
		popupStage.initModality(Modality.APPLICATION_MODAL);
		popupStage.setTitle("Invite User");

		VBox vbox = new VBox(10);
		vbox.setPadding(new Insets(10));

		ListProperty<User> members = new SimpleListProperty<>();

		viewManager.sendRequestAsync(new GetUsers(), (response -> {
			if (!response.isSuccess()) {
				viewManager.showError("Failed to fetch members: " + response.getErrorDescription());
				return;
			}

			if (response instanceof ListResponse listResponse) {
				if (listResponse.getList() instanceof PreviewUserDTO[] users) {
					for (PreviewUserDTO user : users) {
						members.add(User.builder().email(user.getEmail()).build());
					}
				}
			}
		}));

		UsersSearchField emailField = new UsersSearchField(members);

		Button btnInvite = new Button("Invite");
		Button btnCancel = new Button("Cancel");

		btnInvite.setOnAction(e -> {
			String email = emailField.getText();
			if (!email.isEmpty()) {
				String loggedUserEmail = modelManager.getEmailLoggedUser();
				viewManager.sendRequestAsync(new InviteUser(modelManager.getGroupInViewId(), email, loggedUserEmail),
				                             (response -> {
					                             if (!response.isSuccess()) {
						                             viewManager.showError(response.getErrorDescription());
					                             }
				                             }));

				popupStage.close();
			}
		});

		btnCancel.setOnAction(e -> popupStage.close());

		HBox hbox = new HBox(10, btnInvite, btnCancel);
		hbox.setAlignment(Pos.CENTER);

		vbox.getChildren().addAll(new Label("Email:"), emailField, hbox);

		Scene scene = new Scene(vbox);
		popupStage.setScene(scene);
		popupStage.showAndWait();
	}

	private void editNamePopup() {
		Dialog<String> dialog = new Dialog<>();
		dialog.setTitle("Edit Group Name");
		dialog.setHeaderText("Enter the new group name");
		dialog.setContentText("Name:");
		dialog.showAndWait().ifPresent(newName -> {
			viewManager.sendRequestAsync(new EditGroup(modelManager.getGroupInViewId(), newName), (response -> {
				if (!response.isSuccess()) {
					viewManager.showError(response.getErrorDescription());
					return;
				}

				viewManager.showView("group_view");
			}));
		});
	}

	private void deleteGroupPopup() {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Delete Group");
		alert.setHeaderText("Are you sure you want to delete this group?");
		alert.setContentText("This action cannot be undone.");
		alert.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				viewManager.sendRequestAsync(new DeleteGroup(modelManager.getGroupInViewId()), (response1 -> {
					if (!response1.isSuccess()) {
						viewManager.showError(response1.getErrorDescription());
						return;
					}

					viewManager.showView("home_view");
				}));
			}
		});
	}

	private void exitGroupPopup() {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Exit Group");
		alert.setHeaderText("Are you sure you want to exit this group?");
		alert.setContentText("This action cannot be undone.");
		alert.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				viewManager.sendRequestAsync(
						new ExitGroup(modelManager.getEmailLoggedUser(), modelManager.getGroupInViewId()),
						(response1 -> {
							if (!response1.isSuccess()) {
								viewManager.showError(response1.getErrorDescription());
								return;
							}

							viewManager.showView("home_view");
						}));
			}
		});
	}
}
