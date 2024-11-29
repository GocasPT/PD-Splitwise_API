package pt.isec.pd.splitwise.sharedLib.network.request.Expense;

import pt.isec.pd.splitwise.sharedLib.database.DTO.Expense.DetailExpenseDTO;
import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;
import pt.isec.pd.splitwise.sharedLib.database.Entity.Expense;
import pt.isec.pd.splitwise.sharedLib.database.Entity.User;
import pt.isec.pd.splitwise.sharedLib.network.request.Request;
import pt.isec.pd.splitwise.sharedLib.network.response.Response;
import pt.isec.pd.splitwise.sharedLib.network.response.ValueResponse;

public record GetExpense(int expenseId) implements Request {
	@Override public Response execute(DataBaseManager context) {
		logger.debug("Getting expense with id {}", expenseId);

		try {
			Expense expense = context.getExpenseDAO().getExpenseById(expenseId);

			logger.debug("Got expense with id {}", expenseId);

			if ( expense == null ) {
				logger.error("Expense with id {} not found", expenseId);
				return new ValueResponse<>("Expense with id " + expenseId + " not found");
			}

			return new ValueResponse<>(
					DetailExpenseDTO.builder()
							.id(expense.getId())
							.amount(expense.getAmount())
							.title(expense.getTitle())
							.date(expense.getDate())
							.registeredBy(expense.getRegisterByUser())
							.payerUser(expense.getPayerUser())
							.associatedUsersList(
									context.getExpenseUserDAO()
											.getAllUsersFromExpense(expense.getId()).stream()
											.map(User::getEmail)
											.toList()
							)
							.build()
			);
		} catch ( Exception ex ) {
			logger.error("Failed to get expense with id {}: {}", expenseId, ex.getMessage());
			return new ValueResponse<>("Failed to get expense with id " + expenseId + ": " + ex.getMessage());
		}
	}

	@Override public String toString() {
		return "GET_EXPENSE " + expenseId;
	}
}
