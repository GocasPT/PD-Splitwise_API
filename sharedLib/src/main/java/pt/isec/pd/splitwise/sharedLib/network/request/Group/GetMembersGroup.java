package pt.isec.pd.splitwise.sharedLib.network.request.Group;

import pt.isec.pd.splitwise.sharedLib.database.DTO.User.DetailUserDTO;
import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;
import pt.isec.pd.splitwise.sharedLib.network.request.Request;
import pt.isec.pd.splitwise.sharedLib.network.response.ListResponse;
import pt.isec.pd.splitwise.sharedLib.network.response.Response;
import pt.isec.pd.splitwise.sharedLib.network.response.ValueResponse;

import java.util.ArrayList;
import java.util.List;

public record GetMembersGroup(int groupId) implements Request {
	@Override
	public Response execute(DataBaseManager context) {
		logger.debug("Getting members from group {}", groupId);

		List<DetailUserDTO> members = new ArrayList<>();
		try {
			context.getGroupUserDAO().getAllUsersFromGroup(groupId).forEach(
					user -> members.add(new DetailUserDTO(
							user.getId(), user.getUsername(), user.getEmail(), user.getPhoneNumber()
					))
			);
		} catch ( Exception e ) {
			logger.error("GetGroup: {}", e.getMessage());
			return new ValueResponse<>("Failed to get group");
		}

		return new ListResponse<>(members.toArray(DetailUserDTO[]::new));
	}

	@Override
	public String toString() {
		return "GET_MEMBERS_GROUP " + groupId;
	}
}
