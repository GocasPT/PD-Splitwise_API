package pt.isec.pd.splitwise.server.RMI;

import pt.isec.pd.splitwise.sharedLib.database.DataBaseManager;
import pt.isec.pd.splitwise.sharedLib.rmi.OberserverServiceInterface;
import pt.isec.pd.splitwise.sharedLib.rmi.SplitwiseServiceInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RMIService extends UnicastRemoteObject implements SplitwiseServiceInterface {
	private final DataBaseManager dbManager;
	private final List<OberserverServiceInterface> observers;

	public RMIService(DataBaseManager dbManager) throws RemoteException {
		this.dbManager = dbManager;
		this.observers = new ArrayList<>();

		//TODO: add listeners for database changes (registe, login, insert expense, delete expense)

		//TODO: registe RMI service
	}

	@Override public List<String> getUsers() throws RemoteException {
		//TODO: use GetUsers request
		return Arrays.asList("user1", "user2", "user3");
	}

	@Override public List<String> getGroups() throws RemoteException {
		//TODO: use GetGroups request
		return Arrays.asList("group1", "group2", "group3");
	}

	@Override public void addObserver(OberserverServiceInterface observer) throws RemoteException {
		observers.add(observer);
	}

	@Override public void removeObserver(OberserverServiceInterface observer) throws RemoteException {
		observers.remove(observer);
	}

	//TODO: pass registered user
	private void onRegiste() throws RemoteException {
		for (OberserverServiceInterface observer : observers)
			observer.onRegiste();
	}

	//TODO: pass logged in user
	private void onLogin() throws RemoteException {
		for (OberserverServiceInterface observer : observers)
			observer.onLogin();
	}

	//TODO: pass inserted expense
	private void onInsertExpense() throws RemoteException {
		for (OberserverServiceInterface observer : observers)
			observer.onInsertExpense();
	}

	//TODO: pass deleted expense
	private void onDeleteExpense() throws RemoteException {
		for (OberserverServiceInterface observer : observers)
			observer.onDeleteExpense();
	}
}
