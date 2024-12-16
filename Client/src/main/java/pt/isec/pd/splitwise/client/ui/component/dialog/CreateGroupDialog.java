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

import java.io.IOException;

public class CreateGroupDialog extends Dialog<String> {
	@FXML
	private TextField tfGroupName;

	@FXML
	private ButtonType btnFinish;

	public CreateGroupDialog(Window owner) throws IOException {
		FXMLLoader loader = new FXMLLoader(ClientApp.class.getResource("components/dialogs/create_group_dialog.fxml"));
		loader.setController(this);

		DialogPane dialogPane = loader.load();
		initOwner(owner);
		initModality(Modality.APPLICATION_MODAL);

		setTitle("Create Group");
		setDialogPane(dialogPane);
		setResultConverter(buttonType -> {
			if (buttonType == null)
				return null;

			if (buttonType.equals(btnFinish))
				return tfGroupName.getText();
			else
				return null;
		});

		setOnShowing(dialogEvent -> Platform.runLater(() -> tfGroupName.requestFocus()));
	}
}
