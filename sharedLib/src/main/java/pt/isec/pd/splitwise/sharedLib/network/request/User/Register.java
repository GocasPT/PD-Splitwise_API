package pt.isec.pd.splitwise.sharedLib.network.request.User;

import org.sqlite.SQLiteErrorCode;
import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;
import pt.isec.pd.splitwise.sharedLib.network.request.Request;
import pt.isec.pd.splitwise.sharedLib.network.response.Response;

import java.rmi.RemoteException;
import java.sql.SQLException;

public record Register(String username, String email, String phoneNumber, String password) implements Request {
	@Override
	public Response execute(DataBaseManager context) {
		logger.debug("""
		             Register:
		             \tusername: {}
		             \temail: {}
		             \tphone: {}""",
		             username, email, phoneNumber);

		try {
			context.getUserDAO().createUser(username, email, phoneNumber, password);
		} catch ( SQLException e ) {
			//if (e.getErrorCode() == SQLiteErrorCode.SQLITE_CONSTRAINT.code
			if (e.getErrorCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE.code
			    && e.getMessage().toLowerCase().contains("unique")
			)
				return new Response(false, "Email already in use");
		} catch ( Exception e ) {
			logger.error("Register: {}", e.getMessage());
			return new Response(false, "Error registering user");
		}

		try {
			context.getRmiObserver().onRegiste(email);
		} catch ( RemoteException e ) {
			logger.error("[RMI] Register: {}", e.getMessage());
		}

		return new Response(true);
	}

	@Override
	public String toString() {
		return "REGISTER " + username + " " + email + " " + phoneNumber;
	}
}
