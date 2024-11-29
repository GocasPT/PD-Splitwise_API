package pt.isec.pd.splitwise.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.isec.pd.splitwise.server.Manager.HeartbeatManager;
import pt.isec.pd.splitwise.server.Manager.SessionManager;
import pt.isec.pd.splitwise.server.Runnable.ClientHandler;
import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

//TODO: private → protected
public class Server {
	public static final int TIMEOUT_CLIENT_SOCKET = 60;
	private static final Logger logger = LoggerFactory.getLogger(Server.class);
	private final ServerSocket serverSocket;
	private final SessionManager sessionManager;
	private final DataBaseManager dbManager;
	private final HeartbeatManager heartbeatManager;
	private volatile boolean isRunning;

	public Server(int listeningPort, String dbPath) {
		try {
			isRunning = true;
			serverSocket = new ServerSocket(listeningPort);
			sessionManager = new SessionManager();
			dbManager = new DataBaseManager(dbPath, sessionManager);
			heartbeatManager = new HeartbeatManager(isRunning, dbManager);
			if (!dbManager.addDBChangeObserver(heartbeatManager.getHeartbeatSender())) {
				throw new RuntimeException("Failed to add observer to DataBaseManager"); //TODO: improve this
			}

			start();
		} catch ( IOException e ) {
			throw new RuntimeException("IOException in 'Server': " + e.getMessage()); //TODO: improve this
		} finally {
			stop();
		}
	}

	private void start() {
		heartbeatManager.startHeartbeat();

		//TODO: see if this can be improved
		logger.info("Server ready to receive clients...");
		try {
			while (isRunning) {
				Socket clientSocket = serverSocket.accept();
				clientSocket.setSoTimeout(Server.TIMEOUT_CLIENT_SOCKET * 1000);
				//TODO: Runnable VS Thread
				new Thread(
						new ClientHandler(clientSocket, sessionManager, dbManager),
						clientSocket.getInetAddress().getHostAddress()
				).start();
			}
		} catch ( SocketException e ) {
			throw new RuntimeException(e); //TODO: improve this
		} catch ( IOException e ) {
			throw new RuntimeException(e); //TODO: improve this
		}
	}

	//TODO: check this method
	private void stop() {
		isRunning = false;

		try {
			if (heartbeatManager != null)
				heartbeatManager.stopHeartbeat();

			serverSocket.close();
		} catch ( Exception e ) {
			logger.error(e.getMessage());
		}
	}

	public static void main(String[] args) {
		if (args.length != 2) {
			logger.error("Usage: java Server <listening_port> <db_path>"); //TODO: check this later
			return;
		}

		try {
			new Server(Integer.parseInt(args[0]), args[1]);
		} catch ( NumberFormatException e ) {
			logger.error("Invalid port number: {}", args[0]);
		}
	}
}
