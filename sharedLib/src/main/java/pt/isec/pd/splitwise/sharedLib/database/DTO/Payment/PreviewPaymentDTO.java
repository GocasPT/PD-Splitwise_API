package pt.isec.pd.splitwise.sharedLib.database.DTO.Payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class PreviewPaymentDTO implements Serializable {
	private int id;

	private double amount;

	private LocalDate date;

	private String payerUser;

	private String receiverUser;
}
