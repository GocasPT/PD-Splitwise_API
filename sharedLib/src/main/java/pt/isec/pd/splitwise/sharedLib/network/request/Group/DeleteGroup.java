package pt.isec.pd.splitwise.sharedLib.network.request.Group;

import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;
import pt.isec.pd.splitwise.sharedLib.network.request.Request;
import pt.isec.pd.splitwise.sharedLib.network.response.Response;

public record DeleteGroup(int groupId) implements Request {
	@Override
	public Response execute(DataBaseManager context) {
		logger.debug("Deleting group {}", groupId);

		try {
			context.getGroupDAO().deleteGroup(groupId);
		} catch ( Exception e ) {
			logger.error("DeleteGroup: {}", e.getMessage());
			return new Response(false, "Failed to delete group");
		}

		return new Response(true);
	}

	@Override
	public String toString() {
		return "DELETE_GROUP " + groupId;
	}
}
