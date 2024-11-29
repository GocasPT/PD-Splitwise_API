package pt.isec.pd.splitwise.sharedLib.network.request.Payment;

import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;
import pt.isec.pd.splitwise.sharedLib.network.request.Request;
import pt.isec.pd.splitwise.sharedLib.network.response.Response;

import java.time.LocalDate;

public record InsertPayment(int groupID, double amount, LocalDate date, String userPayerEmail,
                            String userReceiverEmail) implements Request {
	@Override
	public Response execute(DataBaseManager context) {
		logger.debug("""
		             Inserting payment on group {}
		             \tamount: {}
		             \tdate: {}
		             \tpayer: {}
		             \treceiver: {}""",
		             groupID, amount, date, userPayerEmail, userReceiverEmail);

		try {
			context.getPaymentDAO().createPayment(
					groupID,
					context.getUserDAO().getUserByEmail(userPayerEmail).getId(),
					context.getUserDAO().getUserByEmail(userReceiverEmail).getId(),
					amount, date
			);
		} catch ( Exception e ) {
			logger.error("InsertPayment: {}", e.getMessage());
			return new Response(false, "Failed to insert payment");
		}

		return new Response(true);
	}

	@Override
	public String toString() {
		return "INSERT_PAYMENT " + groupID + " " + userPayerEmail + " " + userReceiverEmail + " " + amount;
	}
}
