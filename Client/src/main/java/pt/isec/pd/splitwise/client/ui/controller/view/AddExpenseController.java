package pt.isec.pd.splitwise.client.ui.controller.view;

import com.dlsc.gemsfx.CalendarPicker;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import net.synedra.validatorfx.TooltipWrapper;
import net.synedra.validatorfx.Validator;
import org.controlsfx.control.CheckComboBox;
import pt.isec.pd.splitwise.client.model.ModelManager;
import pt.isec.pd.splitwise.client.ui.controller.BaseController;
import pt.isec.pd.splitwise.client.ui.manager.ViewManager;
import pt.isec.pd.splitwise.sharedLib.database.DTO.User.PreviewUserDTO;
import pt.isec.pd.splitwise.sharedLib.network.request.Expense.InsertExpense;
import pt.isec.pd.splitwise.sharedLib.network.request.Group.GetUsersOnGroup;
import pt.isec.pd.splitwise.sharedLib.network.response.Response;
import pt.isec.pd.splitwise.sharedLib.network.response.ValueResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class AddExpenseController extends BaseController {
	@FXML public Text txtGroupName;
	@FXML public TextField tfAmount;
	@FXML public TextField tfDescription;
	@FXML public CalendarPicker datePicker;
	@FXML public ComboBox<PreviewUserDTO> cbPayerUser;
	@FXML public CheckComboBox<PreviewUserDTO> ccbAssociatedUsers;
	@FXML public HBox hbBtn;
	@FXML public Button btnAdd;
	@FXML public Button btnCancel;

	public AddExpenseController(ViewManager viewManager, ModelManager modelManager) {
		super(viewManager, modelManager);
	}

	@Override
	protected void registerHandlers() {
		Validator validator = new Validator();

		validator.createCheck().dependsOn("amount", tfAmount.textProperty()).withMethod(c -> {
			String amountStr = c.get("amount");
			if (amountStr == null || amountStr.isEmpty()) {
				c.error("Amount is required");
			} else {
				try {
					Double.parseDouble(amountStr);
				} catch ( NumberFormatException e ) {
					c.error("Amount must be a number");
				}
			}
		}).decorates(tfAmount).immediate();

		validator.createCheck().dependsOn("description", tfDescription.textProperty()).withMethod(c -> {
			String description = c.get("description");
			if (description == null || description.isEmpty()) {
				c.error("Description is required");
			}
		}).decorates(tfDescription).immediate();

		validator.createCheck().dependsOn("date", datePicker.valueProperty()).withMethod(c -> {
			if (c.get("date") == null) {
				c.error("Date is required");
			}
		}).decorates(datePicker).immediate();

		validator.createCheck().dependsOn("associatedUsers", Bindings.size(
				ccbAssociatedUsers.getCheckModel().getCheckedItems())).withMethod(c -> {
			int numAssociatedUsers = c.get("associatedUsers");
			if (numAssociatedUsers == 0) {
				c.error("At least one associated user is required");
			}
		}).decorates(ccbAssociatedUsers).immediate();

		TooltipWrapper<Button> btnAddWrapper = new TooltipWrapper<>(btnAdd, validator.containsErrorsProperty(),
		                                                            Bindings.concat("Cannot sign up:\n",
		                                                                            validator.createStringBinding()));
		hbBtn.getChildren().addFirst(btnAddWrapper);

		//TODO: fix
		// - show username + userEmail on selection
		// - show username on display
		cbPayerUser.getItems().clear();
		cbPayerUser.setButtonCell(new ListCell<>() {
			@Override
			protected void updateItem(PreviewUserDTO item, boolean empty) {
				super.updateItem(item, empty);

				if (empty || item == null) setText(null);
				else
					//setText(item.username() + " <" + item.userEmail() + ">");
					setText(item.getEmail());
			}
		});
		cbPayerUser.setCellFactory(param -> new ListCell<>() {
			@Override
			protected void updateItem(PreviewUserDTO item, boolean empty) {
				super.updateItem(item, empty);

				if (empty || item == null) setText(null);
				else
					//setText(item.username() + " <" + item.userEmail() + ">");
					setText(item.getEmail());

			}
		});

		//TODO: fix
		// - show username + userEmail on selection
		// - show username on display OR number of users when more than one (?) (e.g. "3 users")
		ccbAssociatedUsers.getItems().clear();
		ccbAssociatedUsers.setConverter(new StringConverter<>() {
			@Override
			public String toString(PreviewUserDTO object) {
				//return object.username() + " <" + object.userEmail() + ">";
				return object.getEmail();
			}

			@Override
			public PreviewUserDTO fromString(String string) {
				return null;
			}
		});

		btnAdd.setOnAction(e -> {
			addExpense();
		});

		btnCancel.setOnAction(e -> {
			viewManager.showView("group_view");
		});

		int groupId = modelManager.getGroupInViewId();
		viewManager.sendRequestAsync(new GetUsersOnGroup(groupId), (response -> {
			if (!response.isSuccess()) {
				viewManager.showError(response.getErrorDescription());
				viewManager.showView("group_view");
				return;
			}

			ValueResponse<Map<String, Object>> listResponse = (ValueResponse<Map<String, Object>>) response;

			String groupName = (String) listResponse.getValue().get("group_name");
			txtGroupName.setText(groupName);

			List<PreviewUserDTO> member = (List<PreviewUserDTO>) listResponse.getValue().get("members");
			for (PreviewUserDTO user : member) {
				cbPayerUser.getItems().add(user);
				ccbAssociatedUsers.getItems().add(user);
			}
		}));
	}

	@Override
	protected void update() {

	}

	@Override
	protected void handleResponse(Response response) {
		if (!response.isSuccess()) {
			viewManager.showError(response.getErrorDescription());
			return;
		}

		viewManager.showView("group_view");
	}

	private void addExpense() {
		String amountStr = tfAmount.getText();
		double amount = Double.parseDouble(amountStr);
		String description = tfDescription.getText();
		LocalDate date = datePicker.getValue();
		String payerEmail = cbPayerUser.getValue().getEmail();
		String[] associatedUsersEmail = ccbAssociatedUsers.getCheckModel().getCheckedItems().stream().map(
				PreviewUserDTO::getEmail).toArray(String[]::new);

		viewManager.sendRequestAsync(
				new InsertExpense(modelManager.getGroupInViewId(), amount, description, date,
				                  modelManager.getEmailLoggedUser(), payerEmail, associatedUsersEmail),
				this::handleResponse);
	}
}
