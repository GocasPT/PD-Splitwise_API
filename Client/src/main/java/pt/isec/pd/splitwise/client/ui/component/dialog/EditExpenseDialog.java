package pt.isec.pd.splitwise.client.ui.component.dialog;

import com.dlsc.gemsfx.CalendarPicker;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Window;
import org.controlsfx.control.CheckComboBox;
import pt.isec.pd.splitwise.client.ClientApp;
import pt.isec.pd.splitwise.sharedLib.database.DTO.Expense.DetailExpenseDTO;
import pt.isec.pd.splitwise.sharedLib.database.DTO.User.PreviewUserDTO;

import java.io.IOException;

public class EditExpenseDialog extends Dialog<DetailExpenseDTO> {
	//TODO inputs
	@FXML public TextField tfAmount;
	@FXML public TextField tfDescription;
	@FXML public CalendarPicker datePicker;
	@FXML public ComboBox<PreviewUserDTO> cbPayerUser;
	@FXML public CheckComboBox<PreviewUserDTO> ccbAssociatedUsers;
	@FXML private ButtonType btnFinish;

	public EditExpenseDialog(Window owner) throws IOException {
		FXMLLoader loader = new FXMLLoader(ClientApp.class.getResource("components/dialogs/edit_expense_dialog.fxml"));
		loader.setController(this);

		DialogPane dialogPane = loader.load();
		initOwner(owner);
		initModality(Modality.APPLICATION_MODAL);

		setTitle("Edit expense");
		setDialogPane(dialogPane);
		setResultConverter(buttonType -> {
			if (buttonType == null) return null;

			if (buttonType.equals(btnFinish))
				return DetailExpenseDTO.builder()
						.amount(Double.parseDouble(tfAmount.getText()))
						.title(tfDescription.getText())
						.date(datePicker.getValue())
						.payerUser(cbPayerUser.getValue().getEmail())
						.associatedUsersList(ccbAssociatedUsers.getCheckModel().getCheckedItems().stream().map(
								PreviewUserDTO::getEmail).toList())
						.build();
			else return null;
		});

		setOnShowing(dialogEvent -> Platform.runLater(() -> tfAmount.requestFocus()));
	}
}
