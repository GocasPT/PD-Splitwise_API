package pt.isec.pd.splitwise.sharedLib.database.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public final class Payment extends Entity {
	private int groupId;
	private double amount;
	private LocalDate date;
	private String fromUser; //TODO: email → pair<username, userEmail>
	private String toUser; //TODO: email → pair<username, userEmail>
}
