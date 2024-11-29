package pt.isec.pd.splitwise.sharedLib.database.DAO;

import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;
import pt.isec.pd.splitwise.sharedLib.database.Entity.Group;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//TODO: javaDoc

/**
 * The type Group dao. //TODO: class layer to access database and return group objects
 */
public class GroupDAO extends DAO {
	/**
	 * Instantiates a new Group dao.
	 *
	 * @param dbManager the db manager
	 */
	public GroupDAO(DataBaseManager dbManager) {
		super(dbManager);
	}

	/**
	 * Create group int.
	 *
	 * @param name   the name
	 * @param userId the user id
	 * @return the int
	 * @throws SQLException the sql exception
	 */
//TODO: rollback system (ex.: create group but can't associate user on group)
	public int createGroup(String name, int userId) throws SQLException {
		logger.debug("Creating group:\n\tname={}", name);

		//language=SQLite
		String queryInsertGroup = "INSERT INTO groups (name) VALUES (?) RETURNING id";
		int id = dbManager.executeWriteWithId(queryInsertGroup, name);

		logger.debug("Created group with id: {}", id);

		dbManager.getGroupUserDAO().createRelation(id, userId);
		return id;
	}

	/**
	 * Gets all groups.
	 *
	 * @return the all groups
	 * @throws SQLException the sql exception
	 */
	public List<Group> getAllGroups() throws SQLException {
		logger.debug("Getting all groups");

		//language=SQLite
		String query = "SELECT * FROM groups";
		List<Map<String, Object>> results = dbManager.executeRead(query);
		List<Group> groups = new ArrayList<>();
		for (Map<String, Object> row : results) {
			groups.add(
					Group.builder()
							.id((int) row.get("id"))
							.name((String) row.get("name"))
							.build()
			);
		}
		return groups;
	}

	/**
	 * Gets group by id.
	 *
	 * @param groupId the id
	 * @return the group by id
	 * @throws SQLException the sql exception
	 */
	public Group getGroupById(int groupId) throws SQLException {
		logger.debug("Getting group with id: {}", groupId);

		//language=SQLite
		String query = "SELECT * FROM groups WHERE id = ?";
		List<Map<String, Object>> results = dbManager.executeRead(query, groupId);
		for (Map<String, Object> row : results) {
			return Group.builder()
					.id((int) row.get("id"))
					.name((String) row.get("name"))
					.build();
		}
		return null;
	}

	/**
	 * Edit group boolean.
	 *
	 * @param groupId the id
	 * @param name    the name
	 * @return the boolean
	 * @throws SQLException the sql exception
	 */
	public boolean editGroup(int groupId, String name) throws SQLException {
		logger.debug("Editing group with id {}\n\tname: {}", groupId, name);

		//language=SQLite
		String query = "UPDATE groups SET name = ? WHERE id = ?";
		return dbManager.executeWrite(query, name, groupId) > 0;
	}

	/**
	 * Delete group boolean.
	 *
	 * @param groupId the id
	 * @return the boolean
	 * @throws SQLException the sql exception
	 */
	public boolean deleteGroup(int groupId) throws SQLException {
		//TODO: check if group have debts or something like that
		logger.debug("Deleting group with id: {}", groupId);

		//language=SQLite
		String query = "DELETE FROM groups WHERE id = ?";
		return dbManager.executeWrite(query, groupId) > 0;
	}
}
