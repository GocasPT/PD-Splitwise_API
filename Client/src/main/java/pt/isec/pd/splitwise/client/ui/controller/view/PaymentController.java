package pt.isec.pd.splitwise.client.ui.controller.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import pt.isec.pd.splitwise.client.model.ModelManager;
import pt.isec.pd.splitwise.client.ui.controller.BaseController;
import pt.isec.pd.splitwise.client.ui.manager.ViewManager;
import pt.isec.pd.splitwise.sharedLib.database.DTO.Payment.DetailPaymentDTO;
import pt.isec.pd.splitwise.sharedLib.network.request.Payment.GetPayment;
import pt.isec.pd.splitwise.sharedLib.network.response.Response;
import pt.isec.pd.splitwise.sharedLib.network.response.ValueResponse;

public class PaymentController extends BaseController {
	@FXML public Text txtGroupName;
	@FXML public Text tfAmount;
	@FXML public Text tfDate;
	@FXML public Text tfFromUser;
	@FXML public Text tfToUser;
	@FXML public HBox hbBtn;
	@FXML public Button btnBack;

	public PaymentController(ViewManager viewManager, ModelManager modelManager) {
		super(viewManager, modelManager);
	}

	@Override
	protected void registerHandlers() {
		btnBack.setOnAction(e -> viewManager.showView("group_view"));
	}

	@Override
	protected void update() {
		viewManager.sendRequestAsync(new GetPayment(modelManager.getExpenseInViewId()), this::handleResponse);
	}

	@Override
	protected void handleResponse(Response response) {
		if (!response.isSuccess()) {
			viewManager.showError(response.getErrorDescription());
			return;
		}

		if (response instanceof ValueResponse<?> valueResponse)
			if (valueResponse.getValue() instanceof DetailPaymentDTO payment) {
				tfAmount.setText(String.valueOf(payment.getAmount()));
				tfDate.setText(payment.getDate().toString());
				tfFromUser.setText(payment.getFromUser());
				tfToUser.setText(payment.getToUser());
			} else
				viewManager.showError("Failed to get expense: Invalid response value");
		else
			viewManager.showError("Failed to get expense: Invalid response type");
	}
}
