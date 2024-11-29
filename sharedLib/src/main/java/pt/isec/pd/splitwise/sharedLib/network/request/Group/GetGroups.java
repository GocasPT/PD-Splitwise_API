package pt.isec.pd.splitwise.sharedLib.network.request.Group;

import pt.isec.pd.splitwise.sharedLib.database.DTO.Group.PreviewGroupDTO;
import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;
import pt.isec.pd.splitwise.sharedLib.network.request.Request;
import pt.isec.pd.splitwise.sharedLib.network.response.ListResponse;
import pt.isec.pd.splitwise.sharedLib.network.response.Response;

import java.util.ArrayList;
import java.util.List;

public record GetGroups(String userEmail) implements Request {
	@Override
	public Response execute(DataBaseManager context) {
		logger.debug("Getting groups from user '{}'", userEmail);

		List<PreviewGroupDTO> groupList = new ArrayList<>();
		try {
			context.getGroupUserDAO().getAllGroupsFromUser(
					context.getUserDAO().getUserByEmail(userEmail).getId()
			).forEach(
					group -> groupList.add(new PreviewGroupDTO(group.getId(), group.getName(), group.getNumUsers()))
			);
		} catch ( Exception e ) {
			logger.error("GetGroups: {}", e.getMessage());
			return new ListResponse<>("Failed to get groups");
		}

		return new ListResponse<>(groupList.toArray(PreviewGroupDTO[]::new));
	}

	@Override
	public String toString() {
		return "GET_GROUPS " + userEmail;
	}
}
