package pt.isec.pd.splitwise.sharedLib.network.request.User;

import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;
import pt.isec.pd.splitwise.sharedLib.network.request.Request;
import pt.isec.pd.splitwise.sharedLib.network.response.Response;

public record Logout(String email) implements Request {
	@Override
	public Response execute(DataBaseManager context) {
		logger.debug("User '{}' logout", email);

		return new Response(true);
	}

	@Override
	public String toString() {
		return "LOGOUT " + email;
	}
}
