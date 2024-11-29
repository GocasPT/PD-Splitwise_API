package pt.isec.pd.splitwise.sharedLib.database.DAO;

import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;
import pt.isec.pd.splitwise.sharedLib.database.Entity.Invite;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//TODO: javaDoc

/**
 * The type Invite dao. //TODO: class layer to access database and return invite objects
 */
public class InviteDAO extends DAO {
	/**
	 * Instantiates a new Invite dao.
	 *
	 * @param dbManager the db manager
	 */
	public InviteDAO(DataBaseManager dbManager) {
		super(dbManager);
	}

	/**
	 * Create invite int.
	 *
	 * @param groupId       the group id
	 * @param guestUserId   the guest user id
	 * @param inviterUserId the inviter user id
	 * @return the int
	 * @throws SQLException the sql exception
	 */
	public int createInvite(int groupId, int guestUserId, int inviterUserId) throws SQLException {
		logger.debug("Creating invite for group {} with guest {} by user {}", groupId, guestUserId, inviterUserId);

		//language=SQLite
		String query = "INSERT INTO invites (group_id, guest_user_id, inviter_user_id) VALUES (?, ?, ?)";

		return dbManager.executeWrite(query, groupId, guestUserId, inviterUserId);
	}

	/**
	 * Gets all invites from user.
	 *
	 * @param userId the id
	 * @return the all invites from user
	 * @throws SQLException the sql exception
	 */
	public List<Invite> getAllInvitesFromUser(int userId) throws SQLException {
		logger.debug("Getting all invites for user {}", userId);

		//TODO: guester_user_name + inviter_user_name
		//language=SQLite
		String query = """
		               SELECT invites.id     AS id,
		                      groups.id      AS group_id,
		                      inviter.email    AS inviter_email
		               FROM invites
		                        JOIN groups ON groups.id = invites.group_id
		                        JOIN users AS guest ON guest.id = invites.guest_user_id
		               		JOIN users AS inviter ON inviter.id = invites.inviter_user_id
		               WHERE guest.id = ?""";

		List<Map<String, Object>> result = dbManager.executeRead(query, userId);
		List<Invite> invites = new ArrayList<>();
		for (Map<String, Object> row : result)
			invites.add(
					Invite.builder()
							.id((int) row.get("id"))
							.groupId((int) row.get("group_id"))
							.guestUserEmail((String) row.get("inviter_email")) //TODO: email → pair<username, userEmail>
							.build()
			);

		return invites;
	}

	/**
	 * Gets invite by id.
	 *
	 * @param inviteId the invite id
	 * @return the invite by id
	 * @throws SQLException the sql exception
	 */
	public Invite getInviteById(int inviteId) throws SQLException {
		logger.debug("Getting invite with id {}", inviteId);

		//language=SQLite
		String query = """
		               SELECT invites.id     AS id,
		                      groups.id      AS group_id,
		                      guest.email   AS guest_email,
		                      inviter.email    AS inviter_email
		               FROM invites
		                        JOIN groups ON groups.id = invites.group_id
		                        JOIN users AS guest ON guest.id = invites.guest_user_id
		               		JOIN users AS inviter ON inviter.id = invites.inviter_user_id
		               WHERE invites.id = ?""";

		Map<String, Object> result = dbManager.executeRead(query, inviteId).getFirst();
		return Invite.builder()
				.id((int) result.get("id"))
				.groupId((int) result.get("group_id"))
				.guestUserEmail((String) result.get("guest_email")) //TODO: email → pair<username, userEmail>
				.hostUserEmail((String) result.get("inviter_email")) //TODO: email → pair<username, userEmail>
				.build();

	}

	/**
	 * Accept invite boolean.
	 *
	 * @param inviteId the invite id
	 * @return the boolean
	 * @throws SQLException the sql exception
	 */
//TODO: rollback system (ex: can delete invite but can't insert into group_users)
	public boolean acceptInvite(int inviteId) throws SQLException {
		logger.debug("Accepting invite with id {}", inviteId);

		//language=SQLite
		String queryInsert = "INSERT INTO group_users (group_id, user_id) SELECT group_id, guest_user_id FROM invites WHERE id = ?";
		//language=SQLite
		String queryDelete = "DELETE FROM invites WHERE id = ?";

		boolean result = dbManager.executeWrite(queryInsert, inviteId) > 0;
		dbManager.executeWrite(queryDelete, inviteId);
		return result;
	}

	/**
	 * Decline invite boolean.
	 *
	 * @param inviteId the invite id
	 * @return the boolean
	 * @throws SQLException the sql exception
	 */
	public boolean declineInvite(int inviteId) throws SQLException {
		logger.debug("Declining invite with id {}", inviteId);

		//language=SQLite
		String query = "DELETE FROM invites WHERE id = ?";

		return dbManager.executeWrite(query, inviteId) > 0;
	}

	/**
	 * Delete invite boolean.
	 *
	 * @param inviteId the invite id
	 * @return the boolean
	 * @throws SQLException the sql exception
	 */
	public boolean deleteInvite(int inviteId) throws SQLException {
		logger.debug("Deleting invite with id {}", inviteId);

		//language=SQLite
		String query = "DELETE FROM invites WHERE id = ?";

		return dbManager.executeWrite(query, inviteId) > 0;
	}
}
