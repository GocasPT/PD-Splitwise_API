package pt.isec.pd.splitwise.sharedLib.database.DTO.Balance;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public final class DetailBalanceDTO implements Serializable {
	private double totalExpended;

	private double totalDebt;

	private Map<String, Double> debtList;

	private double totalReceive;

	private Map<String, Double> receiveList;

	public DetailBalanceDTO() {
		this.totalExpended = 0;
		this.totalDebt = 0;
		this.debtList = new HashMap<>();
		this.totalReceive = 0;
		this.receiveList = new HashMap<>();
	}
}
