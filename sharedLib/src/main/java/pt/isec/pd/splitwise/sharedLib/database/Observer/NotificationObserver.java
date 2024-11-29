package pt.isec.pd.splitwise.sharedLib.database.Observer;


import pt.isec.pd.splitwise.sharedLib.network.response.NotificaionResponse;

public interface NotificationObserver {
	void onNotification(NotificaionResponse notification);
}
