package pt.isec.pd.splitwise.sharedLib.network.request.User;

import pt.isec.pd.splitwise.sharedLib.database.DTO.User.PreviewUserDTO;
import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;
import pt.isec.pd.splitwise.sharedLib.network.request.Request;
import pt.isec.pd.splitwise.sharedLib.network.response.ListResponse;
import pt.isec.pd.splitwise.sharedLib.network.response.Response;
import pt.isec.pd.splitwise.sharedLib.network.response.ValueResponse;

import java.util.ArrayList;
import java.util.List;

public record GetUsers() implements Request {
	@Override
	public Response execute(DataBaseManager context) {
		logger.debug("Getting all users");

		List<PreviewUserDTO> users = new ArrayList<>();
		try {
			context.getUserDAO().getAllUsers().forEach(
					user -> users.add(new PreviewUserDTO(
							user.getId(), user.getUsername(), user.getEmail()
					))
			);
		} catch ( Exception e ) {
			logger.error("GetGroup: {}", e.getMessage());
			return new ValueResponse<>("Failed to get group");
		}

		return new ListResponse<>(users.toArray(PreviewUserDTO[]::new));
	}

	@Override
	public String toString() {
		return "GET_USERS";
	}
}
