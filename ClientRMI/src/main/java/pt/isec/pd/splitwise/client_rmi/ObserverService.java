package pt.isec.pd.splitwise.client_rmi;

import pt.isec.pd.splitwise.sharedLib.rmi.OberserverServiceInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ObserverService extends UnicastRemoteObject implements OberserverServiceInterface {
	protected ObserverService() throws RemoteException {}

	@Override public void onRegiste() throws RemoteException {
		//TODO: print message
	}

	@Override public void onLogin() throws RemoteException {
		//TODO: print message
	}

	@Override public void onInsertExpense() throws RemoteException {
		//TODO: print message
	}

	@Override public void onDeleteExpense() throws RemoteException {
		//TODO: print message
	}
}
