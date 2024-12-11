package pt.isec.pd.splitwise.sharedLib.network.request.Expense;

import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;
import pt.isec.pd.splitwise.sharedLib.database.Entity.User;
import pt.isec.pd.splitwise.sharedLib.network.request.Request;
import pt.isec.pd.splitwise.sharedLib.network.response.Response;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record InsertExpense(int groupID, double amount, String description, LocalDate date, String inserterUserEmail,
                            String buyerUserEmail, String[] associateUsersEmail) implements Request {
	@Override
	public Response execute(DataBaseManager context) {
		logger.debug("""
		             Inserting expense on group {}:
		             \tamount: {}
		             \tdescription: '{}'
		             \tdate: {}
		             \tregisterUserEmail: {}
		             \tbuyerEmail: {}
		             \tassociateUsersEmail: {}""",
		             groupID, amount, description, date, inserterUserEmail, buyerUserEmail, associateUsersEmail);

		try {
			User registerUserData = context.getUserDAO().getUserByEmail(inserterUserEmail);
			User buyerData = context.getUserDAO().getUserByEmail(buyerUserEmail);

			List<User> associatedUserData = new ArrayList<>();
			for (String associatedUser : associateUsersEmail)
				associatedUserData.add(
						context.getUserDAO()
								.getUserByEmail(associatedUser)
				);

			context.getExpenseDAO().createExpense(groupID, amount, description, date,
			                                      registerUserData.getId(), buyerData.getId(),
			                                      associatedUserData.stream()
					                                      .mapToInt(User::getId).toArray()
			);
		} catch ( Exception e ) {
			logger.error("InsertExpense: {}", e.getMessage());
			return new Response(false, "Failed to insert expense");
		}

		try {
			context.getRmiObserver().onInsertExpense(inserterUserEmail, amount);
		} catch ( Exception e ) {
			logger.error("[RMI] InsertExpense: {}", e.getMessage());
		}

		return new Response(true);
	}

	@Override
	public String toString() {
		return "INSERT_EXPENSE " + groupID + " " + amount + " '" + description + "' '" + date + "' " + buyerUserEmail + " " + Arrays.toString(
				associateUsersEmail);
	}
}
