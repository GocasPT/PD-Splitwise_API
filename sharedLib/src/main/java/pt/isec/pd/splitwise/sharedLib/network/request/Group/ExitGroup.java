package pt.isec.pd.splitwise.sharedLib.network.request.Group;

import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;
import pt.isec.pd.splitwise.sharedLib.database.Entity.Expense;
import pt.isec.pd.splitwise.sharedLib.database.Entity.User;
import pt.isec.pd.splitwise.sharedLib.network.request.Request;
import pt.isec.pd.splitwise.sharedLib.network.response.Response;

import java.util.List;

public record ExitGroup(String userEmail, int groupId) implements Request {
	@Override
	public Response execute(DataBaseManager context) {
		logger.debug("User '{}' exit from group {}", userEmail, groupId);

		try {
			//TODO:
			// - get all expenses that user is associated with
			//  - payed
			//  - associated list
			// - check if list is empty (true → can exit group)

			User user = context.getUserDAO().getUserByEmail(userEmail);

			List<Expense> expenseList = context.getExpenseUserDAO().getAllExpensesFromUserOnGroup(user.getId(),
			                                                                                      groupId);
			if (!expenseList.isEmpty())
				return new Response(false, "User has debts");

			context.getGroupUserDAO().deleteAllRelactions(groupId, user.getId());
		} catch ( Exception e ) {
			logger.error("ExitGroup: {}", e.getMessage());
			return new Response(false, "Failed to exit group");
		}

		return new Response(true);
	}

	@Override
	public String toString() {
		return "EXIT_GROUP " + groupId;
	}
}
