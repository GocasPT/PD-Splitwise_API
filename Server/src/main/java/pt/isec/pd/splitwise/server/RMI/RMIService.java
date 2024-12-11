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
import java.util.Arrays;
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

	@Override public List<String> getUsers() throws RemoteException {
		logger.info("RMIService.getUsers");

		try {
			List<User> users = dbManager.getUserDAO().getAllUsers();
			return users.stream().map(User::getEmail).toList();
		} catch ( SQLException e ) {
			logger.error("RMIService.getUsers: " + e.getMessage());
			throw new RuntimeException(e);
		}
	}

	@Override public List<String> getGroups() throws RemoteException {
		logger.info("RMIService.getGroups");

		try {
			List<Group> groups = dbManager.getGroupDAO().getAllGroups();
			return groups.stream().map(Group::getName).toList();
		} catch ( SQLException e ) {
			logger.error("RMIService.getGroups: " + e.getMessage());
			throw new RuntimeException(e);
		}
	}

	@Override public void addObserver(ObserverServiceInterface observer) throws RemoteException {
		logger.info("RMIService.addObserver: observer: " + observer);

		observers.add(observer);
	}

	@Override public void removeObserver(ObserverServiceInterface observer) throws RemoteException {
		logger.info("RMIService.removeObserver: observer: " + observer);

		observers.remove(observer);
	}

	//TODO: pass registered user
	@Override public void onRegiste(String email) throws RemoteException {
		logger.debug("RMIService.onRegiste");

		for (ObserverServiceInterface observer : observers)
			observer.onRegiste(email);
	}

	//TODO: pass logged in user
	@Override public void onLogin(String email) throws RemoteException {
		logger.debug("RMIService.onLogin");

		for (ObserverServiceInterface observer : observers)
			observer.onLogin(email);
	}

	//TODO: pass inserted expense
	@Override public void onInsertExpense(String email, double amount) throws RemoteException {
		logger.debug("RMIService.onInsertExpense");

		for (ObserverServiceInterface observer : observers)
			observer.onInsertExpense(email, amount);
	}

	//TODO: pass deleted expense
	@Override public void onDeleteExpense(int id) throws RemoteException {
		logger.debug("RMIService.onDeleteExpense");

		for (ObserverServiceInterface observer : observers)
			observer.onDeleteExpense(id);
	}
}
