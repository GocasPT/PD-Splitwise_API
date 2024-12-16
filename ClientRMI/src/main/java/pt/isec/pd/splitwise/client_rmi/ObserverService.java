package pt.isec.pd.splitwise.client_rmi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.isec.pd.splitwise.sharedLib.rmi.ObserverServiceInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ObserverService extends UnicastRemoteObject implements ObserverServiceInterface {
	private static final Logger logger = LoggerFactory.getLogger(ObserverService.class);

	public ObserverService() throws RemoteException {
		super();
	}

	@Override
	public void onRegiste(String email) throws RemoteException {
		logger.info("User {} registered", email);
	}

	@Override
	public void onLogin(String email) throws RemoteException {
		logger.info("User {} logged in", email);
	}

	@Override
	public void onInsertExpense(String email, double amount) throws RemoteException {
		logger.info("User {} inserted expense of {}", email, amount);
	}

	@Override
	public void onDeleteExpense(int id) throws RemoteException {
		logger.info("Expense {} deleted", id);
	}
}
