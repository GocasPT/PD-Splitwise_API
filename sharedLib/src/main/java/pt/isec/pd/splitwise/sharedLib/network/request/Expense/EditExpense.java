package pt.isec.pd.splitwise.sharedLib.network.request.Expense;

import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;
import pt.isec.pd.splitwise.sharedLib.network.request.Request;
import pt.isec.pd.splitwise.sharedLib.network.response.Response;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;

public record EditExpense(int expenseID, double amount, String description, LocalDate date, String buyerEmail,
                          String[] associatedUsersEmail) implements Request {
	@Override
	public Response execute(DataBaseManager context) {
		logger.debug("""
		             Editing expense {}
		             \tamount: {}
		             \tdescription: '{}'
		             \tdate: {}
		             \tbuyerEmail: {}
		             \tassociatedUsersEmail: {}""",
		             expenseID, amount, description, date, buyerEmail, Arrays.toString(associatedUsersEmail)
		);

		try {
			int buyerId = context.getUserDAO().getUserByEmail(buyerEmail).getId();
			int[] associatedUsersId = Arrays.stream(associatedUsersEmail).mapToInt(
					email -> {
						try {
							return context.getUserDAO().getUserByEmail(email).getId();
						} catch ( SQLException e ) {
							throw new RuntimeException(e);
						}
					}
			).toArray();
			context.getExpenseDAO().updateExpense(expenseID, amount, description, date, buyerId, associatedUsersId);
		} catch ( Exception e ) {
			logger.error("EditExpense: {}", e.getMessage());
			return new Response(false, "Failed to edit expense");
		}

		return new Response(true);
	}

	@Override
	public String toString() {
		return "EDIT_EXPENSE " + expenseID + " " + amount + " '" + description + "' " + date + " " + buyerEmail + " " + Arrays.toString(
				associatedUsersEmail);
	}
}
