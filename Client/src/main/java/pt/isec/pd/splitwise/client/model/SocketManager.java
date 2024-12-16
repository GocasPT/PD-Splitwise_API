package pt.isec.pd.splitwise.client.model;

import javafx.application.Platform;
import pt.isec.pd.splitwise.client.ui.manager.NotificationManager;
import pt.isec.pd.splitwise.sharedLib.network.request.Request;
import pt.isec.pd.splitwise.sharedLib.network.response.NotificaionResponse;
import pt.isec.pd.splitwise.sharedLib.network.response.Response;

import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

public class SocketManager {
	private final Object lock = new Object();

	private Socket socket;

	private ObjectInputStream input;

	private ObjectOutputStream output;

	private Response feedbackResponse;

	private Thread listenerThread;

	public void connect(InetAddress serverAdder, int port) throws IOException { //TODO: add other exceptions
		socket = new Socket(serverAdder, port);
		output = new ObjectOutputStream(socket.getOutputStream());
		input = new ObjectInputStream(socket.getInputStream());

		listenerThread = new Thread(this::listenForMessages);
		listenerThread.start();
	}

	//TODO: improve catch blocks
	// - catch blocks (show error message on MainGUI/Popup)
	// - throw exception
	private void listenForMessages() {
		try {
			while (socket != null && !socket.isClosed()) {
				Object readObject = input.readObject();
				if (readObject instanceof Response response) synchronized (lock) {
					if (!(response instanceof NotificaionResponse notificationResponse)) {
						feedbackResponse = response;
						lock.notify();
					} else Platform.runLater(() -> NotificationManager.showNotification(notificationResponse));

				}
			}
		} catch ( SocketException e ) {
			System.out.println("SocketException on 'listenForMessages': " + e.getMessage());
		} catch ( ClassNotFoundException e ) {
			System.out.println("ClassNotFoundException on 'listenForMessages': " + e.getMessage());
		} catch ( InvalidClassException e ) {
			System.out.println("InvalidClassException on 'listenForMessages': " + e.getMessage());
		} catch ( IOException e ) {
			System.out.println("IOException on 'listenForMessages': " + e.getMessage());
		} finally {
			//Platform.runLater(() -> ); //TODO: show error message
			Platform.exit();
		}
	}

	public void close() throws IOException {
		if (socket != null && !socket.isClosed()) socket.close();
		if (listenerThread != null && listenerThread.isAlive()) listenerThread.interrupt();
	}

	public Response sendRequest(Request request) throws IOException, InterruptedException {
		synchronized (lock) {
			output.writeObject(request);
			output.flush();

			lock.wait();
			return feedbackResponse;
		}
	}
}
