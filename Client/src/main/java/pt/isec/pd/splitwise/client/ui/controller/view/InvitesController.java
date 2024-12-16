package pt.isec.pd.splitwise.client.ui.controller.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import pt.isec.pd.splitwise.client.model.ModelManager;
import pt.isec.pd.splitwise.client.ui.component.Card;
import pt.isec.pd.splitwise.client.ui.controller.BaseController;
import pt.isec.pd.splitwise.client.ui.manager.ViewManager;
import pt.isec.pd.splitwise.sharedLib.database.DTO.Invite.PreviewInviteDTO;
import pt.isec.pd.splitwise.sharedLib.network.request.Invite.GetInvites;
import pt.isec.pd.splitwise.sharedLib.network.request.Invite.InviteResponse;
import pt.isec.pd.splitwise.sharedLib.network.response.ListResponse;
import pt.isec.pd.splitwise.sharedLib.network.response.Response;

import java.io.IOException;

public class InvitesController extends BaseController {
	@FXML private VBox vbInvites;

	public InvitesController(ViewManager viewManager, ModelManager modelManager) {
		super(viewManager, modelManager);
	}

	@Override
	protected void registerHandlers() {
		//super.registerHandlers();
	}

	@Override
	protected void update() {
		fetchInvites();
	}

	@Override
	protected void handleResponse(Response response) {
		if (!response.isSuccess()) {
			viewManager.showError(response.getErrorDescription());
			return; //TODO: handle error
		}

		vbInvites.getChildren().clear();
		ListResponse<PreviewInviteDTO> listResponse = (ListResponse<PreviewInviteDTO>) response; //TODO: check this warning
		PreviewInviteDTO[] invites = listResponse.getList();
		try {
			for (PreviewInviteDTO invite : invites) {
				Button btnAccept = new Button("Accept");
				Button btnDeny = new Button("Deny");

				Card inviteCard = new Card.Builder().id("invite-card").title(invite.getGroupName()).subtitle(
								invite.getHostEmail()) //TODO: email -> email + username
						.addButton(btnAccept).addButton(btnDeny).addStyleClass("invite-card").build();

				btnAccept.setOnAction(e -> {
					viewManager.sendRequestAsync(new InviteResponse(invite.getId(), true), (resp) -> {
						if (!resp.isSuccess()) {
							viewManager.showError(resp.getErrorDescription());
							return;
						}
						update();
					});
				});

				btnDeny.setOnAction(e -> {
					viewManager.sendRequestAsync(new InviteResponse(invite.getId(), false), (resp) -> {
						if (!resp.isSuccess()) {
							viewManager.showError(resp.getErrorDescription());
							return;
						}
						update();
					});
				});

				vbInvites.getChildren().add(inviteCard);
			}
		} catch ( IOException e ) {
			viewManager.showError("Failed to build invites: " + e.getMessage());
		}
	}

	private void fetchInvites() {
		viewManager.sendRequestAsync(new GetInvites(modelManager.getEmailLoggedUser()), this::handleResponse);
	}
}
