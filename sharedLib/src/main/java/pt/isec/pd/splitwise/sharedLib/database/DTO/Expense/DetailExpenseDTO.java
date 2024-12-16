package pt.isec.pd.splitwise.sharedLib.database.DTO.Expense;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class DetailExpenseDTO implements Serializable {
	private int id;

	private double amount;

	private String title;

	private LocalDate date;

	private String registeredBy;

	private String payerUser;

	private List<String> associatedUsersList;
}
