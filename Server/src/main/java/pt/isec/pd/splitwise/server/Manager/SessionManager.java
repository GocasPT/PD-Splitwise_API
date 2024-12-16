package pt.isec.pd.splitwise.server.Manager;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.util.StringUtils;
import pt.isec.pd.splitwise.server.Runnable.ClientHandler;
import pt.isec.pd.splitwise.sharedLib.database.Observer.NotificationObserver;
import pt.isec.pd.splitwise.sharedLib.network.response.NotificaionResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SessionManager implements NotificationObserver {
	private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);

	private final Map<String, List<ClientHandler>> sessions = new HashMap<>();

	public void addSession(String userId, ClientHandler handler) {
		logger.debug("Adding session for user {}", userId);

		if (sessions.containsKey(userId))
			sessions.get(userId).add(handler);
		else
			sessions.put(userId, new ArrayList<>(List.of(handler)));

		showSessions();
	}

	private void showSessions() {
		//TODO: if no sessions, print "[]"
		logger.debug("Sessions:\n{}",
		             StringUtils
				             .join(
						             sessions.entrySet().stream()
								             .map(e -> e.getKey() + " -> " + e.getValue())
								             .toList(),
						             "\n"
				             ));
	}

	public void removeSession(String userId, ClientHandler handler) {
		logger.debug("Removing session for user {}", userId);

		sessions.get(userId).remove(handler);
		if (sessions.get(userId).isEmpty())
			sessions.remove(userId);

		showSessions();
	}

	@Override
	public void onNotification(NotificaionResponse notification) {
		try {
			List<ClientHandler> handler = sessions.get(notification.getEmail());
			if (handler != null)
				for (ClientHandler clientHandler : handler)
					clientHandler.sendMessage(notification);

		} catch ( IOException e ) {
			throw new RuntimeException(e); //TODO: improve this
		}
	}
}
