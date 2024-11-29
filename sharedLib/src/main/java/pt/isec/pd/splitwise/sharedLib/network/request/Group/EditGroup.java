package pt.isec.pd.splitwise.sharedLib.network.request.Group;

import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;
import pt.isec.pd.splitwise.sharedLib.network.request.Request;
import pt.isec.pd.splitwise.sharedLib.network.response.Response;

public record EditGroup(int groupId, String newGroupName) implements Request {
	@Override
	public Response execute(DataBaseManager context) {
		logger.debug("""
		             EditGroup group {}
		             \tname: {}""",
		             groupId, newGroupName);

		try {
			context.getGroupDAO().editGroup(groupId, newGroupName);
		} catch ( Exception e ) {
			logger.error("EditGroup: {}", e.getMessage());
			return new Response(false, "Failed to edit group");
		}

		return new Response(true);
	}

	@Override
	public String toString() {
		return "EDIT_GROUP " + groupId + " " + newGroupName;
	}
}
