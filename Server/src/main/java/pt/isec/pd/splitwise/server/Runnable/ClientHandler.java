package pt.isec.pd.splitwise.server.Runnable;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.isec.pd.splitwise.server.Manager.SessionManager;
import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;
import pt.isec.pd.splitwise.sharedLib.network.request.Request;
import pt.isec.pd.splitwise.sharedLib.network.request.User.Login;
import pt.isec.pd.splitwise.sharedLib.network.request.User.Logout;
import pt.isec.pd.splitwise.sharedLib.network.request.User.Register;
import pt.isec.pd.splitwise.sharedLib.network.response.NotificaionResponse;
import pt.isec.pd.splitwise.sharedLib.network.response.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class ClientHandler implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

	private final Socket clientSocket;

	private final ObjectOutputStream out;

	private final ObjectInputStream in;

	private final SessionManager sessionManager;

	private final DataBaseManager context;

	private String email;

	//TODO: improve exception handling
	public ClientHandler(Socket clientSocket, SessionManager sessionManager, DataBaseManager context) throws IOException {
		this.clientSocket = clientSocket;
		this.out = new ObjectOutputStream(clientSocket.getOutputStream());
		this.in = new ObjectInputStream(clientSocket.getInputStream());
		this.sessionManager = sessionManager;
		this.context = context;
	}

	@Override
	public void run() {
		logger.info("Client connected");

		try {
			Request request;

			// Loop connection (ex.: Connect → Login → Logout)
			while (true) {
				try {
					// Block for Login or Register request
					// Need to be logged in to access other requests
					while (email == null) {
						request = (Request) in.readObject();
						logger.info("Request: {}", request);
						if (request instanceof Login) {
							Response response = request.execute(context);
							if (!response.isSuccess()) {
								out.writeObject(response);
								return;
							} else {
								email = ((Login) request).email();
								out.writeObject(response);
								sessionManager.addSession(email, this);
							}
						} else if (request instanceof Register) {
							Response response = request.execute(context);
							out.writeObject(response);
						} else {
							logger.info("Client not logged in");
							Response response = new Response(false, "Not logged in");
							out.writeObject(response);
							return;
						}
					}

					clientSocket.setSoTimeout(0); // "Disable" timeout

					// Main loop
					while (!clientSocket.isClosed()) {
						request = (Request) in.readObject();
						logger.info("({}) request: {}", email, request);
						Response response = request.execute(context);
						logger.info("({}) response: {}", email, response);
						out.writeObject(response);

						if (request instanceof Logout)
							break;
					}

					sessionManager.removeSession(email, this);
					email = null;
				} catch ( ClassNotFoundException e ) {
					logger.error("ClassNotFoundException: {}", e.getMessage());
				}
			}
		} catch ( SocketException e ) {
			logger.error("SocketException: {}", e.getMessage());
		} catch ( IOException e ) {
			logger.error("IOException: {}", e.getMessage());
		} finally {
			logger.info("Client disconnected");
			if (email != null)
				sessionManager.removeSession(email, this);
			try {
				clientSocket.close();
			} catch ( IOException e ) {
				logger.error("Closing socket: {}", e.getMessage());
			}
		}
	}

	public void sendMessage(NotificaionResponse notification) throws IOException {
		logger.debug("Sending notification to user: {}", notification);
		out.writeObject(notification);
		out.flush();
	}

	@Override
	public String toString() {
		return "ClientHandler@" + hashCode();
	}
}

