package pt.isec.pd.splitwise.sharedLib.network.request.Payment;

import pt.isec.pd.splitwise.sharedLib.database.DTO.Payment.DetailPaymentDTO;
import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;
import pt.isec.pd.splitwise.sharedLib.database.Entity.Payment;
import pt.isec.pd.splitwise.sharedLib.network.request.Request;
import pt.isec.pd.splitwise.sharedLib.network.response.Response;
import pt.isec.pd.splitwise.sharedLib.network.response.ValueResponse;

public record GetPayment(int expenseId) implements Request {
	@Override
	public Response execute(DataBaseManager context) {
		logger.debug("Getting payment with id {}", expenseId);

		try {
			Payment payment = context.getPaymentDAO().getPaymentById(expenseId);
			return new ValueResponse<>(
					DetailPaymentDTO.builder()
							.id(payment.getId())
							.amount(payment.getAmount())
							.date(payment.getDate())
							.fromUser(payment.getFromUser())
							.toUser(payment.getToUser())
							.build()
			);
		} catch ( Exception ex ) {
			logger.error("Failed to get expense with id {}: {}", expenseId, ex.getMessage());
			return new ValueResponse<>("Failed to get expense with id " + expenseId + ": " + ex.getMessage());
		}
	}

	@Override
	public String toString() {
		return "GET_PAYMENT " + expenseId;
	}
}
