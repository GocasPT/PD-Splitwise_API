package pt.isec.pd.splitwise.sharedLib.database.DAO;


import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;
import pt.isec.pd.splitwise.sharedLib.database.Entity.Payment;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PaymentDAO extends DAO {
	public PaymentDAO(DataBaseManager dbManager) {
		super(dbManager);
	}

	public int createPayment(int groupId, int fromUserId, int forUserId, double amount, LocalDate date) throws SQLException {
		//TODO: created payment = insert payment + check debts from_user to for_user
		logger.debug("Creating payment from user {} to user {} with amount {}", fromUserId, forUserId, amount);
		//language=SQLite
		String query = "INSERT INTO payments (group_id, from_user_id, for_user_id, amount, date) VALUES (?, ?, ?, ?, ?)";
		return dbManager.executeWrite(query, groupId, fromUserId, forUserId, amount, date);
	}

	public Payment getPaymentById(int paymentId) throws SQLException {
		logger.debug("Getting payment with id {}", paymentId);
		//language=SQLite
		String query = """
		               SELECT payments.id      AS id,
		                      groups.id      AS group_id,
		                      payments.amount  AS amount,
		                      payments.date    AS payment_date,
		                      payer.email   AS from_user_email,
		                      reciver.email AS to_user_email
		               FROM payments
		                        JOIN groups ON payments.group_id = groups.id
		                        JOIN users payer ON payments.from_user_id = payer.id
		                        JOIN users reciver ON payments.for_user_id = reciver.id
		               WHERE payments.id = ?;
		               """;
		Map<String, Object> result = dbManager.executeRead(query, paymentId).getFirst();
		return Payment.builder()
				.id((int) result.get("id"))
				.groupId((int) result.get("group_id"))
				.amount((double) result.get("amount"))
				.date(LocalDate.parse(
						(String) result.get("payment_date"),
						DateTimeFormatter.ISO_DATE
				))
				.fromUser((String) result.get("from_user_email"))
				.toUser((String) result.get("to_user_email"))
				.build();
	}

	public List<Payment> getAllPaymentsFromGroup(int groupId) throws SQLException {
		logger.debug("Getting all payments for group {}", groupId);
		//language=SQLite
		String query = """
		               SELECT payments.id      AS id,
		                      groups.id      AS group_id,
		                      payments.amount  AS amount,
		                      payments.date    AS payment_date,
		                      payer.email   AS from_user_email,
		                      reciver.email AS to_user_email
		               FROM payments
		                        JOIN groups ON payments.group_id = groups.id
		                        JOIN users payer ON payments.from_user_id = payer.id
		                        JOIN users reciver ON payments.for_user_id = reciver.id
		               WHERE payments.group_id = ?;
		               """;
		List<Map<String, Object>> result = dbManager.executeRead(query, groupId);

		List<Payment> payments = new ArrayList<>();
		for (Map<String, Object> row : result)
			payments.add(
					Payment.builder()
							.id((int) row.get("id"))
							.groupId((int) row.get("group_id"))
							.amount((double) row.get("amount"))
							.date(LocalDate.parse(
									(String) row.get("payment_date"),
									DateTimeFormatter.ISO_DATE
							))
							.fromUser((String) row.get("from_user_email"))
							.toUser((String) row.get("to_user_email"))
							.build()
			);
		return payments;
	}

	//TODO: get all payments from user

	public boolean deletePayment(int paymentId) throws SQLException {
		logger.debug("Deleting payment with id {}", paymentId);
		//language=SQLite
		String query = "DELETE FROM payments WHERE id = ?";
		return dbManager.executeWrite(query, paymentId) > 0;
	}
}
