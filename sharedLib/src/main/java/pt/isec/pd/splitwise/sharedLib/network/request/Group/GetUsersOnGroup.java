package pt.isec.pd.splitwise.sharedLib.network.request.Group;

import pt.isec.pd.splitwise.sharedLib.database.DTO.User.PreviewUserDTO;
import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;
import pt.isec.pd.splitwise.sharedLib.database.Entity.Group;
import pt.isec.pd.splitwise.sharedLib.network.request.Request;
import pt.isec.pd.splitwise.sharedLib.network.response.Response;
import pt.isec.pd.splitwise.sharedLib.network.response.ValueResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record GetUsersOnGroup(int groupId) implements Request {
	@Override
	public Response execute(DataBaseManager context) {
		logger.debug("Getting users on group {}", groupId);

		//TODO: improve this part
		Map<String, Object> data = new HashMap<>(); //TODO: Pair<string, list>
		try {
			Group groupData = context.getGroupDAO().getGroupById(groupId);
			data.put("group_name", groupData.getName());

			List<PreviewUserDTO> members = new ArrayList<>();
			context.getGroupUserDAO().getAllUsersFromGroup(groupId).forEach(user -> {
				members.add(new PreviewUserDTO(
						user.getId(),
						user.getUsername(),
						user.getEmail()
				));
			});
			data.put("members", members);
		} catch ( Exception e ) {
			logger.error("GetUsersOnGroup: {}", e.getMessage());
			return new Response(false, "Failed to get users on group");
		}

		return new ValueResponse<>(data);
	}

	@Override
	public String toString() {
		return "GET_USER_ON_GROUP " + groupId;
	}
}
