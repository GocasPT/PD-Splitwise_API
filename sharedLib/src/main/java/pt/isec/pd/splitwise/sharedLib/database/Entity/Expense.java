package pt.isec.pd.splitwise.sharedLib.database.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public final class Expense extends Entity {
	private int groupId;

	private double amount;

	private String title;

	private LocalDate date;

	private String registerByUser;

	private String payerUser;

	private List<String> associetedUsersList;

	public Expense() {
		super(0);
	}
}
