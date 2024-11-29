package pt.isec.pd.splitwise.sharedLib.network.request.User;

import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;
import pt.isec.pd.splitwise.sharedLib.database.Entity.User;
import pt.isec.pd.splitwise.sharedLib.network.request.Request;
import pt.isec.pd.splitwise.sharedLib.network.response.Response;

public record Login(String email, String password) implements Request {
	@Override
	public Response execute(DataBaseManager context) {
		logger.debug("user '{}' login", email);

		try {
			User userData = context.getUserDAO().getUserByEmail(email);
			if (userData == null || !userData.getPassword().equals(password))
				return new Response(false, "Invalid userEmail or password");
		} catch ( Exception e ) {
			logger.error("Login: {}", e.getMessage());
			return new Response(false, "Error on login");
		}

		return new Response(true);
	}

	@Override
	public String toString() {
		return "LOGIN " + email + " " + password;
	}
}