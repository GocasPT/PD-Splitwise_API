package pt.isec.pd.splitwise.sharedLib.database.DAO;

import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;
import pt.isec.pd.splitwise.sharedLib.database.Entity.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//TODO: javaDoc

/**
 * The type User dao. //TODO: class layer to access database and return user objects
 */
public class UserDAO extends DAO {
	/**
	 * Instantiates a new User dao.
	 *
	 * @param dbManager the db manager
	 */
	public UserDAO(DataBaseManager dbManager) {
		super(dbManager);
	}

	/**
	 * Create user int.
	 *
	 * @param username    the username
	 * @param email       the userEmail
	 * @param phoneNumber the phoneNumber number
	 * @param password    the password
	 * @return the int
	 * @throws SQLException the sql exception
	 */
	public int createUser(String username, String email, String phoneNumber, String password) throws SQLException {
		logger.debug("Creating user:\n\tusername={}\n\tuserEmail={},\n\tphoneNumber={},\n\tpassword={}", username,
		             email,
		             phoneNumber, password);

		//language=SQLite
		String query = "INSERT INTO users (username, email, phone_number, password) VALUES (?, ?, ?, ?)";

		return dbManager.executeWrite(query, username, email, phoneNumber, password);
	}

	/**
	 * Gets all users.
	 *
	 * @return the all users
	 * @throws SQLException the sql exception
	 */
	public List<User> getAllUsers() throws SQLException {
		logger.debug("Getting all users");

		//language=SQLite
		String query = "SELECT * FROM users";

		List<Map<String, Object>> results = dbManager.executeRead(query);

		List<User> users = new ArrayList<>();
		for (Map<String, Object> row : results)
			users.add(
					User.builder()
							.id((int) row.get("id"))
							.username((String) row.get("username"))
							.email((String) row.get("email"))
							.phoneNumber((String) row.get("phoneNumber"))
							.password((String) row.get("password"))
							.build()
			);

		return users;
	}

	/**
	 * Gets user by id.
	 *
	 * @param userId the id
	 * @return the user by id
	 * @throws SQLException the sql exception
	 */
	public User getUserById(int userId) throws SQLException {
		logger.debug("Getting user by id: {}", userId);

		//language=SQLite
		String query = "SELECT * FROM users WHERE id = ?";

		List<Map<String, Object>> results = dbManager.executeRead(query, userId);

		if (results.isEmpty())
			return null;

		Map<String, Object> row = results.getFirst();
		return User.builder()
				.id((int) row.get("id"))
				.username((String) row.get("username"))
				.email((String) row.get("userEmail"))
				.phoneNumber((String) row.get("phoneNumber"))
				.password((String) row.get("password"))
				.build();
	}

	/**
	 * Gets user by userEmail.
	 *
	 * @param email the userEmail
	 * @return the user by userEmail
	 * @throws SQLException the sql exception
	 */
	public User getUserByEmail(String email) throws SQLException {
		logger.debug("Getting user by userEmail: {}", email);

		//language=SQLite
		String query = "SELECT * FROM users WHERE email = ?";

		List<Map<String, Object>> results = dbManager.executeRead(query, email);

		if (results.isEmpty())
			return null;

		Map<String, Object> row = results.getFirst();
		return User.builder()
				.id((int) row.get("id"))
				.username((String) row.get("username"))
				.email((String) row.get("email"))
				.phoneNumber((String) row.get("phone_number"))
				.password((String) row.get("password"))
				.build();
	}


	/**
	 * Edit user boolean.
	 *
	 * @param userId      the id
	 * @param username    the username
	 * @param email       the userEmail
	 * @param phoneNumber the phoneNumber number
	 * @param password    the password
	 * @return the boolean
	 * @throws SQLException the sql exception
	 */
	public boolean editUser(int userId, String username, String email, String phoneNumber, String password) throws SQLException {
		logger.debug("Editing user id={}:\n\tusername={}\n\tuserEmail={},\n\tphoneNumber={},\n\tpassword={}", userId,
		             username,
		             email, phoneNumber, password);

		//language=SQLite
		String query = "UPDATE users SET username = ?, email = ?, phone_number = ?, password = ? WHERE id = ?";

		return dbManager.executeWrite(query, username, email, phoneNumber, password, userId) > 0;
	}

	/**
	 * Delete user boolean.
	 *
	 * @param userId the id
	 * @return the boolean
	 * @throws SQLException the sql exception
	 */
	public boolean deleteUser(int userId) throws SQLException {
		logger.debug("Deleting user with id {}", userId);

		//language=SQLite
		String query = "DELETE FROM users WHERE id = ?";

		return dbManager.executeWrite(query, userId) > 0;
	}
}
