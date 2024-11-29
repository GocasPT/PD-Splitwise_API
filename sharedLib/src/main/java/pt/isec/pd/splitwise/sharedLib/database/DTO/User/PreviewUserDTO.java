package pt.isec.pd.splitwise.sharedLib.database.DTO.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class PreviewUserDTO implements Serializable {
	private int id;
	private String username;
	private String email;
}