package pt.isec.pd.splitwise.sharedLib.database.DAO;

import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;
import pt.isec.pd.splitwise.sharedLib.database.Entity.Expense;
import pt.isec.pd.splitwise.sharedLib.database.Entity.User;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//TODO: javaDoc

/**
 * The type Expense dao. //TODO: class layer to access database and return expense objects
 */
public class ExpenseDAO extends DAO {
	/**
	 * Instantiates a new Expense dao.
	 *
	 * @param dbManager the db manager
	 */
	public ExpenseDAO(DataBaseManager dbManager) {
		super(dbManager);
	}

	/**
	 * Create expense int.
	 *
	 * @param groupId         the group id
	 * @param amount          the amount
	 * @param description     the description
	 * @param date            the date
	 * @param userPayerId     the user payer id
	 * @param usersInvolvedId the users involved id
	 * @return the int
	 * @throws SQLException the sql exception
	 */
	public int createExpense(int groupId, double amount, String description, LocalDate date, int userInserterId, int userPayerId, int[] usersInvolvedId) throws SQLException {
		logger.debug("Creating expense for group {} with amount {} by user {}", groupId, amount, userPayerId);

		//language=SQLite
		String queryInsert = "INSERT INTO expenses (group_id, amount, description, date, paid_by_user_id, inserted_by_user_id) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";

		int id = dbManager.executeWriteWithId(queryInsert, groupId, amount, description, date, userPayerId,
		                                      userInserterId);
		for (int userId : usersInvolvedId)
			dbManager.getExpenseUserDAO().createRelation(id, userId);

		return id;
	}

	/**
	 * Gets expense by id.
	 *
	 * @param expenseId the expense id
	 * @return the expense by id
	 * @throws SQLException the sql exception
	 */
	public Expense getExpenseById(int expenseId) throws SQLException {
		logger.debug("Getting expense with id {}", expenseId);

		//language=SQLite
		String query = """
		               SELECT
		                   expenses.id AS expense_id,
		                   expenses.amount AS expense_amount,
		                   expenses.description AS expense_description,
		                   expenses.date AS expense_date,
		                   payer.email AS payer_email
		               FROM expenses
		                   JOIN users AS payer ON expenses.paid_by_user_id = payer.id
		               WHERE expenses.id = ?
		               """;

		Map<String, Object> result = dbManager.executeRead(query, expenseId).getFirst();
		return Expense.builder()
				.id((int) result.get("expense_id"))
				.amount((double) result.get("expense_amount"))
				.title((String) result.get("expense_description"))
				.date(LocalDate.parse(
						(String) result.get("expense_date"),
						DateTimeFormatter.ISO_DATE
				))
				.payerUser((String) result.get("payer_email"))
				.build();
	}

	/**
	 * Gets all expenses from group.
	 *
	 * @param groupId the group id
	 * @return the all expenses from group
	 * @throws SQLException the sql exception
	 */
	public List<Expense> getAllExpensesFromGroup(int groupId) throws SQLException {
		logger.debug("Getting all expenses for group {}", groupId);

		//language=SQLite
		String query = """
		               SELECT
		                   expenses.id AS expense_id,
		                   expenses.amount AS expense_amount,
		                   expenses.description AS expense_description,
		                   expenses.date AS expense_date,
		                   payer.email AS payer_email
		               FROM expenses
		                   JOIN groups ON expenses.group_id = groups.id
		                   JOIN users AS payer ON expenses.paid_by_user_id = payer.id
		               WHERE group_id = ?
		               ORDER BY expense_date DESC
		               """;

		List<Map<String, Object>> results = dbManager.executeRead(query, groupId);
		List<Expense> expenses = new ArrayList<>();

		for (Map<String, Object> row : results) {
			expenses.add(
					Expense.builder()
							.id((int) row.get("expense_id"))
							.amount((double) row.get("expense_amount"))
							.title((String) row.get("expense_description"))
							.date(LocalDate.parse(
									(String) row.get("expense_date"),
									DateTimeFormatter.ISO_DATE
							))
							.payerUser((String) row.get("payer_email"))
							.associetedUsersList(
									dbManager.getExpenseUserDAO()
											.getAllUsersFromExpense((int) row.get("expense_id")).stream()
											.map(User::getEmail)
											.toList()
							)
							.build()
			);
		}

		return expenses;
	}

	/**
	 * Update expense.
	 *
	 * @param expenseId       the expense id
	 * @param amount          the amount
	 * @param description     the description
	 * @param date            the date
	 * @param userPayerId     the user payer id
	 * @param usersInvolvedId the users involved id
	 * @throws SQLException the sql exception
	 */
//TODO: rollback system (?)
	public void updateExpense(int expenseId, double amount, String description, LocalDate date, int userPayerId, int[] usersInvolvedId) throws SQLException {
		//TODO: updated expense = update expense + update debts with users (update percentage, remove user, add user)
		logger.debug("Updating expense with id {}\n\tamount: {}\n\tdescription: {}\n\tdate: {}\n\tpaid by: {}",
		             expenseId,
		             amount, description, date, userPayerId);

		//language=SQLite
		String queryUpdate = "UPDATE expenses SET amount = ?, description = ?, date = ?, paid_by_user_id = ? WHERE id = ?";

		dbManager.executeWrite(queryUpdate, amount, description, date, userPayerId, expenseId);
		//dbManager.executeWrite(queryDeleteInvolved, id);
		//TODO: update expense = update expense + update debts with users (update percentage, remove user, add user)
		for (int userId : usersInvolvedId) {
			//TODO: query to get all relations, remove old ones, add new ones(if need)
		}
	}

	/**
	 * Delete expense boolean.
	 *
	 * @param expenseId the expense id
	 * @return the boolean
	 * @throws SQLException the sql exception
	 */
	public boolean deleteExpense(int expenseId) throws SQLException {
		logger.debug("Deleting expense with id: {}", expenseId);

		//TODO: deleted expense = delete expense + delete debts with users
		//language=SQLite
		String query = "DELETE FROM expenses WHERE id = ?";

		dbManager.executeWrite(query, expenseId);
		return true;
	}
}
