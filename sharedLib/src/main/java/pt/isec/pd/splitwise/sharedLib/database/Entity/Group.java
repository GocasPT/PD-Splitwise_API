package pt.isec.pd.splitwise.sharedLib.database.Entity;

import jdk.jfr.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

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

	public Group() {
		super(0);
	}
}
