package pt.isec.pd.splitwise.client.ui.component.dialog;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Window;
import pt.isec.pd.splitwise.client.ClientApp;
import pt.isec.pd.splitwise.sharedLib.database.DTO.User.InfoUserDTO;

import java.io.IOException;

public class EditUserDialog extends Dialog<InfoUserDTO> {
	@FXML private TextField tfName;
	@FXML private TextField tfEmail;
	@FXML private TextField tfPhoneNumber;
	@FXML private TextField tfPassword;
	@FXML private ButtonType btnFinish;

	public EditUserDialog(Window owner) throws IOException {
		FXMLLoader loader = new FXMLLoader(ClientApp.class.getResource("components/dialogs/edit_user_dialog.fxml"));
		loader.setController(this);

		DialogPane dialogPane = loader.load();
		initOwner(owner);
		initModality(Modality.APPLICATION_MODAL);

		setTitle("Edit user");
		setDialogPane(dialogPane);
		setResultConverter(buttonType -> {
			if (buttonType == null) return null;

			if (buttonType.equals(btnFinish))
				return new InfoUserDTO(0, tfName.getText(), tfEmail.getText(), tfPhoneNumber.getText(),
				                       tfPassword.getText());
			else return null;
		});

		setOnShowing(dialogEvent -> Platform.runLater(() -> tfName.requestFocus()));
	}
}
