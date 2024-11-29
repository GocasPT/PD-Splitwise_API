package pt.isec.pd.splitwise.sharedLib.network.request.Invite;

import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;
import pt.isec.pd.splitwise.sharedLib.database.Entity.User;
import pt.isec.pd.splitwise.sharedLib.network.request.Request;
import pt.isec.pd.splitwise.sharedLib.network.response.Response;

import java.util.List;

public record InviteResponse(int inviteId, boolean isAccepted) implements Request {
	@Override
	public Response execute(DataBaseManager context) {
		logger.debug("Invite {} have been {}", inviteId, isAccepted ? "accepted" : "declined");

		try {
			if (isAccepted) {
				List<User> members = context.getGroupUserDAO().getAllUsersFromGroup(
						context.getInviteDAO().getInviteById(inviteId).getGroupId());
				String guestUser = context.getInviteDAO().getInviteById(inviteId).getGuestUserEmail();
				context.getInviteDAO().acceptInvite(inviteId);
				for (User user : members)
					context.triggerNotification(user.getEmail(), guestUser + " has joined the group");
			} else
				context.getInviteDAO().declineInvite(inviteId);
		} catch ( Exception e ) {
			logger.error("InviteResponse: {}", e.getMessage());
			return new Response(false, "Failed to update invite");
		}

		return new Response(true);
	}

	@Override
	public String toString() {
		return "INVITE_RESPONSE " + inviteId + " " + isAccepted;
	}
}
