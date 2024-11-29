package pt.isec.pd.splitwise.sharedLib.database.DAO;

import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;
import pt.isec.pd.splitwise.sharedLib.database.Entity.Expense;
import pt.isec.pd.splitwise.sharedLib.database.Entity.User;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//TODO: javaDoc

/**
 * The type Expense user dao. //TODO: class layer to access database and return user objects
 */
public class ExpenseUserDAO extends DAO {
	/**
	 * Instantiates a new Expense user dao.
	 *
	 * @param dbManager the db manager
	 */
	public ExpenseUserDAO(DataBaseManager dbManager) {
		super(dbManager);
	}

	/**
	 * Create relation int.
	 *
	 * @param expenseId the expense id
	 * @param userId    the user id
	 * @return the int
	 * @throws SQLException the sql exception
	 */
	public int createRelation(int expenseId, int userId) throws SQLException {
		logger.debug("Creating relation between expense {} and user {}", expenseId, userId);

		//language=SQLite
		String query = "INSERT INTO expense_users (expense_id, user_id) VALUES (?, ?) RETURNING id";

		int id = dbManager.executeWriteWithId(query, expenseId, userId);

		logger.debug("Created relation with id {}", id);

		return id;
	}

	/**
	 * Gets all expenses from user.
	 *
	 * @param userId the user id
	 * @return the all expenses from user
	 * @throws SQLException the sql exception
	 */
	public List<Expense> getAllExpensesFromUser(int userId) throws SQLException {
		logger.debug("Getting all expenses from user {}", userId);

		//language=SQLite
		String query = """
		               SELECT expenses.id       AS id,
		                      expenses.amount   AS amount,
		                      expenses.description AS description,
		                      expenses.date     AS date
		               FROM expenses
		                        JOIN expense_users ON expenses.id = expense_users.expense_id
		               WHERE expense_users.user_id = ?""";

		List<Map<String, Object>> results = dbManager.executeRead(query, userId);

		List<Expense> expenses = new ArrayList<>();
		for (Map<String, Object> row : results)
			expenses.add(
					Expense.builder()
							.id((int) row.get("id"))
							.amount((float) row.get("amount"))
							.title((String) row.get("description"))
							.date(LocalDate.ofEpochDay(
									(long) row.get("date"))) //TODO: check this later (long → LocalDate)
							.build()
			);

		logger.debug("Found {} expenses", expenses.size());

		return expenses;
	}

	/**
	 * Gets all expenses from user on group.
	 *
	 * @param userId  the user id
	 * @param groupId the group id
	 * @return the all expenses from user on group
	 * @throws SQLException the sql exception
	 */
	public List<Expense> getAllExpensesFromUserOnGroup(int userId, int groupId) throws SQLException {
		logger.debug("Getting all expenses from user {} on group {}", userId, groupId);

		//TODO: select expenses from user as payer and as associate

		//language=SQLite
		String query = """
		               SELECT expenses.id       AS id,
		                      expenses.amount   AS amount,
		                      expenses.description AS description,
		                      expenses.date     AS date
		               FROM expenses
		                        JOIN expense_users ON expenses.id = expense_users.expense_id
		                        JOIN users ON expense_users.user_id = users.id
		                        JOIN group_users ON users.id = group_users.user_id
		               WHERE group_users.group_id = ? AND users.id = ?""";

		List<Map<String, Object>> results = dbManager.executeRead(query, userId);

		List<Expense> expenses = new ArrayList<>();
		for (Map<String, Object> row : results)
			expenses.add(
					Expense.builder()
							.id((int) row.get("id"))
							.amount((float) row.get("amount"))
							.title((String) row.get("description"))
							.date(LocalDate.ofEpochDay(
									(long) row.get("date"))) //TODO: check this later (long → LocalDate)
							.build()
			);

		logger.debug("Found {} expenses", expenses.size());

		return expenses;
	}

	/**
	 * Gets all users from expense.
	 *
	 * @param expenseId the expense id
	 * @return the all users from expense
	 * @throws SQLException the sql exception
	 */
	public List<User> getAllUsersFromExpense(int expenseId) throws SQLException {
		logger.debug("Getting all users from expense {}", expenseId);

		//language=SQLite
		String query = """
		               SELECT users.id       AS id,
		                      users.username AS username,
		                      users.email    AS email,
		                      users.phone_number AS phone_number
		               FROM users
		                        JOIN expense_users ON users.id = expense_users.user_id
		               WHERE expense_users.expense_id = ?""";

		List<Map<String, Object>> results = dbManager.executeRead(query, expenseId);

		List<User> users = new ArrayList<>();
		for (Map<String, Object> row : results)
			users.add(
					User.builder()
							.id((int) row.get("id"))
							.username((String) row.get("username"))
							.email((String) row.get("email"))
							.phoneNumber((String) row.get("phoneNumber"))
							.build()
			);

		logger.debug("Found {} users", users.size());

		return users;
	}

	/**
	 * Update relation boolean.
	 *
	 * @param expenseId  the expense id
	 * @param userId     the user id
	 * @return the boolean
	 * @throws SQLException the sql exception
	 */
	/*public boolean updateRelation(int expenseId, int userId) throws SQLException {
		logger.debug("Updating relation between expense {} and user {}", expenseId, userId);

		//language=SQLite
		String query = "UPDATE expense_users WHERE expense_id = ? AND user_id = ?";

		return dbManager.executeWrite(query, expenseId, userId) > 0;
	}*/

	/**
	 * Delete relation int.
	 *
	 * @param expenseId the expense id
	 * @param userId    the user id
	 * @return the int
	 * @throws SQLException the sql exception
	 */
	public int deleteRelation(int expenseId, int userId) throws SQLException {
		logger.debug("Deleting relation between expense {} and user {}", expenseId, userId);

		//language=SQLite
		String query = "DELETE FROM expense_users WHERE expense_id = ? AND user_id = ?";

		return dbManager.executeWrite(query, expenseId, userId);
	}

	/**
	 * Delete all relations int.
	 *
	 * @param expenseId the expense id
	 * @return the int
	 * @throws SQLException the sql exception
	 */
	public int deleteAllRelations(int expenseId) throws SQLException {
		logger.debug("Deleting all relations from expense {}", expenseId);

		//language=SQLite
		String query = "DELETE FROM expense_users WHERE expense_id = ?";

		return dbManager.executeWrite(query, expenseId);
	}
}
