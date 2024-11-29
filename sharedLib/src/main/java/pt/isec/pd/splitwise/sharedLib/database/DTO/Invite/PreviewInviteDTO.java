package pt.isec.pd.splitwise.sharedLib.database.DTO.Invite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class PreviewInviteDTO implements Serializable {
	private int id;
	private String groupName;
	private String guestEmail;
	private String hostEmail; //TODO: host OR inviter?
}
