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
public final class DetailPaymentDTO implements Serializable { //TODO: i need this class?
	private int id;

	private double amount;

	private LocalDate date;

	private String fromUser;

	private String toUser;
}
