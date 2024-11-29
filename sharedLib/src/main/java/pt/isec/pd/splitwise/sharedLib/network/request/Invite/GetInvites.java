package pt.isec.pd.splitwise.sharedLib.network.request.Invite;

import pt.isec.pd.splitwise.sharedLib.database.DTO.Invite.PreviewInviteDTO;
import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;
import pt.isec.pd.splitwise.sharedLib.network.request.Request;
import pt.isec.pd.splitwise.sharedLib.network.response.ListResponse;
import pt.isec.pd.splitwise.sharedLib.network.response.Response;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public record GetInvites(String userEmail) implements Request {
	@Override
	public Response execute(DataBaseManager context) {
		logger.debug("Getting invites for user '{}'", userEmail);

		List<PreviewInviteDTO> inviteList = new ArrayList<>();
		try {
			context.getInviteDAO().getAllInvitesFromUser(
					context.getUserDAO().getUserByEmail(userEmail).getId()
			).forEach(
					invite -> {
						try {
							inviteList.add(
									new PreviewInviteDTO(
											invite.getId(),
											context.getGroupDAO().getGroupById(invite.getGroupId()).getName(),
											invite.getGuestUserEmail(),
											context.getUserDAO().getUserByEmail(
													invite.getGuestUserEmail()).getUsername()
									)
							);
						} catch ( SQLException e ) {
							throw new RuntimeException(e);
						}
					}
			);
		} catch ( Exception e ) {
			logger.error("GetInvites: {}", e.getMessage());
			return new ListResponse<>("Failed to get invites");
		}

		return new ListResponse<>(inviteList.toArray(PreviewInviteDTO[]::new));
	}

	@Override
	public String toString() {
		return "GET_INVITATIONS " + userEmail;
	}
}
