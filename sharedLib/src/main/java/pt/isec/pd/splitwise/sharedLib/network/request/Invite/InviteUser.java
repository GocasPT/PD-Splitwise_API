package pt.isec.pd.splitwise.sharedLib.network.request.Invite;

import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;
import pt.isec.pd.splitwise.sharedLib.database.Entity.Invite;
import pt.isec.pd.splitwise.sharedLib.database.Entity.User;
import pt.isec.pd.splitwise.sharedLib.network.request.Request;
import pt.isec.pd.splitwise.sharedLib.network.response.Response;

import java.util.List;

public record InviteUser(int groupID, String guestUserEmail, String inviteeUserEmail) implements Request {
	@Override
	public Response execute(DataBaseManager context) {
		logger.debug("User '{}' invited '{}' to group '{}'", guestUserEmail, inviteeUserEmail, groupID);

		try {
			User userGuestData = context.getUserDAO()
					.getUserByEmail(guestUserEmail);

			// Check if user is already invited to group
			List<Invite> inviteList = context.getInviteDAO()
					.getAllInvitesFromUser(userGuestData.getId()).stream()
					.filter(invite -> invite.getGroupId() == groupID).toList();

			if (!inviteList.isEmpty())
				return new Response(false, "User is already invited to group");

			context.getInviteDAO().createInvite(groupID, userGuestData.getId(),
			                                    context.getUserDAO()
					                                    .getUserByEmail(inviteeUserEmail)
					                                    .getId());
			context.triggerNotification(guestUserEmail,
			                            "You have been invited to group '" + context.getGroupDAO()
					                            .getGroupById(groupID)
					                            .getName() + "'");
		} catch ( Exception e ) {
			logger.error("InviteUser: {}", e.getMessage());
			return new Response(false, "Failed to invite user to group");
		}

		return new Response(true);
	}

	@Override
	public String toString() {
		return "INVITE_USER " + groupID + " " + guestUserEmail + " " + inviteeUserEmail;
	}
}
