package pt.isec.pd.splitwise.sharedLib.network.request.Payment;

import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;
import pt.isec.pd.splitwise.sharedLib.network.request.Request;
import pt.isec.pd.splitwise.sharedLib.network.response.Response;

public record DeletePayment(int paymentID) implements Request {
	@Override
	public Response execute(DataBaseManager context) {
		logger.debug("Deleting payment {}", paymentID);

		try {
			context.getPaymentDAO().deletePayment(paymentID);
		} catch ( Exception e ) {
			logger.error("DeletePayment: {}", e.getMessage());
			return new Response(false);
		}

		return new Response(true);
	}

	@Override
	public String toString() {
		return "DELETE_PAYMENT " + paymentID;
	}
}
