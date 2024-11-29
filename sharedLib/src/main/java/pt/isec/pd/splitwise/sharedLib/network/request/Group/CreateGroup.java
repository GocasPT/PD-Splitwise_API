package pt.isec.pd.splitwise.sharedLib.network.request.Group;

import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;
import pt.isec.pd.splitwise.sharedLib.database.Entity.User;
import pt.isec.pd.splitwise.sharedLib.network.request.Request;
import pt.isec.pd.splitwise.sharedLib.network.response.Response;

public record CreateGroup(String groupName, String userEmail) implements Request {
	@Override
	public Response execute(DataBaseManager context) {
		logger.debug("""
		             Creating group:
		             \tname: {}
		             \tuser: {}""",
		             groupName, userEmail);

		try {
			User userData = context.getUserDAO().getUserByEmail(userEmail);
			context.getGroupDAO().createGroup(groupName, userData.getId());
		} catch ( Exception e ) {
			logger.error("CreateGroup: {}", e.getMessage());
			return new Response(false, "Failed to create group");
		}

		return new Response(true);
	}

	@Override
	public String toString() {
		return "CREATE_GROUP '" + groupName + "' " + userEmail;
	}
}
