package pt.isec.pd.splitwise.sharedLib.database.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public final class Group extends Entity {
	private String name;
	private int numUsers;
	private List<User> members;
	private List<Expense> expenses;
	private List<Payment> payments;
}
