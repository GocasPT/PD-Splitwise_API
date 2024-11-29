package pt.isec.pd.splitwise.sharedLib.network.request.Expense;

import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;
import pt.isec.pd.splitwise.sharedLib.network.request.Request;
import pt.isec.pd.splitwise.sharedLib.network.response.Response;

public record DeleteExpense(int expenseID) implements Request {
	@Override
	public Response execute(DataBaseManager context) {
		logger.debug("Deleting expense {}", expenseID);

		try {
			context.getExpenseDAO().deleteExpense(expenseID);
		} catch ( Exception e ) {
			logger.error("DeleteExpense: {}", e.getMessage());
			return new Response(false, "Failed to delete expense");
		}

		return new Response(true);
	}

	@Override
	public String toString() {
		return "DELETE_EXPENSE " + expenseID;
	}
}
