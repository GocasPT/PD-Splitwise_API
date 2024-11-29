package pt.isec.pd.splitwise.sharedLib.network.request.Expense;

import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;
import pt.isec.pd.splitwise.sharedLib.database.Entity.Expense;
import pt.isec.pd.splitwise.sharedLib.database.Entity.Group;
import pt.isec.pd.splitwise.sharedLib.database.Entity.User;
import pt.isec.pd.splitwise.sharedLib.network.request.Request;
import pt.isec.pd.splitwise.sharedLib.network.response.Response;
import pt.isec.pd.splitwise.sharedLib.network.response.ValueResponse;

import java.io.File;
import java.io.FileWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record ExportCSV(int groupID) implements Request {
	@Override
	public Response execute(DataBaseManager context) {
		logger.debug("Exporting expenses from group {}", groupID);

		File csvFile = new File("expenses.csv");
		try (FileWriter writer = new FileWriter(csvFile)) {
			writer.write("\"Nome do grupo\" \"");
			Group group = context.getGroupDAO().getGroupById(groupID);
			writer.write(group.getName());
			writer.write("\"\n");

			writer.write("\"Elementos\" \"");
			List<User> members = context.getGroupUserDAO().getAllUsersFromGroup(groupID);
			for (int i = 0; i < members.size(); i++) {
				writer.write(members.get(i).getUsername());
				if (i < members.size() - 1) {
					writer.write("\";\"");
				}
			}
			writer.write("\"\n");

			writer.write("\"Data\"; \"Responsável pelo registo da despesa\"; \"Valor\";\"Pago por\";\"A dividir com\"\n");

			List<Expense> expenseList = context.getExpenseDAO().getAllExpensesFromGroup(groupID);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

			for (Expense expense : expenseList) {
				writer.write("\"" + expense.getDate().format(formatter) + "\";");

				User registeredBy = context.getUserDAO().getUserByEmail(expense.getRegisterByUser());
				writer.write("\"" + registeredBy.getUsername() + "\";");

				writer.write("\"" + String.format("%.2f", expense.getAmount()) + "\";");

				User paidBy = context.getUserDAO().getUserByEmail(expense.getPayerUser());
				writer.write("\"" + paidBy.getUsername() + "\";");

				List<User> splitWith = context.getExpenseUserDAO().getAllUsersFromExpense(expense.getId());
				for (int i = 0; i < splitWith.size(); i++) {
					writer.write("\"" + splitWith.get(i).getUsername() + "\"");
					if (i < splitWith.size() - 1) {
						writer.write(";");
					}
				}
				writer.write("\n");
			}

			writer.flush();

			return new ValueResponse<>(csvFile);
		} catch ( Exception e ) {
			logger.error("ExportCSV: {}", e.getMessage());
			return new ValueResponse<>("Fail to export history file");
		}
	}

	@Override
	public String toString() {
		return "EXPORT " + groupID;
	}
}