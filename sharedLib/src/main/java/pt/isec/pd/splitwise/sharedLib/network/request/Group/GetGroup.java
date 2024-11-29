package pt.isec.pd.splitwise.sharedLib.network.request.Group;

import pt.isec.pd.splitwise.sharedLib.database.DTO.Group.PreviewGroupDTO;
import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;
import pt.isec.pd.splitwise.sharedLib.database.Entity.Group;
import pt.isec.pd.splitwise.sharedLib.network.request.Request;
import pt.isec.pd.splitwise.sharedLib.network.response.Response;
import pt.isec.pd.splitwise.sharedLib.network.response.ValueResponse;

public record GetGroup(int groupId) implements Request {
	@Override
	public Response execute(DataBaseManager context) {
		logger.debug("Getting group {} info", groupId);

		PreviewGroupDTO group;
		try {
			Group groupData = context.getGroupDAO().getGroupById(groupId);
			group = PreviewGroupDTO.builder()
					.id(groupData.getId())
					.name(groupData.getName())
					.build();
		} catch ( Exception e ) {
			logger.error("GetGroup: {}", e.getMessage());
			return new ValueResponse<>("Failed to get group");
		}

		return new ValueResponse<>(group);
	}

	@Override
	public String toString() {
		return "GET_GROUP " + groupId;
	}
}
