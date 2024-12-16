package pt.isec.pd.splitwise.server.RMI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;
import pt.isec.pd.splitwise.sharedLib.database.Entity.Group;
import pt.isec.pd.splitwise.sharedLib.database.Entity.User;
import pt.isec.pd.splitwise.sharedLib.database.Observer.RMIObserver;
import pt.isec.pd.splitwise.sharedLib.rmi.ObserverServiceInterface;
import pt.isec.pd.splitwise.sharedLib.rmi.SplitwiseServiceInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RMIService extends UnicastRemoteObject implements SplitwiseServiceInterface, RMIObserver {
	private static final Logger logger = LoggerFactory.getLogger(RMIService.class);

	private final DataBaseManager dbManager;

	private final List<ObserverServiceInterface> observers;

	public RMIService(DataBaseManager dbManager) throws RemoteException {
		this.dbManager = dbManager;
		this.observers = new ArrayList<>();
		dbManager.addRMIChangeObserver(this);
	}

	@Override
	public List<String> getUsers() throws RemoteException {
		try {
			List<User> users = dbManager.getUserDAO().getAllUsers();
			return users.stream().map(User::getEmail).toList();
		} catch ( SQLException e ) {
			logger.error("RMIService.getUsers: {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<String> getGroups() throws RemoteException {
		try {
			List<Group> groups = dbManager.getGroupDAO().getAllGroups();
			return groups.stream().map(Group::getName).toList();
		} catch ( SQLException e ) {
			logger.error("RMIService.getGroups: " + e.getMessage());
			throw new RuntimeException(e);
		}
	}

	@Override
	public void addObserver(ObserverServiceInterface observer) throws RemoteException {
		logger.info("Adding RMIObserver {} ", observer);
		observers.add(observer);
	}

	@Override
	public void removeObserver(ObserverServiceInterface observer) throws RemoteException {
		logger.info("Removing RMIObserver {} ", observer);
		observers.remove(observer);
	}

	@Override
	public void onRegiste(String email) {
		logger.debug("Notifying RMIObservers onRegiste");

		for (ObserverServiceInterface observer : observers)
			try {
				observer.onRegiste(email);
			} catch ( RemoteException e ) {
				logger.error("The RMIObserver {} is not reachable. Removing...", observer);
				observers.remove(observer);
			}
	}

	@Override
	public void onLogin(String email) {
		logger.debug("Notifying RMIObservers onLogin");

		for (ObserverServiceInterface observer : observers)
			try {
				observer.onLogin(email);
			} catch ( RemoteException e ) {
				logger.error("The RMIObserver {} is not reachable. Removing...", observer);
				observers.remove(observer);
			}
	}

	@Override
	public void onInsertExpense(String email, double amount) {
		logger.debug("Notifying RMIObservers onInsertExpense");

		for (ObserverServiceInterface observer : observers)
			try {
				observer.onInsertExpense(email, amount);
			} catch ( RemoteException e ) {
				logger.error("The RMIObserver {} is not reachable. Removing...", observer);
				observers.remove(observer);
			}
	}

	@Override
	public void onDeleteExpense(int id) {
		logger.debug("Notifying RMIObservers onDeleteExpense");

		for (ObserverServiceInterface observer : observers)
			try {
				observer.onDeleteExpense(id);
			} catch ( RemoteException e ) {
				logger.error("The RMIObserver {} is not reachable. Removing...", observer);
				observers.remove(observer);
			}
	}
}
