package pt.isec.pd.splitwise.sharedLib.network.request.User;

import pt.isec.pd.splitwise.sharedLib.database.DTO.User.DetailUserDTO;
import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;
import pt.isec.pd.splitwise.sharedLib.database.Entity.User;
import pt.isec.pd.splitwise.sharedLib.network.request.Request;
import pt.isec.pd.splitwise.sharedLib.network.response.Response;
import pt.isec.pd.splitwise.sharedLib.network.response.ValueResponse;

public record GetUser(String email) implements Request {
	@Override
	public Response execute(DataBaseManager context) {
		logger.debug("Getting user '{}' info", email);

		DetailUserDTO user;
		try {
			User userData = context.getUserDAO().getUserByEmail(email);
			user = new DetailUserDTO(
					userData.getId(),
					userData.getUsername(),
					userData.getEmail(),
					userData.getPhoneNumber()
			);
		} catch ( Exception e ) {
			logger.error("GetUser: {}", e.getMessage());
			return new ValueResponse<>("Error getting user");
		}

		return new ValueResponse<>(user);
	}

	@Override
	public String toString() {
		return "GET_USER " + email;
	}
}
