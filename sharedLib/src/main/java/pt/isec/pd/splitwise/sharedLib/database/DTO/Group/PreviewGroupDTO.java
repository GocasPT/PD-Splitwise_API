package pt.isec.pd.splitwise.sharedLib.database.DTO.Group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class PreviewGroupDTO implements Serializable {
	private int id;

	private String name;

	private int numUsers;
}
