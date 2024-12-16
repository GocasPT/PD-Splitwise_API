package pt.isec.pd.splitwise.client.ui.controller.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.apache.commons.lang3.StringUtils;
import pt.isec.pd.splitwise.client.model.ModelManager;
import pt.isec.pd.splitwise.client.ui.component.dialog.EditExpenseDialog;
import pt.isec.pd.splitwise.client.ui.controller.BaseController;
import pt.isec.pd.splitwise.client.ui.manager.ViewManager;
import pt.isec.pd.splitwise.sharedLib.database.DTO.Expense.DetailExpenseDTO;
import pt.isec.pd.splitwise.sharedLib.network.request.Expense.DeleteExpense;
import pt.isec.pd.splitwise.sharedLib.network.request.Expense.EditExpense;
import pt.isec.pd.splitwise.sharedLib.network.request.Expense.GetExpense;
import pt.isec.pd.splitwise.sharedLib.network.response.Response;
import pt.isec.pd.splitwise.sharedLib.network.response.ValueResponse;

import java.io.IOException;

public class ExpenseController extends BaseController {
	@FXML public Text txtGroupName;
	@FXML public Text tfAmount;
	@FXML public Text tfDescription;
	@FXML public Text tfDate;
	@FXML public Text tfPayerUser;
	@FXML public Text tfAssociatedUsers;
	@FXML public HBox hbBtn;
	@FXML public Button btnEdit;
	@FXML public Button btnBack;
	@FXML public Button btnDelete;

	public ExpenseController(ViewManager viewManager, ModelManager modelManager) {
		super(viewManager, modelManager);
	}

	@Override
	protected void registerHandlers() {
		btnEdit.setOnAction(e -> editPopup());
		btnBack.setOnAction(e -> viewManager.showView("group_view"));
		btnDelete.setOnAction(e -> {
			viewManager.sendRequestAsync(new DeleteExpense(modelManager.getExpenseInViewId()), (response) -> {
				if (!response.isSuccess()) {
					viewManager.showError(response.getErrorDescription());
					return; //TODO: handle error
				}

				viewManager.showView("group_view");
			});
		});
	}

	@Override
	protected void update() {
		viewManager.sendRequestAsync(new GetExpense(modelManager.getExpenseInViewId()), this::handleResponse);
	}

	@Override
	protected void handleResponse(Response response) {
		if (!response.isSuccess()) {
			viewManager.showError(response.getErrorDescription());
			return;
		}

		if (response instanceof ValueResponse<?> valueResponse)
			if (valueResponse.getValue() instanceof DetailExpenseDTO expense) {
				tfAmount.setText(String.valueOf(expense.getAmount()));
				tfDescription.setText(expense.getTitle());
				tfDate.setText(expense.getDate().toString());
				tfPayerUser.setText(expense.getPayerUser() == null ? "'Sem pagador'" : expense.getPayerUser());
				tfAssociatedUsers.setText(
						StringUtils.join(
								expense.getAssociatedUsersList().stream().map(
										u -> u == null ? "'Sem utilizador'" : "-" + u).toArray(),
								",\n"
						)
				);
			} else
				viewManager.showError("Failed to get expense: Invalid response value");
		else
			viewManager.showError("Failed to get expense: Invalid response type");
	}

	private void editPopup() {
		try {
			EditExpenseDialog dialog = new EditExpenseDialog(txtGroupName.getScene().getWindow());
			dialog.showAndWait().ifPresent(detailExpenseDTO -> {
				viewManager.sendRequestAsync(
						new EditExpense(
								modelManager.getExpenseInViewId(),
								detailExpenseDTO.getAmount(),
								detailExpenseDTO.getTitle(),
								detailExpenseDTO.getDate(),
								detailExpenseDTO.getPayerUser(),
								detailExpenseDTO.getAssociatedUsersList().toArray(String[]::new)
						), (response) -> {
							if (!response.isSuccess()) {
								viewManager.showError(response.getErrorDescription());
								return; //TODO: handle error
							}

							update();
						});
			});
		} catch ( IOException e ) {
			viewManager.showError("Error loading edit expense popup");
		}
	}
}
