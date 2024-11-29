package pt.isec.pd.splitwise.sharedLib.network.request.Expense;

import pt.isec.pd.splitwise.sharedLib.database.DTO.Expense.DetailExpenseDTO;
import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;
import pt.isec.pd.splitwise.sharedLib.network.request.Request;
import pt.isec.pd.splitwise.sharedLib.network.response.ListResponse;
import pt.isec.pd.splitwise.sharedLib.network.response.Response;

import java.util.ArrayList;
import java.util.List;

public record GetAllExpenses(int groupID) implements Request {
	@Override
	public Response execute(DataBaseManager context) {
		logger.debug("Getting history of group {}", groupID);

		List<DetailExpenseDTO> expensesHistory = new ArrayList<>();
		try {
			context.getExpenseDAO().getAllExpensesFromGroup(groupID).forEach(expense -> {
				expensesHistory.add(
						new DetailExpenseDTO(
								expense.getId(),
								expense.getAmount(),
								expense.getTitle(),
								expense.getDate(),
								expense.getRegisterByUser(),
								expense.getPayerUser(),
								expense.getAssocietedUsersList()
						)
				);
			});
		} catch ( Exception e ) {
			logger.error("GetAllExpenses: {}", e.getMessage());
			return new ListResponse<>("Failed to get expense history");
		}

		return new ListResponse<>(expensesHistory.toArray(DetailExpenseDTO[]::new));
	}

	@Override
	public String toString() {
		return "GET_HISTORY " + groupID;
	}
}
