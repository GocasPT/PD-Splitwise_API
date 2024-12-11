package pt.isec.pd.splitwise.client_rmi;

import pt.isec.pd.splitwise.sharedLib.rmi.ObserverServiceInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ObserverService extends UnicastRemoteObject implements ObserverServiceInterface {

	public ObserverService() throws RemoteException {}

	@Override public void onRegiste(String email) throws RemoteException {
		//TODO: print message
		System.out.println("User " + email + " registered");
	}

	@Override public void onLogin(String email) throws RemoteException {
		//TODO: print message
		System.out.println("User " + email + " logged in");
	}

	@Override public void onInsertExpense(String email, double amount) throws RemoteException {
		//TODO: print message
		System.out.println("User " + email + " inserted expense of " + amount);
	}

	@Override public void onDeleteExpense(int id) throws RemoteException {
		//TODO: print message
		System.out.println("Expense " + id + " deleted");
	}
}
