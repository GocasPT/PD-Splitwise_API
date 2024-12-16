package pt.isec.pd.splitwise.client.ui.manager;

import javafx.stage.Stage;
import javafx.stage.Window;
import org.controlsfx.control.Notifications;
import pt.isec.pd.splitwise.sharedLib.network.response.NotificaionResponse;

//TODO: improve this class
public class NotificationManager {
	public static void showNotification(NotificaionResponse notificaionResponse) {
		Stage.getWindows().stream()
				.filter(Window::isShowing)
				.findFirst()
				.ifPresent(activeWindow -> Notifications.create()
						.title("Notification")
						.text(notificaionResponse.getNotifyDescription())
						.hideAfter(javafx.util.Duration.seconds(5))
						.owner(activeWindow)
						.show());

	}
}
