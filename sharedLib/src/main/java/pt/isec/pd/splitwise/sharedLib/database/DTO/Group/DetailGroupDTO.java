package pt.isec.pd.splitwise.sharedLib.database.DTO.Group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pt.isec.pd.splitwise.sharedLib.database.DTO.Expense.PreviewExpenseDTO;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class DetailGroupDTO implements Serializable { //TODO: check this later
	private int id;

	private String name;

	private List<PreviewExpenseDTO> expenses;
}
