package pt.isec.pd.splitwise.sharedLib.database.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public final class Invite extends Entity {
	private int groupId;
	private String guestUserEmail;
	private String hostUserEmail; //TODO host OR inviter?
}
