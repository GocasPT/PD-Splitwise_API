package pt.isec.pd.splitwise.client.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import lombok.Setter;
import pt.isec.pd.splitwise.sharedLib.network.request.Request;
import pt.isec.pd.splitwise.sharedLib.network.response.Response;

import java.io.IOException;
import java.net.InetAddress;

public class ModelManager {
	private final SocketManager socketManager;
	@Getter private final ObjectProperty<ENavBarState> navBarStateProperty;

	@Setter
	@Getter
	private String emailLoggedUser;

	@Setter
	@Getter
	private int groupInViewId;

	@Setter
	@Getter
	private int expenseInViewId;

	public ModelManager() {
		socketManager = new SocketManager();
		navBarStateProperty = new SimpleObjectProperty<>(ENavBarState.NULL);
	}

	public void connect(InetAddress serverAdder, int port) {
		try {
			socketManager.connect(serverAdder, port);
		} catch ( IOException e ) { //TODO: Improve this exception handling
			throw new RuntimeException(e);
		}
	}

	public void close() {
		try {
			socketManager.close();
		} catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}

	//TODO: improve this method
	// - throw exceptions and handle them on GUI
	public Response sendRequest(Request request) {
		try {
			return socketManager.sendRequest(request);
		} catch ( IOException e ) {
			System.err.println("IOException while sending request: " + e.getMessage());
		} catch ( InterruptedException e ) {
			System.err.println("InterruptedException while sending request: " + e.getMessage());
		} catch ( Exception e ) {
			System.err.println("Unexpected exception while sending request: " + e.getMessage());
		}

		return null;
	}

	public ENavBarState getNavBarState() {
		return navBarStateProperty.get();
	}

	public void setNavBarState(ENavBarState state) {
		navBarStateProperty.set(state);
	}
}
