package pt.isec.pd.splitwise.sharedLib.database;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.isec.pd.splitwise.sharedLib.database.DAO.*;
import pt.isec.pd.splitwise.sharedLib.database.Observer.DatabaseChangeObserver;
import pt.isec.pd.splitwise.sharedLib.database.Observer.NotificationObserver;
import pt.isec.pd.splitwise.sharedLib.database.Observer.RMIObserver;
import pt.isec.pd.splitwise.sharedLib.network.response.NotificaionResponse;

import java.io.File;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataBaseManager {
	private static final Logger logger = LoggerFactory.getLogger(DataBaseManager.class);
	private final String dbPath;
	private final Connection conn;
	private final NotificationObserver notificationObserver;
	@Getter private final DatabaseSyncManager syncManager;
	@Getter private final UserDAO userDAO;
	@Getter private final GroupDAO groupDAO;
	@Getter private final GroupUserDAO groupUserDAO;
	@Getter private final InviteDAO inviteDAO;
	@Getter private final ExpenseDAO expenseDAO;
	@Getter private final ExpenseUserDAO expenseUserDAO;
	@Getter private final PaymentDAO paymentDAO;
	private DatabaseChangeObserver databaseChangeObserver;
	@Getter private RMIObserver rmiObserver;

	public DataBaseManager(String dbPath, NotificationObserver notificationObserver) {
		logger.debug("Database path: {}", dbPath);
		this.dbPath = dbPath;

		logger.info("Initializing database...");

		try {
			//Note: getConnection() will create the database if it doesn't exist
			conn = DriverManager.getConnection("jdbc:sqlite:" + this.dbPath);

			logger.debug("Connected to the database");

			syncManager = new DatabaseSyncManager();
			createTables(conn);

			this.userDAO = new UserDAO(this);
			this.groupDAO = new GroupDAO(this);
			this.groupUserDAO = new GroupUserDAO(this);
			this.inviteDAO = new InviteDAO(this);
			this.expenseDAO = new ExpenseDAO(this);
			this.expenseUserDAO = new ExpenseUserDAO(this);
			this.paymentDAO = new PaymentDAO(this);

		} catch ( SQLException e ) {
			throw new RuntimeException("SQLException: " + e.getMessage()); //TODO: improve error message
		}
		logger.debug("Setting notification observer {}", notificationObserver);
		this.notificationObserver = notificationObserver;
	}

	//TODO: change tables properties (have been change throw the time)
	private void createTables(Connection conn) throws SQLException {
		try ( Statement stmt = conn.createStatement() ) {
			//language=SQLite
			stmt.executeUpdate("""			      
			                   CREATE TABLE IF NOT EXISTS version
			                   (
			                   	value INTEGER NOT NULL DEFAULT 0
			                   );
			                   INSERT INTO version (value)
			                   SELECT 0
			                   WHERE NOT EXISTS (SELECT 1 FROM version);
			                   """);

			//language=SQLite
			stmt.executeUpdate("""
			                   CREATE TABLE IF NOT EXISTS users
			                   (
			                   	id 				INTEGER PRIMARY KEY AUTOINCREMENT,
			                   	username 		TEXT NOT NULL,
			                   	email 			TEXT NOT NULL UNIQUE,
			                   	password 		TEXT NOT NULL,
			                   	phone_number 	TEXT NOT NULL
			                   )
			                   """);

			//language=SQLite
			stmt.executeUpdate("""
			                   CREATE TABLE IF NOT EXISTS groups
			                   (
			                   	id 		INTEGER PRIMARY KEY AUTOINCREMENT,
			                   	name 	TEXT NOT NULL
			                   )
			                   """);

			//language=SQLite
			stmt.executeUpdate("""
			                   CREATE TABLE IF NOT EXISTS group_users
			                   (
			                    id          INTEGER PRIMARY KEY AUTOINCREMENT,
			                   	group_id 	INTEGER NOT NULL,
			                   	user_id 	INTEGER NOT NULL,
			                   	FOREIGN KEY (group_id) REFERENCES groups (id),
			                   	FOREIGN KEY (user_id) REFERENCES users (id)
			                   )
			                   """);

			//language=SQLite
			stmt.executeUpdate("""
			                   CREATE TABLE IF NOT EXISTS invites
			                   (
			                   	id              INTEGER PRIMARY KEY AUTOINCREMENT,
			                   	group_id        INTEGER NOT NULL,
			                   	guest_user_id   INTEGER NOT NULL,
			                   	inviter_user_id INTEGER NOT NULL,
			                   	FOREIGN KEY (group_id) REFERENCES groups (id),
			                   	FOREIGN KEY (guest_user_id) REFERENCES users (id),
			                   	FOREIGN KEY (inviter_user_id) REFERENCES users (id)
			                   )
			                   """);

			//language=SQLite
			stmt.executeUpdate("""
			                   CREATE TABLE IF NOT EXISTS expenses
		                       (
		                          id                    INTEGER PRIMARY KEY AUTOINCREMENT,
		                          group_id              INTEGER NOT NULL,
		                          amount                REAL    NOT NULL,
		                          description           TEXT    NOT NULL,
		                          date                  TEXT    NOT NULL,
		                          paid_by_user_id       INTEGER NOT NULL,
		                          inserted_by_user_id   INTEGER NOT NULL,
		                          FOREIGN KEY (group_id) REFERENCES groups (id),
		                          FOREIGN KEY (paid_by_user_id) REFERENCES users (id),
		                          FOREIGN KEY (inserted_by_user_id) REFERENCES users (id)
		                       )
			                   """);

			//language=SQLite
			stmt.executeUpdate("""
			                   CREATE TABLE IF NOT EXISTS payments
			                   (
			                       id           INTEGER PRIMARY KEY AUTOINCREMENT,
			                       group_id     INTEGER NOT NULL,
			                       amount       REAL    NOT NULL,
			                       date         TEXT    NOT NULL,
			                       from_user_id INTEGER NOT NULL,
			                       for_user_id  INTEGER NOT NULL,
			                       FOREIGN KEY (group_id) REFERENCES groups (id),
			                       FOREIGN KEY (from_user_id) REFERENCES users (id),
			                       FOREIGN KEY (for_user_id) REFERENCES users (id)
			                   )
			                   """);

			//language=SQLite
			stmt.executeUpdate("""
			                   CREATE TABLE IF NOT EXISTS expense_users
			                   (
			                    id 			INTEGER PRIMARY KEY AUTOINCREMENT,
			                   	expense_id 	INTEGER NOT NULL,
			                   	user_id 	INTEGER NOT NULL,
			                   	FOREIGN KEY (expense_id) REFERENCES expenses (id),
			                   	FOREIGN KEY (user_id) REFERENCES users (id)
			                   )
			                   """);
		}
	}

	public boolean addDBChangeObserver(DatabaseChangeObserver observer) {
		logger.debug("Setting database change observer {}", observer);
		if (databaseChangeObserver != null) {
			logger.error("Database change observer already set");
			return false; //TODO: throw exception (?)
		}

		databaseChangeObserver = observer;
		return true;
	}

	public boolean addRMIChangeObserver(RMIObserver observer) {
		logger.debug("Setting RMI observer {}", observer);
		if (rmiObserver != null) {
			logger.error("RMI observer already set");
			return false; //TODO: throw exception (?)
		}

		rmiObserver = observer;
		return true;
	}

	public File getDBFile() {
		return new File(Paths.get(dbPath).toAbsolutePath().toString());
	}

	public int executeWriteWithId(String query, Object... params) throws SQLException {
		return executeWriteTransactionWithId(query, params);
	}

	private int executeWriteTransactionWithId(String query, Object... params) throws SQLException {
		try {
			return syncManager.executeOperation(() -> {
				conn.setAutoCommit(false);
				try {
					int id;
					try ( PreparedStatement pstmt = conn.prepareStatement(query) ) {
						for (int i = 0; i < params.length; i++) {
							pstmt.setObject(i + 1, params[i]);
						}

						try ( ResultSet rs = pstmt.executeQuery() ) {
							if (!rs.next()) {
								throw new SQLException("No ID was returned from the operation");
							}
							id = (int) rs.getLong(1);
						}
					}

					incrementVersion(conn);
					notifyDatabaseChange(query, params);
					conn.commit();
					return id;
				} catch ( SQLException e ) {
					try {
						conn.rollback();
					} catch ( SQLException rollbackEx ) {
						logger.error("Error rolling back transaction", rollbackEx);
					}
					throw e;
				} finally {
					conn.setAutoCommit(true);
				}
			});
		} catch ( InterruptedException e ) {
			Thread.currentThread().interrupt();
			throw new SQLException("Operation interrupted", e);
		}
	}

	private void incrementVersion(Connection conn) throws SQLException {
		int newVersion = getVersion() + 1;
		logger.debug("Incrementing version from {} to {}", getVersion(), newVersion);
		//language=SQLite
		PreparedStatement pstmt = conn.prepareStatement("UPDATE version SET value = ?");
		pstmt.setInt(1, newVersion);
		pstmt.executeUpdate();
	}

	private void notifyDatabaseChange(String query, Object... params) {
		if (databaseChangeObserver != null) {
			databaseChangeObserver.onDBChange(query, params);
		}
	}

	public int getVersion() {
		logger.debug("Getting version");
		int version = -1;
		//language=SQLite
		String query = "SELECT * FROM version;";

		try {
			List<Map<String, Object>> rs = executeRead(query);
			version = (int) rs.getFirst().get("value");
		} catch ( SQLException e ) {
			logger.error("Getting version: {}", e.getMessage());
		}

		logger.debug("Current version: {}", version);

		return version;
	}

	public List<Map<String, Object>> executeRead(String query, Object... params) throws SQLException {
		List<Map<String, Object>> results = new ArrayList<>();
		try ( PreparedStatement pstmt = conn.prepareStatement(query) ) {
			for (int i = 0; i < params.length; i++) {
				pstmt.setObject(i + 1, params[i]);
			}

			try ( ResultSet rs = pstmt.executeQuery() ) {
				ResultSetMetaData metaData = rs.getMetaData();
				int columnCount = metaData.getColumnCount();

				while (rs.next()) {
					Map<String, Object> row = new HashMap<>();
					for (int i = 1; i <= columnCount; i++) {
						row.put(metaData.getColumnName(i), rs.getObject(i));
					}
					results.add(row);
				}
			}
		}
		return results;
	}

	public int executeWrite(String query, Object... params) throws SQLException {
		return executeWriteTransaction(query, params);
	}

	private int executeWriteTransaction(String query, Object... params) throws SQLException {
		try {
			return syncManager.executeOperation(() -> {
				conn.setAutoCommit(false);
				try {
					int affectedRows;
					try ( PreparedStatement pstmt = conn.prepareStatement(query) ) {
						for (int i = 0; i < params.length; i++) {
							pstmt.setObject(i + 1, params[i]);
						}
						affectedRows = pstmt.executeUpdate();
					}

					incrementVersion(conn);
					notifyDatabaseChange(query, params);
					conn.commit();
					return affectedRows;
				} catch ( SQLException e ) {
					try {
						conn.rollback();
					} catch ( SQLException rollbackEx ) {
						logger.error("Error rolling back transaction", rollbackEx);
					}
					throw e;
				} finally {
					conn.setAutoCommit(true);
				}
			});
		} catch ( InterruptedException e ) {
			Thread.currentThread().interrupt();
			throw new SQLException("Operation interrupted", e);
		}
	}

	//TODO: this trigger should be on DataBaseManger (?)
	public void triggerNotification(String email, String text) {
		logger.debug("Triggering notification for {} ({})", email, notificationObserver);
		if (notificationObserver == null) return; //TODO: throw exception (?)

		NotificaionResponse notification = new NotificaionResponse(email, text);
		notificationObserver.onNotification(notification);
	}
}
