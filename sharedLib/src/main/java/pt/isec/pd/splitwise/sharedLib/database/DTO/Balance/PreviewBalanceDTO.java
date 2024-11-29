package pt.isec.pd.splitwise.sharedLib.database.DTO.Balance;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public final class PreviewBalanceDTO implements Serializable {
	private double totalBalance;
	private Map<String, Double> usersBalance; //TODO: check this later

	public PreviewBalanceDTO() {
		this.totalBalance = 0;
		this.usersBalance = new HashMap<>();
	}
}
