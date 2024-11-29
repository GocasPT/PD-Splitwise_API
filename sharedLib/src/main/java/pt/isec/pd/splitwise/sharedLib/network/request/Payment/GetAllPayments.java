package pt.isec.pd.splitwise.sharedLib.network.request.Payment;

import pt.isec.pd.splitwise.sharedLib.database.DTO.Payment.PreviewPaymentDTO;
import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;
import pt.isec.pd.splitwise.sharedLib.network.request.Request;
import pt.isec.pd.splitwise.sharedLib.network.response.ListResponse;
import pt.isec.pd.splitwise.sharedLib.network.response.Response;

import java.util.ArrayList;
import java.util.List;

public record GetAllPayments(int groupID) implements Request {
	@Override
	public Response execute(DataBaseManager context) {
		logger.debug("Getting payments from group {}", groupID);

		List<PreviewPaymentDTO> payments = new ArrayList<>();
		try {
			context.getPaymentDAO().getAllPaymentsFromGroup(groupID).forEach(
					payment -> payments.add(
							new PreviewPaymentDTO(
									payment.getId(),
									payment.getAmount(),
									payment.getDate(),
									payment.getFromUser(),
									payment.getToUser()
							)
					)
			);
		} catch ( Exception e ) {
			logger.error("GetAllPayments: {}", e.getMessage());
			return new ListResponse<>("Fault to get payments");
		}

		return new ListResponse<>(payments.toArray(PreviewPaymentDTO[]::new));
	}

	@Override
	public String toString() {
		return "GET_PAYMENTS " + groupID;
	}
}
